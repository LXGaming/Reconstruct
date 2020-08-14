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

public final class Modifier {
    
    public static final int PUBLIC = 0x0001;
    public static final int PRIVATE = 0x0002;
    public static final int PROTECTED = 0x0004;
    public static final int STATIC = 0x0008;
    public static final int FINAL = 0x0010;
    public static final int SYNCHRONIZED = 0x0020;
    public static final int VOLATILE = 0x0040;
    public static final int VARARGS = 0x0080;
    public static final int TRANSIENT = 0x0080;
    public static final int NATIVE = 0x0100;
    public static final int INTERFACE = 0x0200;
    public static final int ABSTRACT = 0x0400;
    public static final int STRICT = 0x0800;
    public static final int ANNOTATION = 0x2000;
    public static final int ENUM = 0x4000;
    
    public static boolean isPublic(int modifier) {
        return (modifier & PUBLIC) != 0;
    }
    
    public static boolean isPrivate(int modifier) {
        return (modifier & PRIVATE) != 0;
    }
    
    public static boolean isProtected(int modifier) {
        return (modifier & PROTECTED) != 0;
    }
    
    public static boolean isPackage(int modifier) {
        return (modifier & (PUBLIC | PRIVATE | PROTECTED)) == 0;
    }
    
    public static boolean isStatic(int modifier) {
        return (modifier & STATIC) != 0;
    }
    
    public static boolean isFinal(int modifier) {
        return (modifier & FINAL) != 0;
    }
    
    public static boolean isSynchronized(int modifier) {
        return (modifier & SYNCHRONIZED) != 0;
    }
    
    public static boolean isVolatile(int modifier) {
        return (modifier & VOLATILE) != 0;
    }
    
    public static boolean isVarArgs(int modifier) {
        return (modifier & VARARGS) != 0;
    }
    
    public static boolean isTransient(int modifier) {
        return (modifier & TRANSIENT) != 0;
    }
    
    public static boolean isNative(int modifier) {
        return (modifier & NATIVE) != 0;
    }
    
    public static boolean isInterface(int modifier) {
        return (modifier & INTERFACE) != 0;
    }
    
    public static boolean isAbstract(int modifier) {
        return (modifier & ABSTRACT) != 0;
    }
    
    public static boolean isStrict(int modifier) {
        return (modifier & STRICT) != 0;
    }
    
    public static boolean isAnnotation(int modifier) {
        return (modifier & ANNOTATION) != 0;
    }
    
    public static boolean isEnum(int modifier) {
        return (modifier & ENUM) != 0;
    }
}