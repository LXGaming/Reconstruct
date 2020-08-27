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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class HashUtils {
    
    public static long crc(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }
    
    public static String sha256(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(string.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }
}