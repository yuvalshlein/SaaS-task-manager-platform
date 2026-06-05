package com.taskmanager.tasks;

import java.time.LocalDateTime;

public class TaskFactory {

    private TaskFactory() {}

    public static BugTask createBug(String title, String description, Priority priority,
                                    LocalDateTime deadline, double estimatedHours, int severity) {
        return new BugTask(title, description, priority, deadline, estimatedHours, severity);
    }

    public static FeatureTask createFeature(String title, String description, Priority priority,
                                            LocalDateTime deadline, double estimatedHours,
                                            int storyPoints, int businessImpact) {
        return new FeatureTask(title, description, priority, deadline, estimatedHours, storyPoints, businessImpact);
    }
}
