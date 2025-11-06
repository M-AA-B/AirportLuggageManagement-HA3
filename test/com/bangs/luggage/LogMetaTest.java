package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogMetaTest {

    @Test
    void createFile() throws Exception {
        LogMeta m = new LogMeta();
        Path tmp = Files.createTempDirectory("lg").resolve("x.log");
        Path created = m.create(tmp);
        assertTrue(Files.exists(created));
        Files.deleteIfExists(created);
        Files.deleteIfExists(created.getParent());
    }

    @Test
    void moveFile() throws Exception {
        LogMeta m = new LogMeta();
        Path dir = Files.createTempDirectory("lg");
        Path a = dir.resolve("a.log");
        Path b = dir.resolve("b.log");
        Files.writeString(a, "x");
        m.move(a, b);
        assertTrue(Files.exists(b));
        Files.deleteIfExists(b);
        Files.deleteIfExists(dir);
    }

    @Test
    void deleteFile() throws Exception {
        LogMeta m = new LogMeta();
        Path dir = Files.createTempDirectory("lg");
        Path a = dir.resolve("a.log");
        Files.writeString(a, "x");
        m.delete(a);
        assertFalse(Files.exists(a));
        Files.deleteIfExists(dir);
    }

    @Test
    void archiveZip() throws Exception {
        LogMeta m = new LogMeta();
        Path dir = Files.createTempDirectory("lg");
        Path a = dir.resolve("a.log");
        Files.writeString(a, "x");
        Path zip = dir.resolve("out.zip");
        m.archiveToZip(zip, a);
        assertTrue(Files.exists(zip));
        Files.deleteIfExists(a);
        Files.deleteIfExists(zip);
        Files.deleteIfExists(dir);
    }

    @Test
    void archiveIgnoresMissing() throws Exception {
        LogMeta m = new LogMeta();
        Path dir = Files.createTempDirectory("lg");
        Path zip = dir.resolve("out.zip");
        m.archiveToZip(zip, dir.resolve("missing.log"));
        assertTrue(Files.exists(zip));
        Files.deleteIfExists(zip);
        Files.deleteIfExists(dir);
    }
}
