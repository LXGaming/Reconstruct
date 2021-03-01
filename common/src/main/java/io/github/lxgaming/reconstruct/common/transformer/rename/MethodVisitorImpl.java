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

import io.github.lxgaming.reconstruct.common.util.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodVisitorImpl extends MethodVisitor {
    
    private final int arguments;
    private int parameters = 0;
    private int variables = 0;
    
    public MethodVisitorImpl(int arguments, MethodVisitor methodVisitor) {
        super(Opcodes.ASM7, methodVisitor);
        this.arguments = arguments;
    }
    
    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(getName(name), access);
    }
    
    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(getName(name), descriptor, signature, start, end, index);
    }
    
    private String getName(String name) {
        if (StringUtils.isBlank(name)) {
            return getName();
        }
        
        if (name.equals("this")) {
            return name;
        }
        
        if (!name.equals(StringUtils.filter(name))) {
            return getName();
        }
        
        return name;
    }
    
    private String getName() {
        String name;
        if (arguments > parameters) {
            name = "param_" + parameters;
            parameters += 1;
        } else {
            name = "var_" + variables;
            variables += 1;
        }
        
        return name;
    }
}