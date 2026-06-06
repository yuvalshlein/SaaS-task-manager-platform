package com.taskmanager;

import com.taskmanager.models.*;
import com.taskmanager.strategies.*;
import com.taskmanager.tasks.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {

    private static final int SEP_WIDTH = 72;

    private static void hr(char c) {
        System.out.println(String.valueOf(c).repeat(SEP_WIDTH));
    }

    private static void header(String title, char c) {
        hr(c);
        System.out.println("  " + title);
        hr(c);
    }

    private static void printSchedule(List<ScheduleEntry> schedule, List<String> warnings) {
        if (schedule.isEmpty()) {
            System.out.println("  (no tasks to schedule)");
            return;
        }
        for (ScheduleEntry e : schedule) {
            String flag = e.isOverloaded ? "  *** OVERLOADED ***" : "";
            System.out.printf("  [%-8s] %-38s -> %-15s (%4.1fh, due %s)%s%n",
                e.priorityName, e.taskTitle, e.userName, e.estimatedHours,
                e.deadline.toLocalDate(), flag);
        }
        if (!warnings.isEmpty()) {
            System.out.println();
            for (String w : warnings) System.out.println("  " + w);
        }
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        // -------------------- boot the platform --------------------
        header("SaaS Task Management Platform -- Boot", '=');
        SaaSPlatform platform = new SaaSPlatform("TaskFlow SaaS");
        System.out.println("  " + platform);
        System.out.println();

        // ------------------- first organization: Apex Engineering -------------------
        header("TENANT 1  .  Apex Engineering", '-');
        Organization apex = platform.createOrganization("Apex Engineering", new PriorityBasedStrategy());
        System.out.println("  Provisioned: " + apex);
        System.out.println();

        User yuval = new User("Yuval Shlein",   "yuval@apex.io",  "senior_dev");
        User mia   = new User("Mia Shlein", "mishmish@apex.io",    "developer");
        User leehe = new User("Leehe Shlein",  "lili@apex.io",  "developer");

        for (User u : List.of(yuval, mia, leehe)) {
            apex.addUser(u);
            System.out.println("  + " + u);
        }
        System.out.println();
        apex.registerObserver(new ManagerNotifier("yuval (Apex Manager)"));

        Task[] apexTasks = {
            TaskFactory.createBug("Login page crashes on Safari",       "Null pointer in auth middleware",           Priority.CRITICAL, now.plusDays(1),  5.0, 5),
            TaskFactory.createBug("Report export truncates at 1000 rows","CSV writer flushes before EOF",            Priority.HIGH,     now.plusDays(3),  3.0, 3),
            TaskFactory.createBug("Memory leak in batch data processor", "Heap grows unbounded",                     Priority.CRITICAL, now.plusDays(2),  6.0, 4),
            TaskFactory.createFeature("OAuth2 / SSO integration",        "Google and Microsoft OIDC providers",      Priority.HIGH,     now.plusDays(7),  10.0, 13, 9),
            TaskFactory.createFeature("Dark mode across all pages",      "CSS custom-property theme toggle",         Priority.MEDIUM,   now.plusDays(14), 8.0,  8,  4),
            TaskFactory.createFeature("Notification preferences panel",  "Per-channel opt-in for email, push, Slack",Priority.LOW,      now.plusDays(21), 4.0,  5,  3),
        };

        System.out.println("  Tasks pushed onto priority queue:");
        for (Task t : apexTasks) {
            apex.addTask(t);
            System.out.printf("    +  [%-8s] %s  (score=%.1f)%n", t.getPriority().name(), t.getTitle(), t.priorityScore());
        }
        System.out.println();

        Task top = apex.peekTopTask();
        System.out.printf("  Heap peek (O(1)): highest-priority task is [%s] '%s'  score=%.1f%n%n",
            top.getPriority().name(), top.getTitle(), top.priorityScore());

        // ---------------- apex: Priority-Based Schedule ----------------
        header("Apex Engineering -- Priority-Based Schedule", '-');
        ScheduleResult r1 = apex.runSchedule();
        printSchedule(r1.entries, r1.warnings);
        System.out.println();

        // ---------------- apex: swap to Deadline-Based Strategy ----------------
        header("Apex Engineering -- Strategy swap -> DeadlineBasedStrategy", '-');
        apex.setStrategy(new DeadlineBasedStrategy());
        System.out.println("  [Strategy Pattern] Active strategy: " + apex.currentStrategyName());
        System.out.println();
        ScheduleResult r2 = apex.runSchedule();
        printSchedule(r2.entries, r2.warnings);
        System.out.println();

        // ------------------- second organization: NovaTech -------------------
        header("TENANT 2  .  NovaTech", '-');
        Organization nova = platform.createOrganization("NovaTech", new DeadlineBasedStrategy());
        System.out.println("  Provisioned: " + nova);
        System.out.println();

        User chen = new User("Chen Shlein",   "chen@novatech.io", "tech_lead");
        User ruth  = new User("Ruth Shlein", "ruth@novatech.io",  "developer");

        for (User u : List.of(chen, ruth)) {
            nova.addUser(u);
            System.out.println("  + " + u);
        }
        System.out.println();
        nova.registerObserver(new ManagerNotifier("chen (NovaTech Manager)"));

        Task[] novaTasks = {
            TaskFactory.createFeature("Billing module (Stripe)",      "Subscription plans, invoices, webhooks", Priority.CRITICAL, now.plusDays(5),  12.0, 13, 10),
            TaskFactory.createBug("Race condition in job scheduler",   "Concurrent writes corrupt job state",    Priority.HIGH,     now.plusDays(2),  7.0,  4),
            TaskFactory.createBug("Password reset 404 intermittent",  "Token validation fails ~15% of the time",Priority.MEDIUM,   now.plusDays(6),  2.0,  2),
            TaskFactory.createFeature("Analytics dashboard v2",        "Drill-down charts with date filters",    Priority.MEDIUM,   now.plusDays(30), 15.0, 13, 7),
            TaskFactory.createFeature("i18n: Spanish, French, German", "Locale files + RTL layout",              Priority.LOW,      now.plusDays(45), 9.0,  8,  5),
        };

        System.out.println("  Tasks pushed onto priority queue:");
        for (Task t : novaTasks) {
            nova.addTask(t);
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(now, t.getDeadline());
            System.out.printf("    +  [%-8s] %s  (due in %dd, %.1fh)%n",
                t.getPriority().name(), t.getTitle(), daysLeft, t.getEstimatedHours());
        }
        System.out.println();

        // ---------------- nova: Deadline-Based Schedule ----------------
        header("NovaTech -- Deadline-Based Schedule", '-');
        ScheduleResult r3 = nova.runSchedule();
        printSchedule(r3.entries, r3.warnings);
        System.out.println();

        // ---------------- nova: swap to Priority-Based Strategy ----------------
        header("NovaTech -- Strategy swap -> PriorityBasedStrategy", '-');
        nova.setStrategy(new PriorityBasedStrategy());
        System.out.println("  [Strategy Pattern] Active strategy: " + nova.currentStrategyName());
        System.out.println();
        ScheduleResult r4 = nova.runSchedule();
        printSchedule(r4.entries, r4.warnings);
        System.out.println();

        // ------------------ Data isolation verification across tenants ----------------
        header("Multi-Tenancy -- Data Isolation Verification", '-');
        Organization apexRef = platform.getOrganization(apex.getId());
        Organization novaRef = platform.getOrganization(nova.getId());

        System.out.println("  Apex users : " + apexRef.listUsers().stream().map(User::getName).toList());
        System.out.println("  Nova users : " + novaRef.listUsers().stream().map(User::getName).toList());
        System.out.println();

        java.util.Set<String> apexIds = new java.util.HashSet<>();
        for (Task t : apexRef.listTasks()) apexIds.add(t.getId());
        java.util.Set<String> novaIds = new java.util.HashSet<>();
        for (Task t : novaRef.listTasks()) novaIds.add(t.getId());
        apexIds.retainAll(novaIds); // keeps only the intersection

        System.out.println("  Task-ID overlap between tenants: " + apexIds.size() + "  (expected: 0)");
        System.out.println("  Data isolation: " + (apexIds.isEmpty() ? "PASS" : "FAIL"));
        System.out.println();

        // ---------------- heap demonstration: popping tasks in priority order ----------------
        header("Heap Demo -- Apex tasks popped in priority order (O(log n) each)", '-');
        System.out.println("  (highest priorityScore() always surfaces first)\n");

        java.util.List<Task> popped = new java.util.ArrayList<>();
        int rank = 1;
        while (apex.taskCount() > 0) {
            Task t = apex.popTopTask();
            popped.add(t);
            System.out.printf("  #%2d  [%-8s]  score=%5.1f  %s%n",
                rank++, t.getPriority().name(), t.priorityScore(), t.getTitle());
        }

        List<Double> scores = popped.stream().map(Task::priorityScore).toList();
        boolean isSorted = scores.equals(scores.stream().sorted(Comparator.reverseOrder()).toList());
        System.out.println("\n  Strict descending order: " + (isSorted ? "YES" : "NO"));

        for (Task t : popped) apex.addTask(t);
        System.out.println("  Heap restored (" + popped.size() + " tasks).");
        System.out.println();

        // ----------------- platform-wide statistics ----------------
        header("Platform Statistics", '=');
        Map<String, Object> stats = platform.platformStats();
        int col = stats.keySet().stream().mapToInt(String::length).max().orElse(10) + 2;
        for (Map.Entry<String, Object> e : stats.entrySet()) {
            System.out.printf("  %-" + col + "s: %s%n", e.getKey(), e.getValue());
        }
        System.out.println();

        hr('=');
        System.out.println("  Simulation complete -- all systems nominal.");
        hr('=');
    }
}
