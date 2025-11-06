package com.bangs.luggage;

import java.util.ArrayDeque;
import java.util.Deque;

public class TaskManager {
    private final Deque<Task> queue = new ArrayDeque<>();
    private final Logger logger;

    public TaskManager(Logger logger) {
        this.logger = logger;
    }

    public void enqueue(Task t) {
        queue.addLast(t);
        logger.info(new Equipment("system", "System", Equipment.Type.SYSTEM),
                "Enqueued " + t);
    }

    /** Dispatches the next task to the given equipment and logs state transitions. */
    public void dispatch(Equipment assignee) {
        Task t = queue.pollFirst();
        if (t == null) {
            logger.info(assignee, "No task to dispatch");
            return;
        }
        t.setStatus(Task.Status.RUNNING);
        logger.info(assignee, "Task RUNNING: " + t);

        // Simulate immediate completion
        t.setStatus(Task.Status.DONE);
        logger.info(assignee, "Task DONE: " + t);
    }
}
