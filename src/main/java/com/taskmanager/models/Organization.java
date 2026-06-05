package com.taskmanager.models;

import com.taskmanager.strategies.*;
import com.taskmanager.tasks.Task;

import java.util.*;

public class Organization {

    private static final int ID_LENGTH = 8;

    private final String id;
    private final String name;
    private SubscriptionTier tier;
    private final Map<String, User> users    = new HashMap<>();
    private final PriorityQueue<Task> taskHeap = new PriorityQueue<>(); // max-heap via Task.compareTo
    private final Map<String, Task> tasksMap   = new HashMap<>();
    private SchedulingStrategy strategy;
    private final List<SchedulingObserver> observers = new ArrayList<>();

    public Organization(String name, SchedulingStrategy strategy, SubscriptionTier tier) {
        this.id = UUID.randomUUID().toString().substring(0, ID_LENGTH);
        this.name = name;
        this.strategy = strategy != null ? strategy : new PriorityBasedStrategy();
        this.tier = tier;
    }

    public Organization(String name, SchedulingStrategy strategy) {
        this(name, strategy, SubscriptionTier.FREE);
    }

    // -- User management --

    public void addUser(User user) throws IllegalArgumentException {
        if (users.containsKey(user.getId()))
            throw new IllegalArgumentException("User '" + user.getId() + "' already belongs to '" + name + "'");
        users.put(user.getId(), user);
    }

    public User getUser(String userId) throws NoSuchElementException {
        User user = users.get(userId);
        if (user == null)
            throw new NoSuchElementException("User '" + userId + "' not found in organization '" + name + "'");
        return user;
    }

    public void removeUser(String userId) throws NoSuchElementException {
        if (!users.containsKey(userId))
            throw new NoSuchElementException("User '" + userId + "' not found in organization '" + name + "'");
        users.remove(userId);
    }

    public List<User> listUsers() { return new ArrayList<>(users.values()); }

    // -- Task management --

    public void addTask(Task task) throws IllegalArgumentException {
        if (tasksMap.containsKey(task.getId()))
            throw new IllegalArgumentException("Task '" + task.getId() + "' already exists in '" + name + "'");
        taskHeap.offer(task);
        tasksMap.put(task.getId(), task);
    }

    public Task peekTopTask() { return taskHeap.peek(); }

    public Task popTopTask() throws NoSuchElementException {
        Task task = taskHeap.poll();
        if (task != null) tasksMap.remove(task.getId());
        return task;
    }

    public Task getTask(String taskId) throws NoSuchElementException {
        Task task = tasksMap.get(taskId);
        if (task == null)
            throw new NoSuchElementException("Task '" + taskId + "' not found in organization '" + name + "'");
        return task;
    }

    public List<Task> listTasks() {
        List<Task> sorted = new ArrayList<>(taskHeap);
        sorted.sort((a, b) -> Double.compare(b.priorityScore(), a.priorityScore()));
        return sorted;
    }

    public int taskCount() { return taskHeap.size(); }

    // -- Strategy --

    public void setStrategy(SchedulingStrategy strategy) { this.strategy = strategy; }

    public String currentStrategyName() { return strategy.getClass().getSimpleName(); }

    public ScheduleResult runSchedule() {
        ScheduleResult result = strategy.schedule(new ArrayList<>(taskHeap), users);

        List<String> overloads = new ArrayList<>();
        for (String w : result.warnings) {
            if (w.contains("[OVERLOAD]")) overloads.add(w);
        }
        if (!overloads.isEmpty()) {
            for (SchedulingObserver observer : observers) {
                observer.onOverload(name, overloads);
            }
        }
        return result;
    }

    // -- Observer --

    public void registerObserver(SchedulingObserver observer) { observers.add(observer); }
    public void removeObserver(SchedulingObserver observer)   { observers.remove(observer); }

    // -- Getters --

    public String getId() { return id; }
    public String getName() { return name; }
    public SubscriptionTier getTier() { return tier; }

    @Override
    public String toString() {
        return String.format("Organization(id=%s, name='%s', tier=%s, users=%d, tasks=%d, strategy=%s)",
            id, name, tier.name().toLowerCase(), users.size(), taskCount(), currentStrategyName());
    }
}
