package com.taskmanager.tasks;

public enum Priority {
    LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);

    private final int value;

    Priority(int value) { this.value = value; }

    public int getValue() { return value; }
}
