package com.taskmanager.strategies;

import java.time.LocalDateTime;

public class ScheduleEntry {

    public final String userId;
    public final String userName;
    public final String taskId;
    public final String taskTitle;
    public final String taskType;
    public final String priorityName;
    public final double estimatedHours;
    public final LocalDateTime deadline;
    public final double score;
    public boolean isOverloaded;

    public ScheduleEntry(String userId, String userName, String taskId, String taskTitle,
                         String taskType, String priorityName, double estimatedHours,
                         LocalDateTime deadline, double score) {
        this.userId         = userId;
        this.userName       = userName;
        this.taskId         = taskId;
        this.taskTitle      = taskTitle;
        this.taskType       = taskType;
        this.priorityName   = priorityName;
        this.estimatedHours = estimatedHours;
        this.deadline       = deadline;
        this.score          = score;
        this.isOverloaded   = false;
    }
}
