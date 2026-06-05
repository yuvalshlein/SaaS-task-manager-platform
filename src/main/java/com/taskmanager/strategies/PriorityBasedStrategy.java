package com.taskmanager.strategies;

import com.taskmanager.models.User;
import com.taskmanager.tasks.Task;

import java.util.*;

public class PriorityBasedStrategy extends SchedulingStrategy {

    @Override
    public ScheduleResult schedule(List<Task> tasks, Map<String, User> users) {
        List<User> active = activeUsers(users);
        if (active.isEmpty()) {
            return new ScheduleResult(Collections.emptyList(),
                List.of("[WARNING] No active users - schedule is empty."));
        }

        // Sort descending by priority score (highest priority task first).
        List<Task> ordered = new ArrayList<>(tasks);
        ordered.sort((a, b) -> Double.compare(b.priorityScore(), a.priorityScore()));

        Map<String, Double> workload = new HashMap<>();
        for (User u : active) workload.put(u.getId(), 0.0);

        List<ScheduleEntry> schedule = new ArrayList<>();
        for (Task task : ordered) {
            User target = minLoadUser(active, workload);
            workload.merge(target.getId(), task.getEstimatedHours(), Double::sum);

            schedule.add(new ScheduleEntry(
                target.getId(), target.getName(),
                task.getId(), task.getTitle(),
                task.taskType().name().toLowerCase(),
                task.getPriority().name(),
                task.getEstimatedHours(), task.getDeadline(),
                task.priorityScore()
            ));
        }

        List<String> warnings = detectOverload(workload, users);

        Set<String> overloadedIds = new HashSet<>();
        for (Map.Entry<String, Double> e : workload.entrySet()) {
            if (e.getValue() > OVERLOAD_THRESHOLD_HOURS) overloadedIds.add(e.getKey());
        }
        for (ScheduleEntry entry : schedule) {
            entry.isOverloaded = overloadedIds.contains(entry.userId);
        }

        return new ScheduleResult(schedule, warnings);
    }
}
