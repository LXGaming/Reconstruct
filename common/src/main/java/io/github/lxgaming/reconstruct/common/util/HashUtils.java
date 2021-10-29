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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class HashUtils {
    
    public static long crc(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }
    
    public static boolean sha256(InputStream inputStream, String hash) throws IOException {
        return sha256(inputStream).equalsIgnoreCase(hash);
    }
    
    public static boolean sha256(Path path, String hash) throws IOException {
        return sha256(path).equalsIgnoreCase(hash);
    }
    
    public static String sha256(InputStream inputStream) throws IOException {
        byte[] bytes = digest(sha256(), inputStream);
        return toString(bytes);
    }
    
    public static String sha256(Path path) throws IOException {
        byte[] bytes = digest(sha256(), path);
        return toString(bytes);
    }
    
    public static MessageDigest sha256() {
        return getDigest("SHA-256");
    }
    
    public static String toString(MessageDigest digest) {
        return toString(digest.digest());
    }
    
    public static String toString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for (byte entry : bytes) {
            String hex = Integer.toHexString(0xFF & entry);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            
            stringBuilder.append(hex);
        }
        
        return stringBuilder.toString();
    }
    
    private static byte[] digest(MessageDigest digest, Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return digest(digest, inputStream);
        }
    }
    
    private static byte[] digest(MessageDigest digest, InputStream inputStream) throws IOException {
        byte[] buffer = new byte[IOUtils.DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }
        
        return digest.digest();
    }
    
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}