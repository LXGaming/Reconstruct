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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodVisitorImpl extends MethodVisitor {

    private final boolean isStatic;
    private final int parameterTotal;
    private int parameterIndex;

    public MethodVisitorImpl(MethodVisitor methodVisitor, boolean isStatic, int parameterTotal) {
        super(Opcodes.ASM9, methodVisitor);
        this.isStatic = isStatic;
        this.parameterTotal = parameterTotal;
    }

    @Override
    public void visitParameter(String name, int access) {
        if (isValidJavaIdentifier(name)) {
            super.visitParameter(name, access);
        } else {
            super.visitParameter("param" + parameterIndex, access);
        }

        parameterIndex += 1;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        int parameters = isStatic ? parameterTotal : parameterTotal + 1;
        if (isValidJavaIdentifier(name)) {
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
        } else if (index == 0 && !isStatic) {
            super.visitLocalVariable("this", descriptor, signature, start, end, index);
        } else if (index < parameters) {
            super.visitLocalVariable("param" + (index - (isStatic ? 0 : 1)), descriptor, signature, start, end, index);
        } else {
            super.visitLocalVariable("var" + index, descriptor, signature, start, end, index);
        }
    }

    protected boolean isValidJavaIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }

        for (int index = 0; index < name.length(); index++) {
            if (!Character.isJavaIdentifierPart(name.charAt(index))) {
                return false;
            }
        }

        return true;
    }
}
