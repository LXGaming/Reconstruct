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

public final class RcConstructor extends RcBehavior {
    
    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String STATIC_INITIALIZER_NAME = "<clinit>";
    
    public void update() {
        setDescriptor(Toolbox.getConstructorDescriptor(getName(), getParameters().stream().map(RcClass::getDescriptor).toArray(String[]::new)));
        getAttribute(Attributes.OBFUSCATED_NAME).map(obfuscatedName -> {
            return Toolbox.getConstructorDescriptor(obfuscatedName, getParameters().stream().map(parameter -> {
                return parameter.getAttribute(Attributes.OBFUSCATED_DESCRIPTOR).orElse(parameter.getDescriptor());
            }).toArray(String[]::new));
        }).ifPresent(descriptor -> {
            setAttribute(Attributes.OBFUSCATED_DESCRIPTOR, descriptor);
        });
    }
    
    public boolean isConstructor() {
        return getName() != null && getName().equals(CONSTRUCTOR_NAME);
    }
    
    public boolean isStaticInitializer() {
        return getName() != null && getName().equals(STATIC_INITIALIZER_NAME);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        RcConstructor rcConstructor = (RcConstructor) obj;
        return Objects.equals(getName(), rcConstructor.getName())
                && Objects.deepEquals(getParameters(), rcConstructor.getParameters());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParameters());
    }
}