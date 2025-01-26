package dev.astatic.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseTaskManager {
    private static final ExecutorService executor = Executors.newFixedThreadPool(6);

    public static void submitTask(Runnable task) {
        executor.submit(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
