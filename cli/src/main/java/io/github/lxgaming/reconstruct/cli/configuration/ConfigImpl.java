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

package io.github.lxgaming.reconstruct.cli.configuration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import io.github.lxgaming.reconstruct.common.configuration.Config;

import java.nio.file.Path;
import java.util.List;

public class ConfigImpl implements Config {
    
    @Parameter(names = {"-debug", "--debug"}, description = "For debugging purposes")
    private boolean debug = false;
    
    @Parameter(names = {"-thread", "--thread", "-threads", "--threads"}, description = "Performs deobfuscation asynchronously across the specified number of threads")
    private int threads = 0;
    
    @Parameter(names = {"-transformer", "--transformer", "-transformers", "--transformers"}, description = "Transformers to use during the deobfuscation process")
    private List<String> transformers = null;
    
    @Parameter(names = {"-jar", "--jar", "-input", "--input"}, description = "Obfuscated Jar Path", required = true, converter = PathConverter.class)
    private Path jarPath = null;
    
    @Parameter(names = {"-mapping", "--mapping"}, description = "ProGuard Mappings Path", required = true, converter = PathConverter.class)
    private Path mappingPath = null;
    
    @Parameter(names = {"-output", "--output"}, description = "Output Jar Path", required = true, converter = PathConverter.class)
    private Path outputPath = null;
    
    @Parameter(names = {"-exclude", "--exclude", "-excludes", "--excludes", "-excluded", "--excluded"}, description = "Packages which won't be transformed")
    private List<String> excludedPackages = null;
    
    @Parameter(names = {"-agree", "--agree"}, description = "Do not distribute confidential or proprietary information unless you have explicit permission from the copyright holder")
    private boolean agree = false;
    
    @Override
    public boolean isDebug() {
        return debug;
    }
    
    @Override
    public int getThreads() {
        return threads;
    }
    
    @Override
    public void setThreads(int threads) {
        this.threads = threads;
    }
    
    @Override
    public List<String> getTransformers() {
        return transformers;
    }
    
    @Override
    public Path getJarPath() {
        return jarPath;
    }
    
    @Override
    public Path getMappingPath() {
        return mappingPath;
    }
    
    @Override
    public Path getOutputPath() {
        return outputPath;
    }
    
    @Override
    public List<String> getExcludedPackages() {
        return excludedPackages;
    }
    
    public boolean isAgree() {
        return agree;
    }
}