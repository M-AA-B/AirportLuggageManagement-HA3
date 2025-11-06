package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StorageManagerTest {

    @Test
    void placeAndLocate() {
        Logger log = new Logger();
        StorageManager sm = new StorageManager(log);
        sm.place("BG1", "CONVEYOR-1");
        assertEquals("CONVEYOR-1", sm.locate("BG1"));
    }

    @Test
    void locateUnknownReturnsNull() {
        StorageManager sm = new StorageManager(new Logger());
        assertNull(sm.locate("NOT-THERE"));
    }

    @Test
    void snapshotReflectsPlacements() {
        StorageManager sm = new StorageManager(new Logger());
        sm.place("BG2", "BIN-1");
        assertTrue(sm.snapshot().containsKey("BG2"));
    }

    @Test
    void snapshotIsUnmodifiable() {
        StorageManager sm = new StorageManager(new Logger());
        sm.place("BG3", "CAROUSEL-2");
        assertThrows(UnsupportedOperationException.class, () -> sm.snapshot().put("X","Y"));
    }

    @Test
    void multiplePlacementsUpdateLocation() {
        StorageManager sm = new StorageManager(new Logger());
        sm.place("BG4", "A");
        sm.place("BG4", "B");
        assertEquals("B", sm.locate("BG4"));
    }
}
