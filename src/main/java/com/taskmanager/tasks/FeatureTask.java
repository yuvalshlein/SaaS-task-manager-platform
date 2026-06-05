package com.taskmanager.tasks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FeatureTask extends Task {

    private final int storyPoints;
    private final int businessImpact;

    public FeatureTask(String title, String description, Priority priority,
                       LocalDateTime deadline, double estimatedHours,
                       int storyPoints, int businessImpact) throws IllegalArgumentException {
        super(title, description, priority, deadline, estimatedHours, null);
        if (storyPoints < 1)
            throw new IllegalArgumentException("storyPoints must be >= 1, got " + storyPoints);
        if (businessImpact < 1 || businessImpact > 10)
            throw new IllegalArgumentException("businessImpact must be 1-10, got " + businessImpact);
        this.storyPoints    = storyPoints;
        this.businessImpact = businessImpact;
    }

    @Override
    public TaskType taskType() { return TaskType.FEATURE; }

    @Override
    public double priorityScore() {
        double base             = priority.getValue() * 10.0;           // 10-40
        double complexityFactor = Math.min(storyPoints, 13) * 0.5;     // 0.5-6.5
        double impactFactor     = businessImpact * 2.0;                 // 2-20
        long daysLeft           = ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);
        double urgency          = Math.max(0.0, 30.0 - Math.max(0, daysLeft));
        return base + complexityFactor + impactFactor + urgency;
    }

    public int getStoryPoints()    { return storyPoints; }
    public int getBusinessImpact() { return businessImpact; }
}
