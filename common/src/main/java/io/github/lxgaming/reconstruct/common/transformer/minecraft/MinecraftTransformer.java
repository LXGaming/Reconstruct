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

package io.github.lxgaming.reconstruct.common.transformer.minecraft;

import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.entity.Transform;
import io.github.lxgaming.reconstruct.common.transformer.Transformer;
import io.github.lxgaming.reconstruct.common.transformer.minecraft.entity.Artifact;
import io.github.lxgaming.reconstruct.common.util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class MinecraftTransformer extends Transformer {

    public MinecraftTransformer() {
        addAlias("minecraft");
    }

    @Override
    public boolean prepare() {
        Path inputPath = Reconstruct.getInstance().getConfig().getInputPath();
        try (JarFile jarFile = new JarFile(inputPath.toFile(), false)) {
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                return true;
            }

            String mainClass = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
            if (!mainClass.equals("net.minecraft.bundler.Main")) {
                return true;
            }

            Reconstruct.getInstance().getLogger().info("Minecraft Bundler Detected");

            List<Artifact> versions = parseArtifacts(jarFile, "META-INF/versions.list");
            if (versions == null) {
                return false;
            }

            if (versions.isEmpty()) {
                Reconstruct.getInstance().getLogger().warn("No versions found");
                return false;
            }

            Artifact artifact = versions.get(0);
            Reconstruct.getInstance().getLogger().info("- {} ({})", artifact.getId(), artifact.getHash());

            Path extractPath = extract(jarFile, "META-INF/versions/" + artifact.getPath());
            if (extractPath == null) {
                return false;
            }

            Reconstruct.getInstance().getLogger().info("Overriding Input Path {} -> {}", inputPath, extractPath);
            Reconstruct.getInstance().getConfig().setInputPath(extractPath);
            return true;
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while analysing {}", inputPath, ex);
            return false;
        }
    }

    @Override
    public void execute(Transform transform) throws Exception {
    }

    private List<Artifact> parseArtifacts(JarFile jarFile, String entryName) {
        JarEntry jarEntry = jarFile.getJarEntry(entryName);
        if (jarEntry == null) {
            Reconstruct.getInstance().getLogger().warn("{} does not exist", entryName);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)))) {
            List<Artifact> artifacts = new ArrayList<>();

            String line;
            if ((line = reader.readLine()) != null) {
                String[] split = line.split("\t");
                artifacts.add(new Artifact(split[1], split[2], split[0]));
            }

            return artifacts;
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while parsing {}", entryName, ex);
            return null;
        }
    }

    private Path extract(JarFile jarFile, String entryName) {
        JarEntry jarEntry = jarFile.getJarEntry(entryName);
        if (jarEntry == null) {
            Reconstruct.getInstance().getLogger().warn("{} does not exist", entryName);
            return null;
        }

        int index = jarEntry.getName().lastIndexOf('/');
        String name = index != -1 ? jarEntry.getName().substring(index + 1) : entryName;
        Path outputPath = Reconstruct.getInstance().getConfig().getOutputPath().getParent().resolve(name);

        Reconstruct.getInstance().getLogger().info("Extracting {} -> {}", entryName, outputPath);

        try (InputStream inputStream = jarFile.getInputStream(jarEntry); OutputStream outputStream = Files.newOutputStream(outputPath)) {
            IOUtils.transferBytes(inputStream, outputStream);
            return outputPath;
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while extracting {}", entryName, ex);
            return null;
        }
    }
}