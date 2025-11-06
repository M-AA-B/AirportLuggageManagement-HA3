package com.bangs.luggage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Centralized examples of Assignment-2 exception patterns:
 *  a) Handling multiple exceptions
 *  b) Re-throwing exceptions
 *  c) Resource Management (try-with-resources)
 *  d) Chaining exceptions
 */
public final class AppExceptions {

    // a) Handling Multiple Exceptions (checked + unchecked)
	public static int parsePositiveInt(String s) {
	    try {
	        int v = Integer.parseInt(s);   // may throw NumberFormatException or NullPointerException
	        if (v < 0) throw new IllegalArgumentException("negative: " + v);
	        return v;
	    } 
	    // valid multi-catch (unrelated types)
	    catch (NumberFormatException | NullPointerException ex) {
	        return 0; // invalid input or null handled together
	    } 
	    // separate catch for IllegalArgumentException (negative case)
	    catch (IllegalArgumentException ex) {
	        return 0;
	    }
	}


    // b) Re-throwing Exceptions (wrap low-level into domain exception)
    public static void ensureFileExists(Path p) throws StorageException {
        try {
            if (p == null) throw new NullPointerException("path is null");
            if (!Files.exists(p)) throw new IOException("Missing file: " + p);
        } catch (IOException | NullPointerException e) {
            // Re-throw as our domain-specific exception
            throw new StorageException("File precondition failed for " + p, e);
        }
    }

    // c) Resource Management (try-with-resources)
    public static String readFirstLine(Path p) throws LogException {
        try (BufferedReader br = Files.newBufferedReader(p)) {
            return br.readLine();
        } catch (IOException e) {
            // Wrap into domain log exception
            throw new LogException("Failed to read first line from " + p, e);
        }
    }

    // d) Chaining Exceptions (explicit cause chain)
    public static void performChainedOperation() throws TaskException {
        try {
            // inner cause
            throw new IllegalStateException("inner failure");
        } catch (IllegalStateException inner) {
            // chain cause into a more specific domain exception
            throw new TaskException("Task operation failed", inner);
        }
    }

    // --- Simple domain exceptions used above ---

    public static class StorageException extends Exception {
        public StorageException(String msg) { super(msg); }
        public StorageException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class LogException extends Exception {
        public LogException(String msg) { super(msg); }
        public LogException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class TaskException extends Exception {
        public TaskException(String msg) { super(msg); }
        public TaskException(String msg, Throwable cause) { super(msg, cause); }
    }
}
