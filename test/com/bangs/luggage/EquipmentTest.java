package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EquipmentTest {

    @Test
    void gettersWork() {
        Equipment e = new Equipment("TUG-07", "Tug 07", Equipment.Type.VEHICLE);
        assertEquals("TUG-07", e.getId());
        assertEquals("Tug 07", e.getName());
        assertEquals(Equipment.Type.VEHICLE, e.getType());
    }

    @Test
    void toStringContainsTypeAndId() {
        Equipment e = new Equipment("CS-1", "Charger 1", Equipment.Type.CHARGING);
        String s = e.toString();
        assertTrue(s.contains("CHARGING"));
        assertTrue(s.contains("CS-1"));
    }

    @Test
    void systemEquipment() {
        Equipment e = new Equipment("system", "System", Equipment.Type.SYSTEM);
        assertEquals(Equipment.Type.SYSTEM, e.getType());
    }

    @Test
    void differentObjectsNotEqual() {
        Equipment a = new Equipment("A", "X", Equipment.Type.VEHICLE);
        Equipment b = new Equipment("B", "Y", Equipment.Type.VEHICLE);
        assertNotEquals(a.toString(), b.toString());
    }

    @Test
    void vehiclesVsChargingType() {
        assertNotEquals(Equipment.Type.VEHICLE, Equipment.Type.CHARGING);
    }
}
