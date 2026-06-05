package com.taskmanager.strategies;

import com.taskmanager.models.User;
import com.taskmanager.tasks.Task;

import java.util.*;

public abstract class SchedulingStrategy {

    protected static final double OVERLOAD_THRESHOLD_HOURS = 8.0;

    public abstract ScheduleResult schedule(List<Task> tasks, Map<String, User> users);

    protected List<String> detectOverload(Map<String, Double> userWorkload, Map<String, User> users) {
        List<String> warnings = new ArrayList<>();
        for (Map.Entry<String, Double> entry : userWorkload.entrySet()) {
            if (entry.getValue() > OVERLOAD_THRESHOLD_HOURS) {
                User user = users.get(entry.getKey());
                warnings.add(String.format("[OVERLOAD] '%s' carries %.1fh (threshold: %.1fh/day)",
                    user.getName(), entry.getValue(), OVERLOAD_THRESHOLD_HOURS));
            }
        }
        return warnings;
    }

    protected List<User> activeUsers(Map<String, User> users) {
        return new ArrayList<>(users.values());
    }

    protected User minLoadUser(List<User> candidates, Map<String, Double> workload) {
        User minUser = candidates.get(0);
        for (User u : candidates) {
            if (workload.get(u.getId()) < workload.get(minUser.getId())) {
                minUser = u;
            }
        }
        return minUser;
    }
}
