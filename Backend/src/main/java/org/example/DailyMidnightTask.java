package org.example;

import java.time.*;
import java.util.concurrent.*;

public class DailyMidnightTask {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void startDailyMidnightTask(Runnable task) {
        scheduleNextRun(task);
    }

    private void scheduleNextRun(Runnable task) {
        long delay = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).toMillis();

        scheduler.schedule(() -> {
            task.run();               // Run the function
            scheduleNextRun(task);   // Schedule the next run
        }, delay, TimeUnit.MILLISECONDS);
    }
}
