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

package io.github.lxgaming.reconstruct.util;

import io.github.lxgaming.reconstruct.Reconstruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Toolbox {
    
    private static final Scanner SCANNER = new Scanner(System.in);
    
    public static Optional<String> readline() {
        return readline("> ");
    }
    
    public static Optional<String> readline(String prompt) {
        try {
            System.out.print(prompt);
            if (SCANNER.hasNextLine()) {
                return Optional.ofNullable(SCANNER.nextLine());
            }
            
            return Optional.empty();
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }
    
    public static <T> T getFirst(List<T> list) {
        if (!list.isEmpty()) {
            T t = list.remove(0);
            if (!list.isEmpty()) {
                Reconstruct.getInstance().getLogger().warn("Multiple {} found", t.getClass().getSimpleName(), new Exception());
            }
            
            return t;
        }
        
        return null;
    }
    
    public static String getClassDescriptor(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        if (string.endsWith("[]")) {
            stringBuilder.append("[");
            appendClassDescriptor(stringBuilder, string.substring(0, string.length() - 2));
        } else {
            appendClassDescriptor(stringBuilder, string);
        }
        
        return stringBuilder.toString();
    }
    
    public static String getConstructorDescriptor(String name, String[] parameters) {
        StringBuilder stringBuilder = new StringBuilder(name);
        for (String parameter : parameters) {
            stringBuilder.append(parameter);
        }
        
        return stringBuilder.toString();
    }
    
    public static String getFieldDescriptor(String name, String type) {
        return name + ":" + type;
    }
    
    public static String getMethodDescriptor(String name, String[] parameters, String returnType) {
        StringBuilder stringBuilder = new StringBuilder(name);
        stringBuilder.append("(");
        for (String parameter : parameters) {
            stringBuilder.append(parameter);
        }
        
        stringBuilder.append(")");
        stringBuilder.append(returnType);
        return stringBuilder.toString();
    }
    
    private static void appendClassDescriptor(StringBuilder stringBuilder, String name) {
        if (name.equals("boolean")) {
            stringBuilder.append("Z");
        } else if (name.equals("byte")) {
            stringBuilder.append("B");
        } else if (name.equals("char")) {
            stringBuilder.append("C");
        } else if (name.equals("double")) {
            stringBuilder.append("D");
        } else if (name.equals("float")) {
            stringBuilder.append("F");
        } else if (name.equals("int")) {
            stringBuilder.append("I");
        } else if (name.equals("long")) {
            stringBuilder.append("J");
        } else if (name.equals("short")) {
            stringBuilder.append("S");
        } else if (name.equals("void")) {
            stringBuilder.append("V");
        } else {
            stringBuilder.append("L").append(toJvmName(name)).append(";");
        }
    }
    
    public static String fromFileName(String name) {
        return toJavaName(name.substring(0, name.lastIndexOf('.')));
    }
    
    public static String toFileName(String name) {
        return toJvmName(name) + ".class";
    }
    
    public static String toJavaName(String name) {
        return name.replace('/', '.');
    }
    
    public static String toJvmName(String name) {
        return name.replace('.', '/');
    }
    
    public static boolean startsWith(Collection<String> collection, String targetString) {
        if (collection == null || targetString == null) {
            return false;
        }
        
        for (String string : collection) {
            if (targetString.startsWith(string)) {
                return true;
            }
        }
        
        return false;
    }
    
    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(ArrayList::new));
    }
    
    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(HashSet::new));
    }
    
    @SafeVarargs
    public static <E> LinkedHashSet<E> newLinkedHashSet(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}