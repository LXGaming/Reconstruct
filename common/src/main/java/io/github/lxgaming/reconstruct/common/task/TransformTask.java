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
import io.github.lxgaming.reconstruct.common.entity.Transform;
import io.github.lxgaming.reconstruct.common.manager.TransformerManager;
import io.github.lxgaming.reconstruct.common.util.Toolbox;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.function.Consumer;

public class TransformTask extends Task {
    
    private final WriteTask writeTask;
    private final String name;
    private final byte[] bytes;
    private final Consumer<Task> consumer;
    
    public TransformTask(WriteTask writeTask, String name, byte[] bytes, Consumer<Task> consumer) {
        this.writeTask = writeTask;
        this.name = name;
        this.bytes = bytes;
        this.consumer = consumer;
    }
    
    @Override
    public boolean prepare() {
        type(Type.DEFAULT);
        return true;
    }
    
    @Override
    public void execute() throws Exception {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        
        Transform transform = new Transform();
        transform.setClassName(name);
        transform.setClassVisitor(classWriter);
        
        if (TransformerManager.execute(transform)) {
            classReader.accept(transform.getClassVisitor(), 0);
            Reconstruct.getInstance().getLogger().info("Transformed {} -> {}", name, transform.getClassName());
            writeTask.queue(Toolbox.toFileName(transform.getClassName()), classWriter.toByteArray());
        } else {
            writeTask.queue(Toolbox.toFileName(name), bytes);
        }
        
        consumer.accept(this);
    }
}