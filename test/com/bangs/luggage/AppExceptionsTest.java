package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class AppExceptionsTest {

    @Test
    void parsePositiveIntHandlesBadInput() {
        assertEquals(0, AppExceptions.parsePositiveInt("not-a-number"));
        assertEquals(0, AppExceptions.parsePositiveInt("-5"));
        assertEquals(12, AppExceptions.parsePositiveInt("12"));
    }

    @Test
    void ensureFileExistsRethrowsAsStorageException() {
        assertThrows(AppExceptions.StorageException.class,
                () -> AppExceptions.ensureFileExists(null));
    }

    @Test
    void ensureFileExistsMissingFile() throws Exception {
        Path missing = Path.of("really-not-existing-file-xyz.log");
        AppExceptions.StorageException ex = assertThrows(
                AppExceptions.StorageException.class,
                () -> AppExceptions.ensureFileExists(missing));
        assertNotNull(ex.getCause()); // chained cause (IOException)
    }

    @Test
    void readFirstLineWrapsAsLogException() throws Exception {
        Path tmp = Files.createTempFile("x", ".txt");
        Files.deleteIfExists(tmp); // ensure it doesn't exist
        assertThrows(AppExceptions.LogException.class,
                () -> AppExceptions.readFirstLine(tmp));
    }

    @Test
    void chainedOperationHasCause() {
        AppExceptions.TaskException ex = assertThrows(
                AppExceptions.TaskException.class,
                AppExceptions::performChainedOperation);
        assertTrue(ex.getMessage().contains("Task operation failed"));
        assertNotNull(ex.getCause());
    }
}
