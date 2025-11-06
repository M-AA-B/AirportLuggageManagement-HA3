package com.bangs.luggage;

import java.util.HashMap;
import java.util.Map;

public class Task {
    public enum Type { LOAD, UNLOAD, TRANSFER, CHARGE }
    public enum Status { PENDING, RUNNING, DONE, FAILED }

    private final String id;
    private final Type type;
    private Status status = Status.PENDING;
    private final Map<String, String> payload = new HashMap<>();

    public Task(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public Status getStatus() { return status; }
    public void setStatus(Status s) { this.status = s; }
    public Map<String, String> getPayload() { return payload; }

    public Task with(String key, String value) {
        payload.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Task{id='" + id + "', type=" + type + ", status=" + status + ", payload=" + payload + "}";
    }
}
