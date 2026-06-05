package com.taskmanager.tasks;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Task implements Comparable<Task> {

    protected final String id;
    protected final String title;
    protected final String description;
    protected final Priority priority;
    protected final LocalDateTime deadline;
    protected final double estimatedHours;
    protected String assignedTo;
    protected TaskStatus status;

    protected Task(String title, String description, Priority priority,
                   LocalDateTime deadline, double estimatedHours, String assignedTo) {
        this.id             = UUID.randomUUID().toString().substring(0, 8);
        this.title          = title;
        this.description    = description;
        this.priority       = priority;
        this.deadline       = deadline;
        this.estimatedHours = estimatedHours;
        this.assignedTo     = assignedTo;
        this.status         = TaskStatus.PENDING;
    }

    public abstract TaskType taskType();
    public abstract double priorityScore();

    // Inverted so PriorityQueue (min-heap) surfaces the HIGHEST score first.
    @Override
    public int compareTo(Task other) {
        return Double.compare(other.priorityScore(), this.priorityScore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        return id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return String.format("%s(id=%s, title='%s', priority=%s, deadline=%s, hours=%.1fh, score=%.1f)",
            getClass().getSimpleName(), id, title, priority,
            deadline.toLocalDate(), estimatedHours, priorityScore());
    }

    public String getId()               { return id; }
    public String getTitle()            { return title; }
    public Priority getPriority()       { return priority; }
    public LocalDateTime getDeadline()  { return deadline; }
    public double getEstimatedHours()   { return estimatedHours; }
}
