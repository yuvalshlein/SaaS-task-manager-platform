package com.taskmanager.strategies;

import java.util.List;

public interface SchedulingObserver {
    void onOverload(String orgName, List<String> warnings);
}
