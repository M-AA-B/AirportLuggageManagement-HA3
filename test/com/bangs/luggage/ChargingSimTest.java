package com.bangs.luggage;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the discrete-event AGV charging simulator.
 */
public class ChargingSimTest {

    @Test
    public void testSimulateCharging_SingleStation_SequentialOrder() {
        Logger logger = new Logger();
        ChargingSim sim = new ChargingSim(logger);

        List<ChargingSim.ChargeResult> results = sim.simulateCharging(1, 3);

        // The simulation must return 3 results.
        assertEquals(3, results.size());

        // Verify ordering: each next start time >= previous end time.
        results.sort((a,b) -> Integer.compare(a.startMin, b.startMin));
        for (int i = 1; i < results.size(); i++) {
            assertTrue(results.get(i).startMin >= results.get(i-1).endMin,
                "Next AGV should start after previous one finishes");
        }
    }

    @Test
    public void testMultipleStations_AssignmentSpread() {
        Logger logger = new Logger();
        ChargingSim sim = new ChargingSim(logger);

        List<ChargingSim.ChargeResult> results = sim.simulateCharging(3, 9);
        assertTrue(results.stream().map(r -> r.stationId).distinct().count() <= 3,
                "Should not exceed number of stations");
    }
}
