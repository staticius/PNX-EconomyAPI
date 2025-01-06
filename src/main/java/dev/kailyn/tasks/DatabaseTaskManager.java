package dev.kailyn.tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class DatabaseTaskManager {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void submitTask(Runnable task) {
        executor.submit(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
