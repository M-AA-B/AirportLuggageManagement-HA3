package com.bangs.luggage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Runs M tasks in parallel on K AGVs (thread pool of size K).
 * This runner still uses a time scale for simulated task durations,
 * independent from the ChargingSim (which is now discrete-event).
 */
public class ConcurrentTasksRunner {

    /** Real-time ms per simulated minute for task execution (tweak freely). */
    public static final int TIME_SCALE_MS_PER_MIN = 100;

    private final Logger logger;
    private final Random rand = new Random();

    public ConcurrentTasksRunner(Logger logger) {
        this.logger = logger;
    }

    public List<Equipment> makeAgvs(int K) {
        List<Equipment> agvs = new ArrayList<>();
        for (int i = 1; i <= K; i++) {
            agvs.add(new Equipment("TUG-" + String.format("%02d", i), "Tug " + i, Equipment.Type.VEHICLE));
        }
        return agvs;
    }

    public List<Task> makeRandomTasks(int M) {
        Task.Type[] types = { Task.Type.LOAD, Task.Type.UNLOAD, Task.Type.TRANSFER, Task.Type.CHARGE };
        List<Task> tasks = new ArrayList<>(M);
        for (int i = 1; i <= M; i++) {
            Task.Type t = types[rand.nextInt(types.length)];
            Task task = new Task("SIM-" + i, t);
            task.with("flight", "LH" + (100 + rand.nextInt(900)));
            if (t == Task.Type.TRANSFER) {
                task.with("from", "CONV-" + (1 + rand.nextInt(4)))
                    .with("to", "BAY-" + (1 + rand.nextInt(6)));
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Execute M tasks concurrently across K AGVs.
     * Each task takes 5..20 simulated minutes.
     */
    public void runParallel(List<Task> tasks, List<Equipment> agvs) throws InterruptedException {
        int K = agvs.size();
        ExecutorService pool = Executors.newFixedThreadPool(K);
        CountDownLatch latch = new CountDownLatch(tasks.size());

        for (int i = 0; i < tasks.size(); i++) {
            final Task task = tasks.get(i);
            final Equipment assignee = agvs.get(i % K);
            pool.submit(() -> {
                try {
                    task.setStatus(Task.Status.RUNNING);
                    logger.info(assignee, "Task RUNNING: " + task);

                    int workMin = 5 + rand.nextInt(16); // 5..20
                    Thread.sleep((long) workMin * TIME_SCALE_MS_PER_MIN);

                    task.setStatus(Task.Status.DONE);
                    logger.info(assignee, "Task DONE: " + task + " (dur=" + workMin + " min)");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    task.setStatus(Task.Status.FAILED);
                    logger.error(assignee, "Task FAILED (interrupted): " + task);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
    }
}
