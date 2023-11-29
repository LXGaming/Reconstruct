/*
 * Copyright 2021 Alex Thomson
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

package io.github.lxgaming.reconstruct.common.transformer.minecraft.entity;

public class Artifact {

    private final String id;
    private final String path;
    private final String hash;

    public Artifact(String id, String path, String hash) {
        this.id = id;
        this.path = path;
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getHash() {
        return hash;
    }
}