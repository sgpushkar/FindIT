package com.findit.dto;

public class StatsResponse {
    private int activeLost;
    private int activeFound;
    private int resolvedCount;
    private int totalUsers;
    private int totalReports;

    public StatsResponse() {}

    public StatsResponse(int activeLost, int activeFound, int resolvedCount, int totalUsers, int totalReports) {
        this.activeLost = activeLost;
        this.activeFound = activeFound;
        this.resolvedCount = resolvedCount;
        this.totalUsers = totalUsers;
        this.totalReports = totalReports;
    }

    public int getActiveLost() { return activeLost; }
    public void setActiveLost(int activeLost) { this.activeLost = activeLost; }
    public int getActiveFound() { return activeFound; }
    public void setActiveFound(int activeFound) { this.activeFound = activeFound; }
    public int getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(int resolvedCount) { this.resolvedCount = resolvedCount; }
    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
    public int getTotalReports() { return totalReports; }
    public void setTotalReports(int totalReports) { this.totalReports = totalReports; }
}

