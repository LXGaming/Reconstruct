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

package io.github.lxgaming.reconstruct.common.configuration;

import java.nio.file.Path;
import java.util.Collection;

public interface Config {

    boolean isDebug();

    boolean isTrace();

    int getThreads();

    void setThreads(int threads);

    Collection<String> getTransformers();

    Path getInputPath();

    void setInputPath(Path inputPath);

    Path getMappingPath();

    void setMappingPath(Path mappingPath);

    Path getOutputPath();

    void setOutputPath(Path outputPath);

    Collection<String> getExcludedPackages();
}