package com.taskmanager.models;

import java.util.UUID;

public class User {

    private final String id;
    private final String name;
    private final String email;
    private final String role;
    private final double dailyCapacityHours;

    public User(String name, String email, String role, double dailyCapacityHours) {
        this.id                   = UUID.randomUUID().toString().substring(0, 8);
        this.name                 = name;
        this.email                = email;
        this.role                 = role;
        this.dailyCapacityHours   = dailyCapacityHours;
    }

    public User(String name, String email, String role) {
        this(name, email, role, 8.0);
    }

    public String getId()                 { return id; }
    public String getName()               { return name; }
    public String getEmail()              { return email; }
    public String getRole()               { return role; }
    public double getDailyCapacityHours() { return dailyCapacityHours; }

    @Override
    public String toString() {
        return String.format("User(id=%s, name='%s', role=%s, capacity=%.1fh/day)",
            id, name, role, dailyCapacityHours);
    }
}
