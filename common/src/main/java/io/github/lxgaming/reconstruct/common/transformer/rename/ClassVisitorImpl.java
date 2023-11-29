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

package io.github.lxgaming.reconstruct.common.transformer.rename;

import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.util.Toolbox;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassVisitorImpl extends ClassVisitor {

    private final String className;

    public ClassVisitorImpl(ClassVisitor classVisitor, String className) {
        super(Opcodes.ASM9, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor;
        try {
            methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while renaming {}.{}{} ({})", className, name, descriptor, signature, ex);
            return null;
        }

        if (methodVisitor == null) {
            return null;
        }

        int arguments = Toolbox.countArguments(Type.getMethodType(descriptor));
        return new MethodVisitorImpl(
                methodVisitor,
                (access & Opcodes.ACC_STATIC) != 0,
                arguments
        );
    }
}