package com.bangs.luggage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test combining ChargingSim + ConcurrentTasksRunner
 * via the helper simulateChargingAndTasks() in Main.
 */
public class MainIntegrationTest {

    @Test
    public void testSimulateChargingAndTasks_NoExceptions() {
        Logger logger = new Logger();
        assertDoesNotThrow(() -> {
            // Create a mini version of the simulation
            ChargingSim cSim = new ChargingSim(logger);
            var chargeResults = cSim.simulateCharging(2, 6);
            ConcurrentTasksRunner runner = new ConcurrentTasksRunner(logger);
            var agvs = runner.makeAgvs(6);
            var tasks = runner.makeRandomTasks(10);
            runner.runParallel(tasks, agvs);
        }, "Simulation should complete without exceptions");
    }
}
