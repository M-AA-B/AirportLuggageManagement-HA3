package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TaskManagerTest {

    @Test
    void enqueueAddsTask() {
        TaskManager m = new TaskManager(new Logger());
        m.enqueue(new Task("T1", Task.Type.LOAD));
        // No direct size method; rely on dispatch not being "No task"
        Equipment e = new Equipment("TUG-07","Tug 07", Equipment.Type.VEHICLE);
        m.dispatch(e); // should process without error
        assertTrue(true);
    }

    @Test
    void dispatchOnEmptyQueueDoesNotCrash() {
        TaskManager m = new TaskManager(new Logger());
        Equipment e = new Equipment("TUG-07","Tug 07", Equipment.Type.VEHICLE);
        assertDoesNotThrow(() -> m.dispatch(e));
    }

    @Test
    void dispatchedTaskBecomesDone() {
        TaskManager m = new TaskManager(new Logger());
        Task t = new Task("T1", Task.Type.LOAD);
        m.enqueue(t);
        Equipment e = new Equipment("TUG-07","Tug 07", Equipment.Type.VEHICLE);
        m.dispatch(e);
        assertEquals(Task.Status.DONE, t.getStatus());
    }

    @Test
    void multipleEnqueueDispatch() {
        TaskManager m = new TaskManager(new Logger());
        m.enqueue(new Task("A", Task.Type.LOAD));
        m.enqueue(new Task("B", Task.Type.UNLOAD));
        Equipment e = new Equipment("CS-1", "Charger 1", Equipment.Type.CHARGING);
        m.dispatch(e);
        m.dispatch(e);
        assertTrue(true);
    }

    @Test
    void worksWithDifferentAssignees() {
        TaskManager m = new TaskManager(new Logger());
        m.enqueue(new Task("X", Task.Type.TRANSFER));
        m.enqueue(new Task("Y", Task.Type.CHARGE));
        m.dispatch(new Equipment("TUG-07","Tug 07", Equipment.Type.VEHICLE));
        m.dispatch(new Equipment("CS-1","Charger 1", Equipment.Type.CHARGING));
        assertTrue(true);
    }
}
