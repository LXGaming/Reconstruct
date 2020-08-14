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

import java.util.Objects;

public final class Attribute {
    
    private final Key<?> key;
    private Object value;
    
    Attribute(Key<?> key) {
        this(key, null);
    }
    
    Attribute(Key<?> key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public Key<?> getKey() {
        return key;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Attribute attribute = (Attribute) obj;
        return Objects.equals(getKey(), attribute.getKey());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }
    
    public static final class Key<T> {
        
        private final String name;
        private final Class<T> type;
        
        private Key(String name, Class<T> type) {
            this.name = name;
            this.type = type;
        }
        
        public static <T> Key<T> of(String name, Class<T> type) {
            return new Key<>(name, type);
        }
        
        public String getName() {
            return name;
        }
        
        public Class<T> getType() {
            return type;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            
            Key<?> key = (Key<?>) obj;
            return Objects.equals(getName(), key.getName())
                    && Objects.equals(getType(), key.getType());
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getType());
        }
    }
}