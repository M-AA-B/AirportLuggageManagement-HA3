package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class LoggerTest {

    @Test
    void writesVehicleLog() throws Exception {
        Logger log = new Logger();
        Equipment tug = new Equipment("TUG-07","Tug 07", Equipment.Type.VEHICLE);
        log.info(tug, "hello");
        List<Path> today = log.findByDate(LocalDate.now());
        assertTrue(today.stream().anyMatch(p -> p.toString().contains("vehicle")));
    }

    @Test
    void writesSystemLog() throws Exception {
        Logger log = new Logger();
        Equipment sys = new Equipment("system","System", Equipment.Type.SYSTEM);
        log.error(sys, "boom");
        assertTrue(log.findByDate(LocalDate.now()).size() > 0);
    }

    @Test
    void openByEquipmentName() throws Exception {
        Logger log = new Logger();
        Equipment tug = new Equipment("TUG-TEST","Tug Test", Equipment.Type.VEHICLE);
        log.info(tug, "msg");
        List<Path> found = log.findByEquipmentName("TUG-TEST");
        assertFalse(found.isEmpty());
        assertFalse(log.open(found.get(0)).isEmpty());
    }

    @Test
    void openNonExistingReturnsEmptyList() throws Exception {
        Logger log = new Logger();
        assertTrue(log.findByEquipmentName("NOPE-NOPE").isEmpty());
    }

    @Test
    void logFilesAreReadable() throws Exception {
        Logger log = new Logger();
        Equipment ch = new Equipment("CS-J1","Charger J1", Equipment.Type.CHARGING);
        log.info(ch, "charging");
        List<Path> today = log.findByDate(LocalDate.now());
        // pick one file and read at least one line
        Path any = today.get(0);
        assertTrue(Files.size(any) > 0);
        assertFalse(log.open(any).isEmpty());
    }
}
