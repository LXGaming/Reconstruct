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

package io.github.lxgaming.reconstruct.common.task;

import io.github.lxgaming.common.task.Task;
import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.entity.ByteArrayZipEntry;
import io.github.lxgaming.reconstruct.common.util.Toolbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WriteTask extends Task {
    
    private final Path path;
    private final Set<String> paths;
    private final BlockingQueue<ByteArrayZipEntry> queue;
    private final AtomicBoolean state;
    
    public WriteTask(Path path) {
        this.path = path;
        this.paths = new HashSet<>();
        this.queue = new LinkedBlockingQueue<>(250);
        this.state = new AtomicBoolean(false);
    }
    
    @Override
    public boolean prepare() {
        type(Type.DEFAULT);
        getState().set(true);
        return true;
    }
    
    @Override
    public void execute() throws Exception {
        try (JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(path.toFile()))) {
            outputStream.setMethod(ZipEntry.DEFLATED);
            
            while (!queue.isEmpty() || getState().get()) {
                ByteArrayZipEntry zipEntry = queue.poll(1L, TimeUnit.MILLISECONDS);
                if (zipEntry == null) {
                    continue;
                }
                
                int index = 0;
                while ((index = zipEntry.getName().indexOf('/', index)) != -1) {
                    String name = zipEntry.getName().substring(0, ++index);
                    if (paths.add(name)) {
                        outputStream.putNextEntry(new ZipEntry(name));
                        outputStream.closeEntry();
                    }
                }
                
                writeZipEntry(zipEntry, outputStream);
            }
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while writing to {}", path, ex);
        }
    }
    
    private void writeZipEntry(ByteArrayZipEntry zipEntry, ZipOutputStream outputStream) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(zipEntry.getBytes())) {
            outputStream.putNextEntry(zipEntry);
            Toolbox.transferBytes(inputStream, outputStream);
            outputStream.closeEntry();
        }
    }
    
    public void queue(ZipEntry zipEntry, InputStream inputStream) throws InterruptedException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Toolbox.transferBytes(inputStream, outputStream);
        queue(zipEntry, outputStream.toByteArray());
    }
    
    public void queue(ZipEntry zipEntry, byte[] bytes) throws InterruptedException {
        queue(new ByteArrayZipEntry(zipEntry, bytes));
    }
    
    public void queue(String string, byte[] bytes) throws InterruptedException {
        queue(new ByteArrayZipEntry(string, bytes));
    }
    
    public void queue(ByteArrayZipEntry zipEntry) throws InterruptedException {
        queue.put(zipEntry);
    }
    
    public AtomicBoolean getState() {
        return state;
    }
}