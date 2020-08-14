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

package io.github.lxgaming.reconstruct.common.transformer.proguard;

import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.bytecode.Attributes;
import io.github.lxgaming.reconstruct.common.bytecode.RcClass;
import io.github.lxgaming.reconstruct.common.bytecode.RcConstructor;
import io.github.lxgaming.reconstruct.common.bytecode.RcField;
import io.github.lxgaming.reconstruct.common.bytecode.RcMethod;
import proguard.obfuscate.MappingProcessor;

public class MappingProcessorImpl implements MappingProcessor {
    
    private RcClass currentClass;
    
    @Override
    public boolean processClassMapping(String className, String newClassName) {
        currentClass = Reconstruct.getInstance().getOrCreateClass(className);
        currentClass.setAttribute(Attributes.OBFUSCATED_NAME, newClassName);
        currentClass.update();
        return true;
    }
    
    @Override
    public void processFieldMapping(String className, String fieldType, String fieldName, String newClassName, String newFieldName) {
        if (currentClass == null) {
            return;
        }
        
        if (!className.equals(newClassName)) {
            Reconstruct.getInstance().getLogger().warn("Field {} has an explicit original class name. As this is not supported {} will be used instead of {}", fieldName, newClassName, className);
        }
        
        RcField field = new RcField();
        field.setName(fieldName);
        
        RcClass type = Reconstruct.getInstance().getOrCreateClass(fieldType);
        field.setType(type);
        field.setAttribute(Attributes.OBFUSCATED_NAME, newFieldName);
        if (!currentClass.getFields().add(field)) {
            Reconstruct.getInstance().getLogger().warn("Duplicate field {} for class {}", field.getName(), currentClass.getName());
        }
    }
    
    @Override
    public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType,
                                     String methodName, String methodArguments, String newClassName, int newFirstLineNumber,
                                     int newLastLineNumber, String newMethodName) {
        if (currentClass == null) {
            return;
        }
        
        if (!className.equals(newClassName)) {
            Reconstruct.getInstance().getLogger().warn("Method {} has an explicit original class name. As this is not supported {} will be used instead of {}", methodName, newClassName, className);
        }
        
        if (methodName.equals(RcConstructor.CONSTRUCTOR_NAME) || methodName.equals(RcConstructor.STATIC_INITIALIZER_NAME)) {
            RcConstructor constructor = new RcConstructor();
            constructor.setName(methodName);
            if (!methodArguments.isEmpty()) {
                for (String argument : methodArguments.split(",")) {
                    RcClass parameter = Reconstruct.getInstance().getOrCreateClass(argument);
                    constructor.getParameters().add(parameter);
                }
            }
            
            constructor.setAttribute(Attributes.OBFUSCATED_NAME, newMethodName);
            constructor.setAttribute(Attributes.BEHAVIOR_FIRST_LINE_NUMBER, firstLineNumber);
            constructor.setAttribute(Attributes.BEHAVIOR_LAST_LINE_NUMBER, lastLineNumber);
            if (!currentClass.getConstructors().add(constructor)) {
                Reconstruct.getInstance().getLogger().warn("Duplicate constructor {} for class {}", constructor.getName(), currentClass.getName());
            }
        } else {
            RcMethod method = new RcMethod();
            method.setName(methodName);
            if (!methodArguments.isEmpty()) {
                for (String argument : methodArguments.split(",")) {
                    RcClass parameter = Reconstruct.getInstance().getOrCreateClass(argument);
                    method.getParameters().add(parameter);
                }
            }
            
            RcClass returnType = Reconstruct.getInstance().getOrCreateClass(methodReturnType);
            method.setReturnType(returnType);
            method.setAttribute(Attributes.OBFUSCATED_NAME, newMethodName);
            method.setAttribute(Attributes.BEHAVIOR_FIRST_LINE_NUMBER, firstLineNumber);
            method.setAttribute(Attributes.BEHAVIOR_LAST_LINE_NUMBER, lastLineNumber);
            if (!currentClass.getMethods().add(method)) {
                Reconstruct.getInstance().getLogger().warn("Duplicate method {} for class {}", method.getName(), currentClass.getName());
            }
        }
    }
}