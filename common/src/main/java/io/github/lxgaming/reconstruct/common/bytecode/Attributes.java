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
import java.util.Set;

public interface Attributes {
    
    Attribute.Key<Integer> BEHAVIOR_FIRST_LINE_NUMBER = Attribute.Key.of("behavior_first_line_number", Integer.class);
    Attribute.Key<Integer> BEHAVIOR_LAST_LINE_NUMBER = Attribute.Key.of("behavior_last_line_number", Integer.class);
    Attribute.Key<String> OBFUSCATED_DESCRIPTOR = Attribute.Key.of("obfuscated_descriptor", String.class);
    Attribute.Key<String> OBFUSCATED_NAME = Attribute.Key.of("obfuscated_name", String.class);
    
    Set<Attribute> getAttributes();
    
    default <T> Optional<T> getAttribute(Attribute.Key<T> key) {
        for (Attribute attribute : getAttributes()) {
            if (attribute.getKey().equals(key)) {
                return Optional.ofNullable(key.getType().cast(attribute.getValue()));
            }
        }
        
        return Optional.empty();
    }
    
    default <T> boolean setAttribute(Attribute.Key<T> key, T value) {
        for (Attribute attribute : getAttributes()) {
            if (attribute.getKey().equals(key)) {
                attribute.setValue(value);
                return true;
            }
        }
        
        return getAttributes().add(new Attribute(key, value));
    }
}