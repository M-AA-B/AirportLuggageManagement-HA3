package com.bangs.luggage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Demonstrates byte and character streams as required. */
public class StreamSim {

    // ----- BYTE STREAMS -----
    public void writeLuggageBinary(OutputStream out, String id, String flight, String owner, int weightKg) throws IOException {
        try (DataOutputStream data = new DataOutputStream(out)) {
            data.writeUTF(id);
            data.writeUTF(flight);
            data.writeUTF(owner);
            data.writeInt(weightKg);
        }
    }
    public Map<String, Object> readLuggageBinary(InputStream in) throws IOException {
        try (DataInputStream data = new DataInputStream(in)) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", data.readUTF());
            m.put("flight", data.readUTF());
            m.put("owner", data.readUTF());
            m.put("weightKg", data.readInt());
            return m;
        }
    }
    // Helpers that operate on byte arrays for easy demo in Main:
    public byte[] writeLuggageBinaryToBytes(String id, String flight, String owner, int weightKg) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        writeLuggageBinary(bout, id, flight, owner, weightKg);
        return bout.toByteArray();
    }
    public Map<String, Object> readLuggageBinaryFromBytes(byte[] bytes) throws IOException {
        return readLuggageBinary(new ByteArrayInputStream(bytes));
    }

    // ----- CHARACTER STREAMS -----
    public void writeProtocol(Writer w, String line) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(w)) {
            bw.write(line);
            bw.newLine();
        }
    }
    public List<String> readProtocol(Reader r) throws IOException {
        try (BufferedReader br = new BufferedReader(r)) {
            return br.lines().collect(Collectors.toList());
        }
    }
    // Helpers for easy demo in Main:
    public String writeProtocolToString(String line) throws IOException {
        StringWriter sw = new StringWriter();
        writeProtocol(sw, line);
        return sw.toString();
    }
    public List<String> readProtocolFromString(String s) throws IOException {
        StringReader sr = new StringReader(s);
        return readProtocol(sr);
    }
}
