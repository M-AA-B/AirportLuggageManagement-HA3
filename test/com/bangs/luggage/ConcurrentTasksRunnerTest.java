package com.bangs.luggage;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for parallel task execution.
 */
public class ConcurrentTasksRunnerTest {

    @Test
    public void testRunParallel_CompletesAllTasks() throws InterruptedException {
        Logger logger = new Logger();
        ConcurrentTasksRunner runner = new ConcurrentTasksRunner(logger);

        List<Equipment> agvs = runner.makeAgvs(4);
        List<Task> tasks = runner.makeRandomTasks(10);

        runner.runParallel(tasks, agvs);

        long doneCount = tasks.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();
        assertEquals(tasks.size(), doneCount, "All tasks should reach DONE status");
    }

    @Test
    public void testMakeAgvs_UniqueIds() {
        ConcurrentTasksRunner runner = new ConcurrentTasksRunner(new Logger());
        List<Equipment> agvs = runner.makeAgvs(5);
        assertEquals(5, agvs.size());
        assertEquals(agvs.stream().map(Equipment::getId).distinct().count(), 5);
    }
}
