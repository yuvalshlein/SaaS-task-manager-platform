package com.taskmanager.strategies;

import com.taskmanager.models.User;
import com.taskmanager.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class DeadlineBasedStrategy extends SchedulingStrategy {

    @Override
    public ScheduleResult schedule(List<Task> tasks, Map<String, User> users) {
        List<User> active = activeUsers(users);
        if (active.isEmpty()) {
            return new ScheduleResult(Collections.emptyList(),
                List.of("[WARNING] No active users - schedule is empty."));
        }

        // Sort ascending by deadline (earliest deadline first).
        List<Task> ordered = new ArrayList<>(tasks);
        ordered.sort(Comparator.comparing(Task::getDeadline));

        Map<String, Double> workload = new HashMap<>();
        for (User u : active) workload.put(u.getId(), 0.0);

        List<ScheduleEntry> schedule = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Task task : ordered) {
            User target = minLoadUser(active, workload);
            workload.merge(target.getId(), task.getEstimatedHours(), Double::sum); // add the estimated hours or set it not present

            schedule.add(new ScheduleEntry(
                target.getId(), target.getName(),
                task.getId(), task.getTitle(),
                task.taskType().name().toLowerCase(),
                task.getPriority().name(),
                task.getEstimatedHours(), task.getDeadline(),
                task.priorityScore()
            ));
        }

        List<String> warnings = new ArrayList<>(detectOverload(workload, users));

        for (Task task : ordered) {
            if (task.getDeadline().isBefore(now)) {
                warnings.add(String.format("[DEADLINE BREACH] '%s' - deadline was %s (already passed)",
                    task.getTitle(), task.getDeadline().toLocalDate()));
            }
        }

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
