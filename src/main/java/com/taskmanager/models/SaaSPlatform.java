package com.taskmanager.models;

import com.taskmanager.strategies.SchedulingStrategy;

import java.util.*;

public class SaaSPlatform {

    private final String platformName;
    private final Map<String, Organization> orgs = new HashMap<>();

    public SaaSPlatform(String platformName) {
        this.platformName = platformName;
    }

    public Organization createOrganization(String name, SchedulingStrategy strategy, SubscriptionTier tier) {
        Organization org = new Organization(name, strategy, tier);
        orgs.put(org.getId(), org);
        return org;
    }

    public Organization createOrganization(String name, SchedulingStrategy strategy) {
        return createOrganization(name, strategy, SubscriptionTier.FREE);
    }

    public Organization getOrganization(String orgId) throws NoSuchElementException {
        Organization org = orgs.get(orgId);
        if (org == null)
            throw new NoSuchElementException("Organization '" + orgId + "' not found on platform '" + platformName + "'");
        return org;
    }

    public void deleteOrganization(String orgId) throws NoSuchElementException {
        if (!orgs.containsKey(orgId))
            throw new NoSuchElementException("Cannot delete: organization '" + orgId + "' does not exist.");
        orgs.remove(orgId);
    }

    public List<Organization> listOrganizations() { return new ArrayList<>(orgs.values()); }

    public int organizationCount() { return orgs.size(); }

    public Map<String, Object> platformStats() {
        int totalUsers = 0;
        for (Organization o : orgs.values()) {
            totalUsers += o.listUsers().size();
        }

        int totalTasks = 0;
        for (Organization o : orgs.values()) {
            totalTasks += o.taskCount();
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("platform",    platformName);
        stats.put("tenants",     organizationCount());
        stats.put("total_users", totalUsers);
        stats.put("total_tasks", totalTasks);
        return stats;
    }

    @Override
    public String toString() {
        return String.format("SaaSPlatform(name='%s', tenants=%d)", platformName, organizationCount());
    }
}
