package com.bangs.luggage;

public class Equipment {
    public enum Type { VEHICLE, CHARGING, SYSTEM }

    private final String id;
    private final String name;
    private final Type type;

    public Equipment(String id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }

    @Override
    public String toString() {
        return type + ":" + id + "(" + name + ")";
    }
}
