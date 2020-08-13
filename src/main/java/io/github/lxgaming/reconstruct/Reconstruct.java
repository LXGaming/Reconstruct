/*
 * Copyright 2019 Alex Thomson
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

package io.github.lxgaming.reconstruct;

import io.github.lxgaming.reconstruct.bytecode.Attribute;
import io.github.lxgaming.reconstruct.bytecode.Attributes;
import io.github.lxgaming.reconstruct.bytecode.RcClass;
import io.github.lxgaming.reconstruct.data.Transform;
import io.github.lxgaming.reconstruct.manager.TransformerManager;
import io.github.lxgaming.reconstruct.transformer.NameTransformer;
import io.github.lxgaming.reconstruct.transformer.ProGuardTransformer;
import io.github.lxgaming.reconstruct.util.ShutdownHook;
import io.github.lxgaming.reconstruct.util.StringUtils;
import io.github.lxgaming.reconstruct.util.Toolbox;
import io.github.lxgaming.reconstruct.util.jcommander.Arguments;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.objectweb.asm.ClassReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Reconstruct {
    
    public static final String ID = "reconstruct";
    public static final String NAME = "Reconstruct";
    public static final String VERSION = "@version@";
    public static final String AUTHORS = "LX_Gaming";
    public static final String SOURCE = "https://github.com/LXGaming/Reconstruct";
    public static final String WEBSITE = "https://lxgaming.github.io/";
    
    private static Reconstruct instance;
    private final Logger logger;
    private final Arguments arguments;
    private final Set<RcClass> classes;
    
    public Reconstruct() {
        instance = this;
        this.logger = LogManager.getLogger(Reconstruct.NAME);
        this.arguments = new Arguments();
        this.classes = Toolbox.newHashSet();
    }
    
    public void load() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        
        getLogger().info("{} v{}", Reconstruct.NAME, Reconstruct.VERSION);
        getLogger().info("Authors: {}", Reconstruct.AUTHORS);
        getLogger().info("Source: {}", Reconstruct.SOURCE);
        getLogger().info("Website: {}", Reconstruct.WEBSITE);
        
        if (arguments.isDebug()) {
            Configurator.setLevel(getLogger().getName(), Level.DEBUG);
            getLogger().debug("Debug mode enabled");
        } else {
            getLogger().info("Debug mode disabled");
        }
        
        if (System.console() == null && !System.getProperty("java.class.path").contains("idea_rt.jar")) {
            getLogger().error("Failed to detect Console");
            return;
        }
        
        if (arguments.getJarPath() == null || arguments.getMappingPath() == null || arguments.getOutputPath() == null) {
            getLogger().error("Invalid arguments");
            return;
        }
        
        if (!getArguments().isAgree()) {
            getLogger().warn("========================== WARNING ==========================");
            getLogger().warn("  Do not distribute confidential or proprietary information  ");
            getLogger().warn("unless you have explicit permission from the copyright holder");
            getLogger().warn("========================== WARNING ==========================");
            
            Toolbox.readline("Press Enter to continue...");
        }
        
        ProGuardTransformer proGuardTransformer = new ProGuardTransformer(arguments.getMappingPath());
        if (!TransformerManager.registerTransformer(proGuardTransformer)) {
            return;
        }
        
        NameTransformer nameTransformer = new NameTransformer();
        if (!TransformerManager.registerTransformer(nameTransformer)) {
            return;
        }
        
        if (!link(getArguments().getJarPath())) {
            return;
        }
        
        transform(arguments.getJarPath(), arguments.getOutputPath());
    }
    
    private boolean link(Path inputPath) {
        try (JarFile jarFile = new JarFile(inputPath.toString(), false)) {
            for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements(); ) {
                JarEntry jarEntry = enumeration.nextElement();
                if (!jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                
                try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    String name = Toolbox.fromFileName(jarEntry.getName());
                    if (StringUtils.startsWith(getArguments().getExcludedPackages(), name)) {
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
        try (JarFile jarFile = new JarFile(inputPath.toFile(), false)) {
            try (JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(outputPath.toFile()))) {
                outputStream.setMethod(ZipOutputStream.STORED);
                for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements(); ) {
                    JarEntry jarEntry = enumeration.nextElement();
                    if (jarEntry.isDirectory()) {
                        continue;
                    }
                    
                    if (jarEntry.getName().startsWith("META-INF/")) {
                        // Exclude code signing certificates
                        if (jarEntry.getName().endsWith(".DSA") || jarEntry.getName().endsWith(".RSA") || jarEntry.getName().endsWith(".SF")) {
                            continue;
                        }
                        
                        if (jarEntry.getName().endsWith("MANIFEST.MF")) {
                            try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                                Manifest manifest = new Manifest(inputStream);
                                manifest.getMainAttributes().putValue(Reconstruct.NAME, Reconstruct.VERSION);
                                // Exclude code signing digests
                                manifest.getEntries().clear();
                                writeZipEntry(getByteArray(manifest), outputStream, jarEntry.getName());
                                continue;
                            }
                        }
                    }
                    
                    try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        if (!jarEntry.getName().endsWith(".class")) {
                            writeZipEntry(inputStream, outputStream, jarEntry);
                            continue;
                        }
                        
                        String name = Toolbox.fromFileName(jarEntry.getName());
                        if (StringUtils.startsWith(getArguments().getExcludedPackages(), name)) {
                            getLogger().debug("Skipping {}", name);
                            writeZipEntry(inputStream, outputStream, jarEntry);
                            continue;
                        }
                        
                        ClassReader classReader = new ClassReader(inputStream);
                        
                        Transform transform = new Transform();
                        transform.setClassReader(classReader);
                        transform.setClassName(name);
                        
                        if (!TransformerManager.process(transform)) {
                            writeZipEntry(inputStream, outputStream, jarEntry);
                            continue;
                        }
                        
                        getLogger().info("Transformed {} -> {}", name, transform.getClassName());
                        writeZipEntry(transform.getClassWriter().toByteArray(), outputStream, Toolbox.toFileName(transform.getClassName()));
                    }
                }
            }
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while transforming", ex);
        }
    }
    
    private byte[] getByteArray(Manifest manifest) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            manifest.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private void writeZipEntry(byte[] bytes, ZipOutputStream outputStream, String name) throws Exception {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        
        ZipEntry zipEntry = new ZipEntry(name);
        zipEntry.setSize(bytes.length);
        zipEntry.setCompressedSize(bytes.length);
        zipEntry.setCrc(crc.getValue());
        
        crc.reset();
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            writeZipEntry(inputStream, outputStream, zipEntry);
        }
    }
    
    private void writeZipEntry(InputStream inputStream, ZipOutputStream outputStream, ZipEntry zipEntry) throws Exception {
        outputStream.putNextEntry(zipEntry);
        
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        
        outputStream.closeEntry();
    }
    
    public static Reconstruct getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Arguments getArguments() {
        return arguments;
    }
    
    public RcClass getOrCreateClass(String name) {
        return getClass(name).orElseGet(() -> {
            RcClass rcClass = new RcClass();
            rcClass.setName(name);
            rcClass.update();
            classes.add(rcClass);
            return rcClass;
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
        List<RcClass> classes = Toolbox.newArrayList();
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
}