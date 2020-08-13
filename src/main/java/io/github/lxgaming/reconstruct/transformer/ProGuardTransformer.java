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

package io.github.lxgaming.reconstruct.transformer;

import io.github.lxgaming.reconstruct.Reconstruct;
import io.github.lxgaming.reconstruct.bytecode.Attributes;
import io.github.lxgaming.reconstruct.bytecode.RcClass;
import io.github.lxgaming.reconstruct.bytecode.RcConstructor;
import io.github.lxgaming.reconstruct.bytecode.RcField;
import io.github.lxgaming.reconstruct.bytecode.RcMethod;
import io.github.lxgaming.reconstruct.entity.Transform;
import io.github.lxgaming.reconstruct.transformer.proguard.MappingProcessorImpl;
import io.github.lxgaming.reconstruct.transformer.proguard.RemapperImpl;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import proguard.obfuscate.MappingReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProGuardTransformer implements Transformer {
    
    private final Path path;
    private final MappingProcessorImpl mappingProcessor;
    
    public ProGuardTransformer(Path path) {
        this.path = path;
        this.mappingProcessor = new MappingProcessorImpl();
    }
    
    @Override
    public boolean prepare() {
        if (!Files.isRegularFile(path)) {
            Reconstruct.getInstance().getLogger().error("Provided path is not a file");
            return false;
        }
        
        try {
            MappingReader mappingReader = new MappingReader(path.toFile());
            mappingReader.pump(mappingProcessor);
            
            int constructors = 0;
            int fields = 0;
            int methods = 0;
            for (RcClass currentClass : Reconstruct.getInstance().getClasses()) {
                for (RcConstructor constructor : currentClass.getConstructors()) {
                    constructor.update();
                    constructors++;
                }
                
                for (RcField field : currentClass.getFields()) {
                    field.update();
                    fields++;
                }
                
                for (RcMethod method : currentClass.getMethods()) {
                    method.update();
                    methods++;
                }
            }
            
            Reconstruct.getInstance().getLogger().info("Mapped:");
            Reconstruct.getInstance().getLogger().info(" - {} Classes", Reconstruct.getInstance().getClasses().size());
            Reconstruct.getInstance().getLogger().info(" - {} Constructors", constructors);
            Reconstruct.getInstance().getLogger().info(" - {} Fields", fields);
            Reconstruct.getInstance().getLogger().info(" - {} Methods", methods);
            return true;
        } catch (IOException ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while mapping {}", path.getFileName(), ex);
            return false;
        }
    }
    
    @Override
    public void execute(Transform transform) throws Exception {
        RemapperImpl remapper = new RemapperImpl();
        RcClass currentClass = remapper.getClass(transform.getClassName(), Attributes.OBFUSCATED_NAME);
        if (currentClass != null) {
            transform.setClassName(currentClass.getName());
        }
        
        transform.setClassWriter(new ClassWriter(0));
        ClassRemapper classRemapper = new ClassRemapper(transform.getClassWriter(), remapper);
        transform.getClassReader().accept(classRemapper, 0);
        transform.getClassWriter().visitEnd();
    }
}