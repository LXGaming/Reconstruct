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

package io.github.lxgaming.reconstruct.data;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Transform {
    
    private final ClassReader classReader;
    private final ClassWriter classWriter;
    private String className;
    
    public Transform(ClassReader classReader, ClassWriter classWriter) {
        this.classReader = classReader;
        this.classWriter = classWriter;
    }
    
    public ClassReader getClassReader() {
        return classReader;
    }
    
    public ClassWriter getClassWriter() {
        return classWriter;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
}