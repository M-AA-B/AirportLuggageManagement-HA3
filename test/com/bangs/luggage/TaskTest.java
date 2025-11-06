package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void createTaskDefaultsPending() {
        Task t = new Task("T1", Task.Type.LOAD);
        assertEquals(Task.Status.PENDING, t.getStatus());
    }

    @Test
    void withPayloadFluent() {
        Task t = new Task("T1", Task.Type.TRANSFER).with("from","A").with("to","B");
        assertEquals("A", t.getPayload().get("from"));
        assertEquals("B", t.getPayload().get("to"));
    }

    @Test
    void canMoveToRunningAndDone() {
        Task t = new Task("T1", Task.Type.LOAD);
        t.setStatus(Task.Status.RUNNING);
        t.setStatus(Task.Status.DONE);
        assertEquals(Task.Status.DONE, t.getStatus());
    }

    @Test
    void typeEnumValues() {
        assertNotNull(Task.Type.valueOf("CHARGE"));
    }

    @Test
    void toStringHasId() {
        Task t = new Task("IDX", Task.Type.UNLOAD);
        assertTrue(t.toString().contains("IDX"));
    }
}
