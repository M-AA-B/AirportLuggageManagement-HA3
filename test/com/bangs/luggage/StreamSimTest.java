package com.bangs.luggage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class StreamSimTest {

    @Test
    void binaryWriteReadRoundTrip() throws Exception {
        StreamSim s = new StreamSim();
        byte[] b = s.writeLuggageBinaryToBytes("BG1","F1","NAME", 10);
        Map<String,Object> m = s.readLuggageBinaryFromBytes(b);
        assertEquals("BG1", m.get("id"));
        assertEquals(10, m.get("weightKg"));
    }

    @Test
    void binaryHandlesDifferentData() throws Exception {
        StreamSim s = new StreamSim();
        byte[] b = s.writeLuggageBinaryToBytes("X","FX","O", 1);
        Map<String,Object> m = s.readLuggageBinaryFromBytes(b);
        assertEquals("X", m.get("id"));
    }

    @Test
    void protocolWriteRead() throws Exception {
        StreamSim s = new StreamSim();
        String out = s.writeProtocolToString("ACK 1");
        List<String> lines = s.readProtocolFromString(out);
        assertEquals(1, lines.size());
        assertEquals("ACK 1", lines.get(0));
    }

    @Test
    void protocolMultipleLines() throws Exception {
        StreamSim s = new StreamSim();
        String out = s.writeProtocolToString("L1");
        out += s.writeProtocolToString("L2");
        List<String> lines = s.readProtocolFromString(out);
        assertEquals(2, lines.size());
    }

    @Test
    void protocolNotNull() throws Exception {
        StreamSim s = new StreamSim();
        assertNotNull(s.writeProtocolToString("X"));
    }
}
