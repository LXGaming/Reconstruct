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

package io.github.lxgaming.reconstruct.common.bytecode;

import io.github.lxgaming.reconstruct.common.util.Toolbox;

import java.util.Objects;

public final class RcMethod extends RcBehavior {
    
    private RcClass returnType;
    
    public void update() {
        setDescriptor(Toolbox.getMethodDescriptor(getName(), getParameters().stream().map(RcClass::getDescriptor).toArray(String[]::new), returnType.getDescriptor()));
        getAttribute(Attributes.OBFUSCATED_NAME).map(obfuscatedName -> {
            return Toolbox.getMethodDescriptor(obfuscatedName, getParameters().stream().map(parameter -> {
                return parameter.getAttribute(Attributes.OBFUSCATED_DESCRIPTOR).orElse(parameter.getDescriptor());
            }).toArray(String[]::new), returnType.getAttribute(Attributes.OBFUSCATED_DESCRIPTOR).orElse(returnType.getDescriptor()));
        }).ifPresent(descriptor -> {
            setAttribute(Attributes.OBFUSCATED_DESCRIPTOR, descriptor);
        });
    }
    
    public RcClass getReturnType() {
        return returnType;
    }
    
    public void setReturnType(RcClass returnType) {
        this.returnType = returnType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        RcMethod rcMethod = (RcMethod) obj;
        return Objects.equals(getName(), rcMethod.getName())
                && Objects.deepEquals(getParameters(), rcMethod.getParameters())
                && Objects.equals(getReturnType(), rcMethod.getReturnType());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParameters(), getReturnType());
    }
}