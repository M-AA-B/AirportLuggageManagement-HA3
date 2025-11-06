package com.bangs.luggage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        // ---------------- INITIAL SETUP ----------------
        Equipment tug = new Equipment("TUG-07", "Tug 07", Equipment.Type.VEHICLE);
        Equipment charger = new Equipment("CS-1", "Charger 1", Equipment.Type.CHARGING);
        Equipment system = new Equipment("system", "System", Equipment.Type.SYSTEM);

        Logger logger = new Logger();
        LogMeta meta = new LogMeta();
        StorageManager storage = new StorageManager(logger);
        TaskManager tasks = new TaskManager(logger);

        // ---------------- STORAGE SYSTEM ----------------
        storage.place("BG998877", "CONVEYOR-1");
        storage.place("BG111222", "VEHICLE:TUG-07");
        System.out.println("Locate BG998877 => " + storage.locate("BG998877"));

        // ---------------- TASK SYSTEM ----------------
        Task t1 = new Task("T1", Task.Type.LOAD).with("flight", "LH123").with("bag", "BG998877");
        Task t2 = new Task("T2", Task.Type.TRANSFER).with("from", "CONVEYOR-1").with("to", "VEHICLE:TUG-07");
        tasks.enqueue(t1);
        tasks.enqueue(t2);
        tasks.dispatch(tug);
        tasks.dispatch(charger);

        // ---------------- LOGGING DEMO ----------------
        logger.info(system, "System started");
        logger.info(tug, "Assigned LOAD LH123 bag=BG998877");
        logger.info(charger, "Charging started for TUG-07");
        logger.error(system, "Demo error: testing log levels");

        // ---------------- BYTE + CHAR STREAMS ----------------
        StreamSim sim = new StreamSim();
        byte[] bin = sim.writeLuggageBinaryToBytes("BG998877", "LH123", "A.KHAN", 18);
        System.out.println("Binary luggage decoded => " + sim.readLuggageBinaryFromBytes(bin));

        String proto = sim.writeProtocolToString("ACK LOAD BG998877");
        System.out.println("Protocol lines => " + sim.readProtocolFromString(proto));

        // ---------------- ARCHIVE TODAY'S LOG FILES (auto demo) ----------------
        List<Path> todayLogs = logger.findByDate(LocalDate.now());
        if (!todayLogs.isEmpty()) {
            Path zip = meta.archiveToZip(Path.of("archive", "today.zip"), todayLogs.toArray(Path[]::new));
            System.out.println("Archived today's logs to: " + zip.toAbsolutePath());
        }

        // ---------------- FILE METADATA OPS (auto demo) ----------------
        try {
            // 1) CREATE
            Path newLog = Path.of("logs", "temp.log");
            Path created = meta.create(newLog);
            System.out.println("Created file: " + created);

            // 2) MOVE (rename)
            Path moved = Path.of("logs", "temp_moved.log");
            meta.move(created, moved);
            System.out.println("Moved file to: " + moved);

            // 3) DELETE
            meta.delete(moved);
            System.out.println("Deleted file: " + moved);

            // 4) ARCHIVE again to a separate zip
            List<Path> todayLogs2 = logger.findByDate(LocalDate.now());
            if (!todayLogs2.isEmpty()) {
                Path zip2 = meta.archiveToZip(Path.of("archive", "meta_demo.zip"),
                        todayLogs2.toArray(Path[]::new));
                System.out.println("Archived today's logs to: " + zip2.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Metadata operation failed: " + e.getMessage());
        }

        // ---------------- INTERACTIVE MENU ----------------
        menuInteractive(logger, meta);
    }

    // ================== INTERACTIVE MENU ==================
    private static void menuInteractive(Logger logger, LogMeta meta) throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1) Open logs (by equipment or date)");
            System.out.println("2) Create file");
            System.out.println("3) Move (rename) file");
            System.out.println("4) Delete file");
            System.out.println("5) Archive logs (zip by date)");
            System.out.println("6) Simulate charging & concurrent tasks"); // <-- new
            System.out.println("0) Exit");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> openLogsInteractive(logger, sc);
                    case "2" -> createFileInteractive(meta, sc);
                    case "3" -> moveFileInteractive(meta, sc);
                    case "4" -> deleteFileInteractive(meta, sc);
                    case "5" -> archiveInteractive(logger, meta, sc);
                    case "6" -> simulateChargingAndTasks(logger); // <-- new
                    case "0" -> { System.out.println("Bye!"); return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Operation failed: " + e.getMessage());
            }
        }
    }

    // ----- 1) OPEN LOGS (reusing a shared Scanner) -----
    private static void openLogsInteractive(Logger logger, Scanner sc) throws Exception {
        System.out.println("\nOpen logs by: 1) Equipment name/ID  2) Date (YYYY-MM-DD)");
        String choice = sc.nextLine().trim();

        java.util.List<java.nio.file.Path> results = java.util.List.of();

        if ("1".equals(choice)) {
            System.out.print("Enter equipment name or ID (e.g., TUG-07): ");
            String name = sc.nextLine().trim();
            results = logger.findByEquipmentName(name);

        } else if ("2".equals(choice)) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String s = sc.nextLine().trim();
            java.time.LocalDate date = java.time.LocalDate.parse(s);
            results = logger.findByDate(date);

        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No matching log files found.");
            return;
        }

        System.out.println("Found files:");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ") " + results.get(i));
        }

        System.out.print("Enter number to open: ");
        int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
        if (idx < 0 || idx >= results.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        var selected = results.get(idx);
        System.out.println("--- BEGIN " + selected + " ---");
        logger.open(selected).forEach(System.out::println);
        System.out.println("--- END " + selected + " ---");
    }

    // ----- 2) CREATE FILE -----
    private static void createFileInteractive(LogMeta meta, Scanner sc) throws Exception {
        System.out.print("\nEnter path to create (e.g., logs/demo_created.log): ");
        String p = sc.nextLine().trim();
        java.nio.file.Path created = meta.create(java.nio.file.Path.of(p));
        System.out.println("Created: " + created.toAbsolutePath());
    }

    // ----- 3) MOVE/RENAME FILE -----
    private static void moveFileInteractive(LogMeta meta, Scanner sc) throws Exception {
        System.out.print("\nEnter source path (e.g., logs/demo_created.log): ");
        String src = sc.nextLine().trim();
        System.out.print("Enter destination path (e.g., logs/demo_moved.log): ");
        String dst = sc.nextLine().trim();
        java.nio.file.Path moved = meta.move(java.nio.file.Path.of(src), java.nio.file.Path.of(dst));
        System.out.println("Moved to: " + moved.toAbsolutePath());
    }

    // ----- 4) DELETE FILE -----
    private static void deleteFileInteractive(LogMeta meta, Scanner sc) throws Exception {
        System.out.print("\nEnter file path to delete: ");
        String p = sc.nextLine().trim();
        meta.delete(java.nio.file.Path.of(p));
        System.out.println("Deleted (if existed): " + java.nio.file.Path.of(p).toAbsolutePath());
    }

    // ----- 5) ARCHIVE LOGS (ZIP BY DATE) -----
    private static void archiveInteractive(Logger logger, LogMeta meta, Scanner sc) throws Exception {
        System.out.print("\nEnter date to archive (YYYY-MM-DD): ");
        String s = sc.nextLine().trim();
        java.time.LocalDate date = java.time.LocalDate.parse(s);
        var files = logger.findByDate(date);
        if (files.isEmpty()) {
            System.out.println("No logs found for that date.");
            return;
        }
        System.out.print("Enter output zip path (e.g., archive/" + s + ".zip): ");
        String out = sc.nextLine().trim();
        java.nio.file.Path zip = meta.archiveToZip(java.nio.file.Path.of(out), files.toArray(java.nio.file.Path[]::new));
        System.out.println("Archived to: " + zip.toAbsolutePath());
    }

    // ----- 6) SIMULATE CHARGING + PARALLEL TASKS -----
    private static void simulateChargingAndTasks(Logger logger) throws Exception {
        // -------- Randomize N, K, M each run --------
        java.util.Random rnd = new java.util.Random();
        int N = 1 + rnd.nextInt(4);   // 1..4 charging stations
        int K = 3 + rnd.nextInt(8);   // 3..10 AGVs
        int M = 5 + rnd.nextInt(16);  // 5..20 tasks

        System.out.printf("%n[SIM] Parameters => N stations=%d, K AGVs=%d, M tasks=%d%n", N, K, M);

        // -------- 1) Charging simulation (discrete-event, no sleeping) --------
        ChargingSim cSim = new ChargingSim(logger);
        var chargeResults = cSim.simulateCharging(N, K);

     // Sort by station first, then by actual charge start time
        chargeResults.sort(java.util.Comparator
                .comparingInt((ChargingSim.ChargeResult r) -> r.stationId)
                .thenComparingInt(r -> r.startMin));
        
        System.out.println(
        	    "\n[SIM] Charging results (AGV / Charging station / arrival / Battery % / Waiting time / Charging time in minutes / Schedule start→end ):"
        	);
        	for (var r : chargeResults) {
        	    System.out.printf(
        	        "  %-8s  cs=%-2d  arr=%3d'  batt=%3d%%  wait=%3d'  chg=%3d'  %3d'→%-3d'%n",
        	        r.agvId,
        	        r.stationId,
        	        r.arrivalMin,
        	        r.initialBattery,
        	        r.waitMin,
        	        r.chargeMin,
        	        r.startMin,
        	        r.endMin
        	    );
        	}
        long waited15 = chargeResults.stream().filter(r -> r.waitMin >= 15).count();
        System.out.println("[SIM] AGVs with wait >= 15 min: " + waited15 + " / " + chargeResults.size());

        // -------- 2) Concurrent task execution (still time-scaled) --------
        ConcurrentTasksRunner runner = new ConcurrentTasksRunner(logger);
        var agvs = runner.makeAgvs(K);
        var tasks = runner.makeRandomTasks(M);

        System.out.println("\n[SIM] Running " + M + " tasks across " + K + " AGVs...");
        long t0 = System.currentTimeMillis();
        runner.runParallel(tasks, agvs);
        long t1 = System.currentTimeMillis();
        double simMinutes = (t1 - t0) / (double) ConcurrentTasksRunner.TIME_SCALE_MS_PER_MIN;
        System.out.printf("[SIM] All tasks completed. Elapsed ~%.1f simulated minutes (%.1fs real).%n",
                simMinutes, (t1 - t0) / 1000.0);
    }
}
