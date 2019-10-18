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

package io.github.lxgaming.reconstruct.manager;

import io.github.lxgaming.reconstruct.Reconstruct;
import io.github.lxgaming.reconstruct.data.Transform;
import io.github.lxgaming.reconstruct.transformer.Transformer;
import io.github.lxgaming.reconstruct.util.Toolbox;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.Set;

public final class TransformerManager {
    
    private static final Set<Transformer> TRANSFORMERS = Toolbox.newLinkedHashSet();
    
    public static boolean process(Transform transform) {
        String className = transform.getClassName();
        for (Transformer transformer : TRANSFORMERS) {
            try {
                if (transform.getClassReader() != null && transform.getClassWriter() != null) {
                    ClassReader classReader = new ClassReader(transform.getClassWriter().toByteArray());
                    
                    transform.setClassReader(classReader);
                    transform.setClassWriter(null);
                }
                
                transformer.execute(transform);
            } catch (Exception ex) {
                Reconstruct.getInstance().getLogger().error("{} encountered an error while processing {}", transformer.getClass().getSimpleName(), className, ex);
                return false;
            }
        }
        
        if (transform.getClassReader() != null && transform.getClassWriter() == null) {
            ClassWriter classWriter = new ClassWriter(transform.getClassReader(), 0);
            transform.setClassWriter(classWriter);
        }
        
        return true;
    }
    
    public static boolean registerTransformer(Transformer transformer) {
        if (contains(transformer.getClass())) {
            Reconstruct.getInstance().getLogger().warn("{} has already been registered", transformer.getClass().getSimpleName());
            return false;
        }
        
        if (!transformer.prepare()) {
            Reconstruct.getInstance().getLogger().error("{} failed to prepare", transformer.getClass().getSimpleName());
            return false;
        }
        
        TRANSFORMERS.add(transformer);
        Reconstruct.getInstance().getLogger().debug("{} registered", transformer.getClass().getSimpleName());
        return true;
    }
    
    private static boolean contains(Class<? extends Transformer> transformerClass) {
        for (Transformer transformer : TRANSFORMERS) {
            if (transformer.getClass().equals(transformerClass)) {
                return true;
            }
        }
        
        return false;
    }
}