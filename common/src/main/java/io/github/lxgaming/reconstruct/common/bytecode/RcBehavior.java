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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class RcBehavior extends RcMember {
    
    private final List<RcClass> parameters;
    
    RcBehavior() {
        this.parameters = new ArrayList<>();
    }
    
    public List<RcClass> getParameters(Predicate<RcClass> predicate) {
        List<RcClass> parameters = new ArrayList<>();
        for (RcClass parameter : this.parameters) {
            if (predicate.test(parameter)) {
                parameters.add(parameter);
            }
        }
        
        return parameters;
    }
    
    public List<RcClass> getParameters() {
        return parameters;
    }
}