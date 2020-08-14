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

package io.github.lxgaming.reconstruct.common.entity;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Transform {
    
    private ClassReader classReader;
    private ClassWriter classWriter;
    private String className;
    
    public ClassReader getClassReader() {
        return classReader;
    }
    
    public void setClassReader(ClassReader classReader) {
        this.classReader = classReader;
    }
    
    public ClassWriter getClassWriter() {
        return classWriter;
    }
    
    public void setClassWriter(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
}