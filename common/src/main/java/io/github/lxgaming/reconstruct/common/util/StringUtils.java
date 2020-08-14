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

package io.github.lxgaming.reconstruct.common.util;

import java.util.Collection;

public class StringUtils {
    
    public static boolean containsIgnoreCase(Collection<String> collection, String targetString) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        
        for (String string : collection) {
            if (string.equalsIgnoreCase(targetString)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean containsIgnoreCase(Collection<String> collection, Collection<String> targetCollection) {
        if (collection == null || collection.isEmpty() || targetCollection == null || targetCollection.isEmpty()) {
            return false;
        }
        
        for (String string : targetCollection) {
            if (containsIgnoreCase(collection, string)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Removes non-printable characters (excluding new line and carriage return) in the provided {@link java.lang.String String}.
     *
     * @param string The {@link java.lang.String String} to filter.
     * @return The filtered {@link java.lang.String String}.
     */
    public static String filter(String string) {
        return string.replaceAll("[^\\x20-\\x7E\\x0A\\x0D]", "");
    }
    
    public static boolean isBlank(CharSequence charSequence) {
        int length;
        if (charSequence == null || (length = charSequence.length()) == 0) {
            return true;
        }
        
        for (int index = 0; index < length; index++) {
            if (!Character.isWhitespace(charSequence.charAt(index))) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean isNotBlank(CharSequence charSequence) {
        return !isBlank(charSequence);
    }
    
    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
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
}