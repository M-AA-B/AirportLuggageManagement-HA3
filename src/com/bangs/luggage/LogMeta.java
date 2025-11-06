package com.bangs.luggage;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** File metadata operations: create, move, delete, archive-to-zip. */
public class LogMeta {

    public Path create(Path p) throws IOException {
        Objects.requireNonNull(p, "path");
        Files.createDirectories(p.getParent());
        return Files.exists(p) ? p : Files.createFile(p);
    }

    public Path move(Path src, Path dst) throws IOException {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(dst, "dst");
        Files.createDirectories(dst.getParent());
        return Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    public void delete(Path p) throws IOException {
        Objects.requireNonNull(p, "path");
        Files.deleteIfExists(p);
    }

    public Path archiveToZip(Path targetZip, Path... files) throws IOException {
        Objects.requireNonNull(targetZip, "targetZip");
        Files.createDirectories(targetZip.getParent());
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetZip))) {
            for (Path f : files) {
                if (f == null || !Files.exists(f) || Files.isDirectory(f)) continue;
                String entryName = f.toString().replace('\\', '/');
                zos.putNextEntry(new ZipEntry(entryName));
                Files.copy(f, zos);
                zos.closeEntry();
            }
        }
        return targetZip;
    }
}
