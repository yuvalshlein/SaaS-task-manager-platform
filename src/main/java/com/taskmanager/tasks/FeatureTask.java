package com.taskmanager.tasks;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FeatureTask extends Task {

    private static final int    MIN_STORY_POINTS      = 1;
    private static final int    MIN_BUSINESS_IMPACT   = 1;
    private static final int    MAX_BUSINESS_IMPACT   = 10;
    private static final double PRIORITY_MULTIPLIER   = 10.0;
    private static final int    MAX_STORY_POINTS_CAP  = 13;
    private static final double COMPLEXITY_MULTIPLIER = 0.5;
    private static final double IMPACT_MULTIPLIER     = 2.0;
    private static final double URGENCY_WINDOW        = 30.0;

    private final int storyPoints;
    private final int businessImpact;

    public FeatureTask(String title, String description, Priority priority,
                       LocalDateTime deadline, double estimatedHours,
                       int storyPoints, int businessImpact) throws IllegalArgumentException {
        super(title, description, priority, deadline, estimatedHours, null);
        if (storyPoints < MIN_STORY_POINTS)
            throw new IllegalArgumentException("storyPoints must be >= 1, got " + storyPoints);
        if (businessImpact < MIN_BUSINESS_IMPACT || businessImpact > MAX_BUSINESS_IMPACT)
            throw new IllegalArgumentException("businessImpact must be 1-10, got " + businessImpact);
        this.storyPoints    = storyPoints;
        this.businessImpact = businessImpact;
    }

    @Override
    public TaskType taskType() { return TaskType.FEATURE; }

    @Override
    public double priorityScore() {
        double base             = priority.getValue() * PRIORITY_MULTIPLIER;
        double complexityFactor = Math.min(storyPoints, MAX_STORY_POINTS_CAP) * COMPLEXITY_MULTIPLIER;
        double impactFactor     = businessImpact * IMPACT_MULTIPLIER;
        long daysLeft           = ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);
        double urgency          = Math.max(0.0, URGENCY_WINDOW - Math.max(0, daysLeft));
        return base + complexityFactor + impactFactor + urgency;
    }

    public int getStoryPoints()    { return storyPoints; }
    public int getBusinessImpact() { return businessImpact; }
}
