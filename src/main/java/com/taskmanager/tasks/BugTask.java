package com.taskmanager.tasks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BugTask extends Task {

    private final int severity;

    public BugTask(String title, String description, Priority priority,
                   LocalDateTime deadline, double estimatedHours, int severity) {
        super(title, description, priority, deadline, estimatedHours, null);
        if (severity < 1 || severity > 5)
            throw new IllegalArgumentException("BugTask severity must be 1-5, got " + severity);
        this.severity = severity;
    }

    @Override
    public TaskType taskType() { return TaskType.BUG; }

    @Override
    public double priorityScore() {
        double base          = priority.getValue() * 10.0;   // 10-40
        double severityBoost = severity * 2.0;               // 2-10
        long daysLeft        = ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);
        double urgency       = Math.max(0.0, 30.0 - Math.max(0, daysLeft));
        return base + severityBoost + urgency;
    }

    public int getSeverity() { return severity; }
}
