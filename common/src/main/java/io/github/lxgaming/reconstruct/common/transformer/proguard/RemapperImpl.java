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
import io.github.lxgaming.reconstruct.common.bytecode.Attribute;
import io.github.lxgaming.reconstruct.common.bytecode.Attributes;
import io.github.lxgaming.reconstruct.common.bytecode.RcClass;
import io.github.lxgaming.reconstruct.common.bytecode.RcField;
import io.github.lxgaming.reconstruct.common.bytecode.RcMethod;
import io.github.lxgaming.reconstruct.common.util.Toolbox;
import org.objectweb.asm.commons.Remapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RemapperImpl extends Remapper {
    
    private final Set<RcClass> cachedClasses;
    
    public RemapperImpl() {
        this.cachedClasses = new HashSet<>();
    }
    
    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        RcClass currentClass = getClass(Toolbox.toJavaName(owner), Attributes.OBFUSCATED_NAME);
        if (currentClass == null) {
            return name;
        }
        
        RcMethod currentMethod = getMethod(currentClass, name + descriptor, Attributes.OBFUSCATED_DESCRIPTOR);
        if (currentMethod == null) {
            return name;
        }
        
        Reconstruct.getInstance().getLogger().debug("Method {}.{}{} -> {}.{}{}", owner, name, descriptor, owner, currentMethod.getName(), descriptor);
        return currentMethod.getName();
    }
    
    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        RcClass currentClass = getClass(Toolbox.toJavaName(owner), Attributes.OBFUSCATED_NAME);
        if (currentClass == null) {
            return name;
        }
        
        RcField currentField = getField(currentClass, name + ":" + descriptor, Attributes.OBFUSCATED_DESCRIPTOR);
        if (currentField == null) {
            return name;
        }
        
        Reconstruct.getInstance().getLogger().debug("Field {}.{}:{} -> {}.{}:{}", owner, name, descriptor, owner, currentField.getName(), descriptor);
        return currentField.getName();
    }
    
    @Override
    public String map(String internalName) {
        RcClass currentClass = getClass(Toolbox.toJavaName(internalName), Attributes.OBFUSCATED_NAME);
        if (currentClass == null) {
            return internalName;
        }
        
        return Toolbox.toJvmName(currentClass.getName());
    }
    
    public RcClass getClass(String name, Attribute.Key<String> attribute) {
        RcClass cachedClass = getCachedClass(rcClass -> rcClass.getAttribute(attribute).map(name::equals).orElse(false));
        if (cachedClass != null) {
            return cachedClass;
        }
        
        RcClass currentClass = Reconstruct.getInstance().getClass(name, attribute).orElse(null);
        if (currentClass != null) {
            cachedClasses.add(currentClass);
            Reconstruct.getInstance().getLogger().debug("Class {} -> {}", name, currentClass.getName());
            return currentClass;
        }
        
        return null;
    }
    
    private RcField getField(RcClass rcClass, String descriptor, Attribute.Key<String> attribute) {
        return rcClass.getField(field -> {
            return field.getAttribute(attribute).map(alternativeDescriptor -> {
                return alternativeDescriptor.equals(descriptor);
            }).orElse(false);
        });
    }
    
    private RcMethod getMethod(RcClass rcClass, String descriptor, Attribute.Key<String> attribute) {
        return rcClass.getMethod(method -> {
            return method.getAttribute(attribute).map(alternativeDescriptor -> {
                return alternativeDescriptor.equals(descriptor);
            }).orElse(false);
        });
    }
    
    private RcClass getCachedClass(Predicate<RcClass> predicate) {
        return Toolbox.getFirst(getCachedClasses(predicate));
    }
    
    private List<RcClass> getCachedClasses(Predicate<RcClass> predicate) {
        List<RcClass> classes = new ArrayList<>();
        for (RcClass rcClass : this.cachedClasses) {
            if (predicate.test(rcClass)) {
                classes.add(rcClass);
            }
        }
        
        return classes;
    }
}