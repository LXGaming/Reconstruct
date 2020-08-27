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

import io.github.lxgaming.common.task.Task;
import io.github.lxgaming.reconstruct.common.Reconstruct;
import io.github.lxgaming.reconstruct.common.util.Toolbox;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TaskManager {
    
    public static final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(0, Toolbox.newThreadFactory("Task Thread #%d"));
    
    public static void prepare() {
        SCHEDULED_EXECUTOR_SERVICE.setCorePoolSize(Reconstruct.getInstance().getConfig().getThreads());
    }
    
    public static void schedule(Task task) {
        try {
            if (!task.prepare()) {
                Reconstruct.getInstance().getLogger().warn("{} failed to prepare", Toolbox.getClassSimpleName(task.getClass()));
                return;
            }
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while preparing {}", Toolbox.getClassSimpleName(task.getClass()), ex);
            return;
        }
        
        try {
            task.schedule(SCHEDULED_EXECUTOR_SERVICE);
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Encountered an error while scheduling {}", Toolbox.getClassSimpleName(task.getClass()), ex);
        }
    }
    
    public static ScheduledFuture<?> schedule(Runnable runnable) {
        return SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, 0L, TimeUnit.MILLISECONDS);
    }
    
    public static void shutdown() {
        Reconstruct.getInstance().getLogger().debug("Shutting down TaskManager");
        try {
            SCHEDULED_EXECUTOR_SERVICE.shutdown();
            if (!SCHEDULED_EXECUTOR_SERVICE.awaitTermination(15000L, TimeUnit.MILLISECONDS)) {
                throw new InterruptedException();
            }
            
            Reconstruct.getInstance().getLogger().info("Successfully terminated task, continuing with shutdown process...");
        } catch (Exception ex) {
            Reconstruct.getInstance().getLogger().error("Failed to terminate task, continuing with shutdown process...");
        }
    }
}