package com.bangs.luggage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Daily logs per equipment type:
 * logs/YYYY/MM/DD/{vehicle|charging|system}/<equipmentId>.log
 *
 * Regex search:
 * - by equipment name (matches on filename stem == id OR contains in content)
 * - by date (folder YYYY/MM/DD)
 */
public class Logger {
    private final Path root = Paths.get("logs");
    private final ZoneId zone = ZoneId.of("Europe/Berlin");

    // filename regex: .../YYYY/MM/DD/(vehicle|charging|system)/NAME.log
    private static final Pattern LOG_PATH_PATTERN = Pattern.compile(
        ".*/\\d{4}/\\d{2}/\\d{2}/(vehicle|charging|system)/[A-Za-z0-9._-]+\\.log$",
        Pattern.CASE_INSENSITIVE
    );

    // log line format: 2025-10-29T09:42:13+02:00 [INFO] [vehicle:TUG-07 | Tug 07] message...
    private static final Pattern LOG_LINE_PATTERN = Pattern.compile(
        "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+\\-]\\d{2}:\\d{2}) \\[(INFO|WARN|ERROR)] \\[(vehicle|charging|system):([^\\]|]+)] (.*)$",
        Pattern.CASE_INSENSITIVE
    );

    public void info(Equipment who, String msg) { write(who, "INFO", msg); }
    public void error(Equipment who, String msg) { write(who, "ERROR", msg); }
    public void warn(Equipment who, String msg) { write(who, "WARN", msg); } 

    private synchronized void write(Equipment who, String level, String msg) {
        try {
            LocalDate today = LocalDate.now(zone);
            Path p = pathFor(today, who);
            Files.createDirectories(p.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(
                    p, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                String scope = who.getType().name().toLowerCase();
                String line = String.format("%s [%s] [%s:%s | %s] %s",
                        OffsetDateTime.now(zone),
                        level,
                        scope,
                        who.getId(),
                        who.getName(),
                        msg);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path pathFor(LocalDate date, Equipment who) {
        String yyyy = String.format("%04d", date.getYear());
        String mm = String.format("%02d", date.getMonthValue());
        String dd = String.format("%02d", date.getDayOfMonth());
        String folder = switch (who.getType()) {
            case VEHICLE -> "vehicle";
            case CHARGING -> "charging";
            case SYSTEM -> "system";
        };
        String name = who.getType() == Equipment.Type.SYSTEM ? "system" : who.getId();
        return root.resolve(Paths.get(yyyy, mm, dd, folder, name + ".log"));
    }

    /** Find all log files for this date. */
    public List<Path> findByDate(LocalDate date) throws IOException {
        Path d = root.resolve(Paths.get(
                String.format("%04d", date.getYear()),
                String.format("%02d", date.getMonthValue()),
                String.format("%02d", date.getDayOfMonth())));
        if (!Files.exists(d)) return List.of();
        try (var s = Files.walk(d)) {
            return s.filter(Files::isRegularFile)
                    .filter(p -> LOG_PATH_PATTERN.matcher(p.toString().replace('\\','/')).matches())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Find logs by equipment *name* or *id*:
     * 1) filename stem equals id or equals normalized name
     * 2) OR any file whose content mentions "[...:id | name]"
     */
    public List<Path> findByEquipmentName(String nameOrId) throws IOException {
        List<Path> matches = new ArrayList<>();
        String needle = normalize(nameOrId);
        try (var s = Files.walk(root)) {
            for (Path p : s.filter(Files::isRegularFile).collect(Collectors.toList())) {
                String fn = p.getFileName().toString();
                if (!fn.endsWith(".log")) continue;
                String stem = fn.substring(0, fn.length() - 4);
                // filename match
                if (normalize(stem).equals(needle)) {
                    matches.add(p);
                    continue;
                }
                // content match via regex
                if (fileContainsNameOrId(p, nameOrId)) {
                    matches.add(p);
                }
            }
        }
        return matches;
    }

    private boolean fileContainsNameOrId(Path p, String nameOrId) {
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            String needleLower = nameOrId.toLowerCase();
            while ((line = br.readLine()) != null) {
                if (LOG_LINE_PATTERN.matcher(line).matches() && line.toLowerCase().contains(needleLower)) {
                    return true;
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    private String normalize(String s) {
        return s.replaceAll("\\s+", "_").toLowerCase();
    }

    /** Read whole file (char stream) into memory as lines. */
    public List<String> open(Path logFile) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(logFile)) {
            List<String> lines = new ArrayList<>();
            String ln;
            while ((ln = br.readLine()) != null) lines.add(ln);
            return lines;
        }
    }
}
