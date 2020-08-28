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

import java.util.Optional;

public final class RcArray extends RcClass {
    
    private RcClass type;
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> getAttribute(Attribute.Key<T> key) {
        if (key == Attributes.OBFUSCATED_DESCRIPTOR) {
            return (Optional<T>) getType().getAttribute(key).map(value -> "[" + value);
        }
        
        if (key == Attributes.OBFUSCATED_NAME) {
            return (Optional<T>) getType().getAttribute(key).map(value -> value + "[]");
        }
        
        return getType().getAttribute(key);
    }
    
    public RcClass getType() {
        return type;
    }
    
    public void setType(RcClass type) {
        this.type = type;
    }
}