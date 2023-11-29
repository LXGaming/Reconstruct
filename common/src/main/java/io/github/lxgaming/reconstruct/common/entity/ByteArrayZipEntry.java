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

import io.github.lxgaming.reconstruct.common.util.HashUtils;

import java.util.zip.ZipEntry;

public class ByteArrayZipEntry extends ZipEntry {

    private final byte[] bytes;

    public ByteArrayZipEntry(ZipEntry zipEntry, byte[] bytes) {
        this(zipEntry.getName(), bytes);
    }

    public ByteArrayZipEntry(String name, byte[] bytes) {
        super(name);
        this.bytes = bytes;

        // Required for STORED
        setCrc(HashUtils.crc(bytes));
        setSize(bytes.length);
    }

    public byte[] getBytes() {
        return bytes;
    }
}