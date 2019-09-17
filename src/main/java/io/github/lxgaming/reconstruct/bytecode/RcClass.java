/*
 * Copyright 2019 Alex Thomson
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

package io.github.lxgaming.reconstruct.bytecode;

import io.github.lxgaming.reconstruct.util.Toolbox;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public final class RcClass implements Attributes {
    
    private final Set<Attribute> attributes;
    private final Set<RcClass> classes;
    private final Set<RcConstructor> constructors;
    private final Set<RcField> fields;
    private final Set<RcMethod> methods;
    private String name;
    private String descriptor;
    private int modifiers;
    
    public RcClass() {
        this.attributes = Toolbox.newHashSet();
        this.classes = Toolbox.newLinkedHashSet();
        this.constructors = Toolbox.newLinkedHashSet();
        this.fields = Toolbox.newLinkedHashSet();
        this.methods = Toolbox.newLinkedHashSet();
    }
    
    public void update() {
        setDescriptor(Toolbox.getClassDescriptor(getName()));
        getAttribute(Attributes.OBFUSCATED_NAME).map(Toolbox::getClassDescriptor).ifPresent(descriptor -> {
            setAttribute(Attributes.OBFUSCATED_DESCRIPTOR, descriptor);
        });
    }
    
    public RcClass getSuperClass() {
        if (Modifier.isAnnotation(getModifiers()) || Modifier.isEnum(getModifiers()) || Modifier.isInterface(getModifiers())) {
            return null;
        }
        
        return Toolbox.getFirst(getClasses(rcClass -> !Modifier.isAnnotation(rcClass.getModifiers())
                && !Modifier.isEnum(rcClass.getModifiers())
                && !Modifier.isInterface(rcClass.getModifiers())
        ));
    }
    
    public List<RcClass> getInterfaces() {
        return getClasses(rcClass -> Modifier.isInterface(rcClass.getModifiers()));
    }
    
    public List<RcClass> getClasses(Predicate<RcClass> predicate) {
        List<RcClass> classes = Toolbox.newArrayList();
        for (RcClass rcClass : this.classes) {
            if (predicate.test(rcClass)) {
                classes.add(rcClass);
            }
        }
        
        return classes;
    }
    
    public RcConstructor getDeclaredConstructor(Predicate<RcConstructor> predicate) {
        return Toolbox.getFirst(getConstructors(predicate));
    }
    
    public RcConstructor getConstructor(Predicate<RcConstructor> predicate) {
        RcConstructor constructor = getDeclaredConstructor(predicate);
        if (constructor != null) {
            return constructor;
        }
        
        RcClass parentClass = getSuperClass();
        if (parentClass != null) {
            return parentClass.getConstructor(predicate);
        }
        
        return null;
    }
    
    public List<RcConstructor> getConstructors(Predicate<RcConstructor> predicate) {
        List<RcConstructor> constructors = Toolbox.newArrayList();
        for (RcConstructor constructor : this.constructors) {
            if (predicate.test(constructor)) {
                constructors.add(constructor);
            }
        }
        
        return constructors;
    }
    
    public RcField getDeclaredField(Predicate<RcField> predicate) {
        return Toolbox.getFirst(getFields(predicate));
    }
    
    public RcField getField(Predicate<RcField> predicate) {
        RcField field = getDeclaredField(predicate);
        if (field != null) {
            return field;
        }
        
        RcClass parentClass = getSuperClass();
        if (parentClass != null) {
            return parentClass.getField(predicate);
        }
        
        return null;
    }
    
    public List<RcField> getFields(Predicate<RcField> predicate) {
        List<RcField> fields = Toolbox.newArrayList();
        for (RcField field : this.fields) {
            if (predicate.test(field)) {
                fields.add(field);
            }
        }
        
        return fields;
    }
    
    public RcMethod getDeclaredMethod(Predicate<RcMethod> predicate) {
        return Toolbox.getFirst(getMethods(predicate));
    }
    
    public RcMethod getMethod(Predicate<RcMethod> predicate) {
        RcMethod method = getDeclaredMethod(predicate);
        if (method != null) {
            return method;
        }
        
        for (RcClass rcClass : getClasses()) {
            RcMethod parentMethod = rcClass.getMethod(predicate);
            if (parentMethod != null) {
                return parentMethod;
            }
        }
        
        return null;
    }
    
    public List<RcMethod> getMethods(Predicate<RcMethod> predicate) {
        List<RcMethod> methods = Toolbox.newArrayList();
        for (RcMethod method : this.methods) {
            if (predicate.test(method)) {
                methods.add(method);
            }
        }
        
        return methods;
    }
    
    @Override
    public Set<Attribute> getAttributes() {
        return attributes;
    }
    
    public Set<RcClass> getClasses() {
        return classes;
    }
    
    public Set<RcConstructor> getConstructors() {
        return constructors;
    }
    
    public Set<RcField> getFields() {
        return fields;
    }
    
    public Set<RcMethod> getMethods() {
        return methods;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescriptor() {
        return descriptor;
    }
    
    protected void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
    
    public int getModifiers() {
        return modifiers;
    }
    
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        RcClass rcClass = (RcClass) obj;
        return Objects.equals(getName(), rcClass.getName());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}