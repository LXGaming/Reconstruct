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

package io.github.lxgaming.reconstruct.cli;

import com.beust.jcommander.JCommander;
import io.github.lxgaming.reconstruct.cli.configuration.ConfigImpl;
import io.github.lxgaming.reconstruct.cli.util.ShutdownHook;
import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.util.Toolbox;
import org.apache.logging.log4j.core.config.Configurator;
import org.fusesource.jansi.AnsiConsole;

public class Main {
    
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        if (System.getProperty("log4j.skipJansi", "false").equalsIgnoreCase("false")) {
            System.setProperty("log4j.skipJansi", "false");
            AnsiConsole.systemInstall();
        }
        
        Reconstruct reconstruct = new Reconstruct(new ConfigImpl());
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        
        Reconstruct.getInstance().getLogger().info("{} v{}", Reconstruct.NAME, Reconstruct.VERSION);
        Reconstruct.getInstance().getLogger().info("Authors: {}", Reconstruct.AUTHORS);
        Reconstruct.getInstance().getLogger().info("Source: {}", Reconstruct.SOURCE);
        Reconstruct.getInstance().getLogger().info("Website: {}", Reconstruct.WEBSITE);
        
        try {
            JCommander.newBuilder()
                    .addObject(Reconstruct.getInstance().getConfig())
                    .build()
                    .parse(args);
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while parsing arguments", ex);
            Runtime.getRuntime().exit(-1);
            return;
        }
        
        if (Reconstruct.getInstance().getConfig().isDebug()) {
            System.setProperty("reconstruct.logging.console.level", "DEBUG");
            Configurator.reconfigure();
            Reconstruct.getInstance().getLogger().debug("Debug mode enabled");
        } else {
            Reconstruct.getInstance().getLogger().info("Debug mode disabled");
        }
        
        if (!((ConfigImpl) Reconstruct.getInstance().getConfig()).isAgree()) {
            if (System.console() == null && !System.getProperty("java.class.path").contains("idea_rt.jar")) {
                Reconstruct.getInstance().getLogger().error("Failed to detect Console");
                return;
            }
            
            Reconstruct.getInstance().getLogger().warn("========================== WARNING ==========================");
            Reconstruct.getInstance().getLogger().warn("  Do not distribute confidential or proprietary information  ");
            Reconstruct.getInstance().getLogger().warn("unless you have explicit permission from the copyright holder");
            Reconstruct.getInstance().getLogger().warn("========================== WARNING ==========================");
            
            Toolbox.readline("Press Enter to continue...");
        }
        
        reconstruct.load();
    }
}