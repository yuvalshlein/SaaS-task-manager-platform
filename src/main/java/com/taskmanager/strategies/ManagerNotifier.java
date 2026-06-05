package com.taskmanager.strategies;

import java.util.List;

public class ManagerNotifier implements SchedulingObserver {

    private final String managerName;

    public ManagerNotifier(String managerName) {
        this.managerName = managerName;
    }

    @Override
    public void onOverload(String orgName, List<String> warnings) {
        System.out.printf("%n  [ALERT -> %s] Overload detected in '%s':%n", managerName, orgName);
        for (String w : warnings) {
            System.out.println("    " + w);
        }
    }
}
