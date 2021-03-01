/*
 * Copyright 2020 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.reconstruct.common;

import io.github.lxgaming.common.task.Task;
import io.github.lxgaming.reconstruct.common.bytecode.Attribute;
import io.github.lxgaming.reconstruct.common.bytecode.Attributes;
import io.github.lxgaming.reconstruct.common.bytecode.RcArray;
import io.github.lxgaming.reconstruct.common.bytecode.RcClass;
import io.github.lxgaming.reconstruct.common.configuration.Config;
import io.github.lxgaming.reconstruct.common.manager.TaskManager;
import io.github.lxgaming.reconstruct.common.manager.TransformerManager;
import io.github.lxgaming.reconstruct.common.task.TransformTask;
import io.github.lxgaming.reconstruct.common.task.WriteTask;
import io.github.lxgaming.reconstruct.common.util.StringUtils;
import io.github.lxgaming.reconstruct.common.util.Toolbox;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Reconstruct {
    
    public static final String ID = "reconstruct";
    public static final String NAME = "Reconstruct";
    public static final String VERSION = "@version@";
    public static final String AUTHORS = "LX_Gaming";
    public static final String SOURCE = "https://github.com/LXGaming/Reconstruct";
    public static final String WEBSITE = "https://lxgaming.github.io/";
    
    private static Reconstruct instance;
    private final Logger logger;
    private final Config config;
    private final Set<RcClass> classes;
    private final AtomicBoolean state;
    private final List<Task> tasks;
    
    public Reconstruct(Config config) {
        instance = this;
        this.logger = LoggerFactory.getLogger(Reconstruct.NAME);
        this.config = config;
        this.classes = new HashSet<>();
        this.state = new AtomicBoolean(false);
        this.tasks = new CopyOnWriteArrayList<>();
    }
    
    public void load() {
        if (getConfig().getJarPath() == null || getConfig().getMappingPath() == null || getConfig().getOutputPath() == null) {
            getLogger().error("Invalid arguments");
            return;
        }
        
        if (getConfig().getThreads() <= 0) {
            getLogger().warn("Threads is out of bounds. Resetting to {}", Runtime.getRuntime().availableProcessors());
            getConfig().setThreads(Runtime.getRuntime().availableProcessors());
        }
        
        getState().set(true);
        
        TaskManager.prepare();
        TransformerManager.prepare();
        
        if (!link(getConfig().getJarPath())) {
            return;
        }
        
        transform(getConfig().getJarPath(), getConfig().getOutputPath());
    }
    
    public void shutdown() {
        getState().set(false);
        TaskManager.shutdown();
        instance = null;
    }
    
    private boolean link(Path path) {
        try (JarFile jarFile = new JarFile(path.toFile(), false)) {
            for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements(); ) {
                JarEntry jarEntry = enumeration.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                
                try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    String name = Toolbox.fromFileName(jarEntry.getName());
                    if (StringUtils.startsWith(getConfig().getExcludedPackages(), name)) {
                        getLogger().debug("Skipping {}", name);
                        continue;
                    }
                    
                    ClassReader classReader = new ClassReader(inputStream);
                    String className = Toolbox.toJavaName(classReader.getClassName());
                    String superName = Toolbox.toJavaName(Objects.toString(classReader.getSuperName(), "java/lang/Object"));
                    
                    RcClass rcClass = getClass(className, Attributes.OBFUSCATED_NAME).orElseGet(() -> getClass(className).orElse(null));
                    if (rcClass == null) {
                        getLogger().warn("Missing {}", className);
                        continue;
                    }
                    
                    rcClass.setModifiers(classReader.getAccess());
                    
                    if (!superName.equals("java.lang.Object")) {
                        RcClass superRcClass = getClass(superName, Attributes.OBFUSCATED_NAME).orElseGet(() -> getClass(superName).orElse(null));
                        if (superRcClass != null) {
                            rcClass.getClasses().add(superRcClass);
                        }
                    }
                    
                    for (String internalInterfaceName : classReader.getInterfaces()) {
                        String interfaceName = Toolbox.toJavaName(internalInterfaceName);
                        RcClass interfaceRcClass = getClass(interfaceName, Attributes.OBFUSCATED_NAME).orElseGet(() -> getClass(interfaceName).orElse(null));
                        if (interfaceRcClass != null) {
                            rcClass.getClasses().add(interfaceRcClass);
                        }
                    }
                }
            }
            
            getLogger().info("Linked {} classes", getClasses().size());
            return true;
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while preparing", ex);
            return false;
        }
    }
    
    private void transform(Path inputPath, Path outputPath) {
        WriteTask writeTask = new WriteTask(outputPath);
        TaskManager.schedule(writeTask);
        
        try (JarFile jarFile = new JarFile(inputPath.toFile(), false)) {
            for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements(); ) {
                // Shutting down
                if (!getState().get()) {
                    break;
                }
                
                JarEntry jarEntry = enumeration.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                
                if (jarEntry.getName().startsWith("META-INF/")) {
                    // Exclude code signing certificates
                    if (jarEntry.getName().endsWith(".DSA") || jarEntry.getName().endsWith(".RSA") || jarEntry.getName().endsWith(".SF")) {
                        continue;
                    }
                    
                    // Modify the Manifest
                    if (jarEntry.getName().endsWith("MANIFEST.MF")) {
                        try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                            Manifest manifest = new Manifest(inputStream);
                            manifest.getMainAttributes().putValue(Reconstruct.NAME, Reconstruct.VERSION);
                            // Exclude code signing digests
                            manifest.getEntries().clear();
                            writeTask.queue(jarEntry, getByteArray(manifest));
                            continue;
                        }
                    }
                }
                
                try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    if (!jarEntry.getName().endsWith(".class")) {
                        writeTask.queue(jarEntry, inputStream);
                        continue;
                    }
                    
                    String name = Toolbox.fromFileName(jarEntry.getName());
                    if (StringUtils.startsWith(getConfig().getExcludedPackages(), name)) {
                        getLogger().debug("Skipping {}", name);
                        writeTask.queue(jarEntry, inputStream);
                        continue;
                    }
                    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    Toolbox.transferBytes(inputStream, outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    
                    TransformTask transformTask = new TransformTask(writeTask, name, bytes, tasks::remove);
                    if (getConfig().getThreads() != 1) {
                        tasks.add(transformTask);
                        TaskManager.schedule(transformTask);
                    } else {
                        transformTask.execute();
                    }
                }
            }
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while transforming", ex);
        }
        
        Reconstruct.getInstance().getLogger().info("Waiting for TransformTasks to complete...");
        for (Task task : tasks) {
            task.await();
        }
        
        Reconstruct.getInstance().getLogger().info("Waiting for WriteTask to complete...");
        writeTask.getState().set(false);
        writeTask.await();
    }
    
    private byte[] getByteArray(Manifest manifest) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            manifest.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public static Reconstruct getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Config getConfig() {
        return config;
    }
    
    public RcClass getOrCreateClass(String name) {
        return getClass(name).orElseGet(() -> {
            RcClass object;
            if (name.endsWith("[]")) {
                RcClass type = getOrCreateClass(name.substring(0, name.length() - 2));
                object = new RcArray();
                ((RcArray) object).setType(type);
            } else {
                object = new RcClass();
            }
            
            object.setName(name);
            object.update();
            classes.add(object);
            return object;
        });
    }
    
    public Optional<RcClass> getClass(String name, Attribute.Key<String> attribute) {
        return Optional.ofNullable(getClass(rcClass -> rcClass.getAttribute(attribute).map(name::equals).orElse(false)));
    }
    
    public Optional<RcClass> getClass(String name) {
        return Optional.ofNullable(getClass(rcClass -> rcClass.getName().equals(name)));
    }
    
    public RcClass getClass(Predicate<RcClass> predicate) {
        return Toolbox.getFirst(getClasses(predicate));
    }
    
    public List<RcClass> getClasses(Predicate<RcClass> predicate) {
        List<RcClass> classes = new ArrayList<>();
        for (RcClass rcClass : this.classes) {
            if (predicate.test(rcClass)) {
                classes.add(rcClass);
            }
        }
        
        return classes;
    }
    
    public Set<RcClass> getClasses() {
        return classes;
    }
    
    public AtomicBoolean getState() {
        return state;
    }
}