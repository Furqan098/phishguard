package com.phishguard.dto;

import com.phishguard.model.ThreatCategory;
import com.phishguard.model.ThreatLevel;

import java.time.LocalDateTime;
import java.util.List;

public class EmailAnalysisResponse {

    private Long id;
    private String senderEmail;
    private String subject;
    private int threatScore;
    private ThreatLevel threatLevel;
    private String summary;
    private LocalDateTime scanDate;
    private List<IndicatorDto> indicators;

    public static class IndicatorDto {
        private ThreatCategory category;
        private ThreatLevel severity;
        private String description;
        private String details;
        private int scoreImpact;

        public IndicatorDto() {}

        public IndicatorDto(ThreatCategory category, ThreatLevel severity, String description, String details, int scoreImpact) {
            this.category = category;
            this.severity = severity;
            this.description = description;
            this.details = details;
            this.scoreImpact = scoreImpact;
        }

        public ThreatCategory getCategory() { return category; }
        public void setCategory(ThreatCategory category) { this.category = category; }
        public ThreatLevel getSeverity() { return severity; }
        public void setSeverity(ThreatLevel severity) { this.severity = severity; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        public int getScoreImpact() { return scoreImpact; }
        public void setScoreImpact(int scoreImpact) { this.scoreImpact = scoreImpact; }
    }

    public EmailAnalysisResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getThreatScore() { return threatScore; }
    public void setThreatScore(int threatScore) { this.threatScore = threatScore; }

    public ThreatLevel getThreatLevel() { return threatLevel; }
    public void setThreatLevel(ThreatLevel threatLevel) { this.threatLevel = threatLevel; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public LocalDateTime getScanDate() { return scanDate; }
    public void setScanDate(LocalDateTime scanDate) { this.scanDate = scanDate; }

    public List<IndicatorDto> getIndicators() { return indicators; }
    public void setIndicators(List<IndicatorDto> indicators) { this.indicators = indicators; }
}
