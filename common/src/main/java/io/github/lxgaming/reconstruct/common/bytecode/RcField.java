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

public final class RcField extends RcMember {
    
    private RcClass type;
    
    public void update() {
        setDescriptor(Toolbox.getFieldDescriptor(getName(), getType().getDescriptor()));
        getAttribute(Attributes.OBFUSCATED_NAME).map(obfuscatedName -> {
            return Toolbox.getFieldDescriptor(obfuscatedName, getType().getAttribute(Attributes.OBFUSCATED_DESCRIPTOR).orElse(type.getDescriptor()));
        }).ifPresent(descriptor -> {
            setAttribute(Attributes.OBFUSCATED_DESCRIPTOR, descriptor);
        });
    }
    
    public RcClass getType() {
        return type;
    }
    
    public void setType(RcClass type) {
        this.type = type;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        RcField rcField = (RcField) obj;
        return Objects.equals(getName(), rcField.getName())
                && Objects.deepEquals(getType(), rcField.getType());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType());
    }
}