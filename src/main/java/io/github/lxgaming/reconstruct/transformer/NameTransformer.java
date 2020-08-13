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

import io.github.lxgaming.reconstruct.entity.Transform;
import io.github.lxgaming.reconstruct.transformer.rename.ClassVisitorImpl;
import org.objectweb.asm.ClassWriter;

public class NameTransformer implements Transformer {
    
    @Override
    public boolean prepare() {
        return true;
    }
    
    @Override
    public void execute(Transform transform) throws Exception {
        transform.setClassWriter(new ClassWriter(0));
        ClassVisitorImpl classVisitor = new ClassVisitorImpl(transform.getClassWriter());
        transform.getClassReader().accept(classVisitor, 0);
        transform.getClassWriter().visitEnd();
    }
}