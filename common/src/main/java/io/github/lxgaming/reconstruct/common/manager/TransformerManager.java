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

package io.github.lxgaming.reconstruct.common.manager;

import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.entity.Transform;
import io.github.lxgaming.reconstruct.common.transformer.Transformer;
import io.github.lxgaming.reconstruct.common.transformer.proguard.ProGuardTransformer;
import io.github.lxgaming.reconstruct.common.transformer.rename.RenameTransformer;
import io.github.lxgaming.reconstruct.common.util.StringUtils;
import io.github.lxgaming.reconstruct.common.util.Toolbox;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class TransformerManager {
    
    private static final Set<Transformer> TRANSFORMERS = new LinkedHashSet<>();
    private static final Set<Class<? extends Transformer>> TRANSFORMER_CLASSES = new HashSet<>();
    
    public static void prepare() {
        registerTransformer(ProGuardTransformer.class);
        registerTransformer(RenameTransformer.class);
    }
    
    public static boolean execute(Transform transform) {
        String className = transform.getClassName();
        for (Transformer transformer : TRANSFORMERS) {
            try {
                transformer.execute(transform);
            } catch (Exception ex) {
                Reconstruct.getInstance().getLogger().error("{} encountered an error while processing {}", Toolbox.getClassSimpleName(transformer.getClass()), className, ex);
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean registerAlias(Transformer transformer, String alias) {
        if (StringUtils.containsIgnoreCase(transformer.getAliases(), alias)) {
            Reconstruct.getInstance().getLogger().warn("{} is already registered for {}", alias, Toolbox.getClassSimpleName(transformer.getClass()));
            return false;
        }
        
        transformer.getAliases().add(alias);
        Reconstruct.getInstance().getLogger().debug("{} registered for {}", alias, Toolbox.getClassSimpleName(transformer.getClass()));
        return true;
    }
    
    public static boolean registerTransformer(Class<? extends Transformer> transformerClass) {
        if (TRANSFORMER_CLASSES.contains(transformerClass)) {
            Reconstruct.getInstance().getLogger().warn("{} is already registered", Toolbox.getClassSimpleName(transformerClass));
            return false;
        }
        
        TRANSFORMER_CLASSES.add(transformerClass);
        Transformer transformer = Toolbox.newInstance(transformerClass);
        if (transformer == null) {
            Reconstruct.getInstance().getLogger().error("{} failed to initialize", Toolbox.getClassSimpleName(transformerClass));
            return false;
        }
        
        try {
            if (!transformer.initialize()) {
                Reconstruct.getInstance().getLogger().warn("{} failed to initialize", Toolbox.getClassSimpleName(transformerClass));
                return false;
            }
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while initializing {}", Toolbox.getClassSimpleName(transformerClass), ex);
            return false;
        }
        
        Collection<String> transformers = Reconstruct.getInstance().getConfig().getTransformers();
        if (transformers != null && !transformers.isEmpty() && !StringUtils.containsIgnoreCase(transformers, transformer.getAliases())) {
            return false;
        }
        
        try {
            if (!transformer.prepare()) {
                Reconstruct.getInstance().getLogger().warn("{} failed to prepare", Toolbox.getClassSimpleName(transformerClass));
                return false;
            }
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while preparing {}", Toolbox.getClassSimpleName(transformerClass), ex);
            return false;
        }
        
        TRANSFORMERS.add(transformer);
        Reconstruct.getInstance().getLogger().debug("{} registered", Toolbox.getClassSimpleName(transformerClass));
        return true;
    }
}