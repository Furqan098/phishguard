package com.phishguard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "threat_indicators")
public class ThreatIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ThreatCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private ThreatLevel severity;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "score_impact")
    private int scoreImpact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_scan_id")
    @JsonIgnore
    private EmailScan emailScan;

    public ThreatIndicator() {}

    public ThreatIndicator(ThreatCategory category, ThreatLevel severity, String description, String details, int scoreImpact) {
        this.category = category;
        this.severity = severity;
        this.description = description;
        this.details = details;
        this.scoreImpact = scoreImpact;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public EmailScan getEmailScan() { return emailScan; }
    public void setEmailScan(EmailScan emailScan) { this.emailScan = emailScan; }
}
