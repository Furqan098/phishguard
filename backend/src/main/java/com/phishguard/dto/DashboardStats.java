package com.phishguard.dto;

import java.util.List;
import java.util.Map;

public class DashboardStats {

    private long totalScans;
    private long threatsDetected;
    private long safeEmails;
    private long criticalThreats;
    private double averageThreatScore;
    private Map<String, Long> threatsByLevel;
    private Map<String, Long> threatsByCategory;
    private List<ScanSummary> recentScans;
    private List<DailyStats> dailyStats;

    public static class ScanSummary {
        private Long id;
        private String senderEmail;
        private String subject;
        private int threatScore;
        private String threatLevel;
        private String scanDate;

        public ScanSummary() {}

        public ScanSummary(Long id, String senderEmail, String subject, int threatScore, String threatLevel, String scanDate) {
            this.id = id;
            this.senderEmail = senderEmail;
            this.subject = subject;
            this.threatScore = threatScore;
            this.threatLevel = threatLevel;
            this.scanDate = scanDate;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSenderEmail() { return senderEmail; }
        public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public int getThreatScore() { return threatScore; }
        public void setThreatScore(int threatScore) { this.threatScore = threatScore; }
        public String getThreatLevel() { return threatLevel; }
        public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }
        public String getScanDate() { return scanDate; }
        public void setScanDate(String scanDate) { this.scanDate = scanDate; }
    }

    public static class DailyStats {
        private String date;
        private long scans;
        private long threats;

        public DailyStats() {}

        public DailyStats(String date, long scans, long threats) {
            this.date = date;
            this.scans = scans;
            this.threats = threats;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public long getScans() { return scans; }
        public void setScans(long scans) { this.scans = scans; }
        public long getThreats() { return threats; }
        public void setThreats(long threats) { this.threats = threats; }
    }

    public DashboardStats() {}

    // Getters and Setters
    public long getTotalScans() { return totalScans; }
    public void setTotalScans(long totalScans) { this.totalScans = totalScans; }

    public long getThreatsDetected() { return threatsDetected; }
    public void setThreatsDetected(long threatsDetected) { this.threatsDetected = threatsDetected; }

    public long getSafeEmails() { return safeEmails; }
    public void setSafeEmails(long safeEmails) { this.safeEmails = safeEmails; }

    public long getCriticalThreats() { return criticalThreats; }
    public void setCriticalThreats(long criticalThreats) { this.criticalThreats = criticalThreats; }

    public double getAverageThreatScore() { return averageThreatScore; }
    public void setAverageThreatScore(double averageThreatScore) { this.averageThreatScore = averageThreatScore; }

    public Map<String, Long> getThreatsByLevel() { return threatsByLevel; }
    public void setThreatsByLevel(Map<String, Long> threatsByLevel) { this.threatsByLevel = threatsByLevel; }

    public Map<String, Long> getThreatsByCategory() { return threatsByCategory; }
    public void setThreatsByCategory(Map<String, Long> threatsByCategory) { this.threatsByCategory = threatsByCategory; }

    public List<ScanSummary> getRecentScans() { return recentScans; }
    public void setRecentScans(List<ScanSummary> recentScans) { this.recentScans = recentScans; }

    public List<DailyStats> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyStats> dailyStats) { this.dailyStats = dailyStats; }
}
