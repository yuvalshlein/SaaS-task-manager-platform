package com.taskmanager.strategies;

import java.util.List;

// Java can't return two values from a method, so this bundles the schedule + warnings together.
public class ScheduleResult {

    public final List<ScheduleEntry> entries;
    public final List<String> warnings;

    public ScheduleResult(List<ScheduleEntry> entries, List<String> warnings) {
        this.entries  = entries;
        this.warnings = warnings;
    }
}
