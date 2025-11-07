package com.bangs.luggage;

import java.util.*;

/**
 * Discrete-event charging simulator:
 *  - N charging stations
 *  - K AGVs with random arrivals and random initial battery (10–90%)
 *  - Charge rate = 1% per simulated minute (deterministic charge time)
 *  - Wait time computed from queue snapshot at arrival
 *  - Records which station each AGV uses
 */
public class ChargingSim {

    /** 1% per simulated minute */
    public static final int CHARGE_RATE_PERCENT_PER_MIN = 1;

    private final Logger logger;
    private final Random rand = new Random();

    public ChargingSim(Logger logger) {
        this.logger = logger;
    }

    /** Result of a single AGV charging attempt (for summary output). */
    public static class ChargeResult {
        public final String agvId;
        public final int arrivalMin;      // when the AGV arrived (sim minutes)
        public final int initialBattery;  // %
        public final int chargeMin;       // minutes to reach 100% at 1%/min
        public final int waitMin;         // queue wait before starting
        public final int startMin;        // arrivalMin + waitMin
        public final int endMin;          // startMin + chargeMin
        public final int stationId;       // 1..N

        public ChargeResult(String agvId, int arrivalMin, int initialBattery, int chargeMin,
                            int waitMin, int startMin, int endMin, int stationId) {
            this.agvId = agvId;
            this.arrivalMin = arrivalMin;
            this.initialBattery = initialBattery;
            this.chargeMin = chargeMin;
            this.waitMin = waitMin;
            this.startMin = startMin;
            this.endMin = endMin;
            this.stationId = stationId;
        }

        @Override
        public String toString() {
            return String.format("%s arr=%d' batt=%d%% wait=%d' chg=%d' %d'→%d' cs=%d",
                    agvId, arrivalMin, initialBattery, waitMin, chargeMin, startMin, endMin, stationId);
        }
    }

    // Small record to track each station's next free time + its id
    private record Station(int id, int freeAt) {}

    /**
     * Simulate K AGVs charging at N stations.
     * - Arrival times are random in [0..20] minutes.
     * - Initial battery levels are random in [10..90] %.
     * - Charge duration = (100 - battery) minutes (1%/min).
     * - Wait uses a min-heap of station free-times for correct queueing.
     * - Returns per-AGV results including assigned station id.
     */
    public List<ChargeResult> simulateCharging(int N, int K) {
        // Generate inputs
        int[] arrivalMin     = new int[K];
        int[] initialBattery = new int[K];
        int[] chargeMin      = new int[K];

        for (int i = 0; i < K; i++) {
            arrivalMin[i]     = rand.nextInt(21);       // 0..20
            initialBattery[i] = 10 + rand.nextInt(81);  // 10..90 %
            int needed = Math.max(0, 100 - initialBattery[i]);
            chargeMin[i] = needed / CHARGE_RATE_PERCENT_PER_MIN; // 1% per minute
        }

        // Build jobs and sort by arrival time (stable for tie-break by index)
        record Job(String agvId, int idx, int arrival, int initBatt, int chg) {}
        List<Job> jobs = new ArrayList<>(K);
        for (int i = 0; i < K; i++) {
            jobs.add(new Job("TUG-" + String.format("%02d", i + 1), i, arrivalMin[i], initialBattery[i], chargeMin[i]));
        }
        jobs.sort(Comparator.comparingInt(Job::arrival).thenComparingInt(Job::idx));

        // Min-heap of stations by next free time; ids are 1..N
        PriorityQueue<Station> stationHeap = new PriorityQueue<>(
                Comparator.comparingInt(Station::freeAt).thenComparingInt(Station::id));
        for (int s = 1; s <= N; s++) stationHeap.add(new Station(s, 0));

        Equipment charger = new Equipment("CS-SIM", "Charger Simulator", Equipment.Type.CHARGING);
        List<ChargeResult> results = new ArrayList<>(K);

        // Schedule each job
        for (Job j : jobs) {
            Station st = stationHeap.poll();             // earliest station ready
            int earliestFree = st.freeAt();
            int start = Math.max(j.arrival(), earliestFree);
            int wait  = start - j.arrival();
            int end   = start + j.chg();

            // Put station back with its new free-time
            stationHeap.add(new Station(st.id(), end));

            // Logging that matches the intended semantics
            logger.info(charger, String.format(
                    "AGV %s arrived (arr=%d min, batt=%d%%, plan charge %d min) — earliest station free at %d min (cs=%d)",
                    j.agvId(), j.arrival(), j.initBatt(), j.chg(), earliestFree, st.id()));

            if (wait >= 15) {
                logger.warn(charger, String.format("AGV %s waited %d minutes (>=15). (cs=%d)", j.agvId(), wait, st.id()));
            } else {
                logger.info(charger, String.format("AGV %s waited %d minutes. (cs=%d)", j.agvId(), wait, st.id()));
            }

            logger.info(charger, String.format("AGV %s START charge at %d' for %d' (to 100%%) on cs=%d.",
                    j.agvId(), start, j.chg(), st.id()));
            logger.info(charger, String.format("AGV %s END charge at %d' (100%%) on cs=%d.",
                    j.agvId(), end, st.id()));

            results.add(new ChargeResult(j.agvId(), j.arrival(), j.initBatt(), j.chg(), wait, start, end, st.id()));
        }

        // Restore original order for printing (optional): by agv index
        results.sort(Comparator.comparing(r -> Integer.parseInt(r.agvId.substring(4))));

        return results;
    }
}
