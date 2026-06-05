package com.taskmanager.tasks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BugTask extends Task {

    private static final int MIN_SEVERITY = 1;
    private static final int MAX_SEVERITY = 5;
    private static final double PRIORITY_MULTIPLIER = 10.0;
    private static final double SEVERITY_MULTIPLIER = 2.0;
    private static final double URGENCY_WINDOW = 30.0;

    private final int severity;

    public BugTask(String title, String description, Priority priority,
                   LocalDateTime deadline, double estimatedHours, int severity) throws IllegalArgumentException {
        super(title, description, priority, deadline, estimatedHours, null);
        if (severity < MIN_SEVERITY || severity > MAX_SEVERITY)
            throw new IllegalArgumentException("BugTask severity must be 1-5, got " + severity);
        this.severity = severity;
    }

    @Override
    public TaskType taskType() { return TaskType.BUG; }

    @Override
    public double priorityScore() {
        double base          = priority.getValue() * PRIORITY_MULTIPLIER;
        double severityBoost = severity * SEVERITY_MULTIPLIER;
        long daysLeft        = ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);
        double urgency       = Math.max(0.0, URGENCY_WINDOW - Math.max(0, daysLeft));
        return base + severityBoost + urgency;
    }

    public int getSeverity() { return severity; }
}
