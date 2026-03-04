package com.phishguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "email_scans")
public class EmailScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body", length = 10000)
    private String body;

    @Column(name = "raw_headers", length = 5000)
    private String rawHeaders;

    @Column(name = "threat_score")
    private int threatScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "threat_level")
    private ThreatLevel threatLevel;

    @Column(name = "summary", length = 2000)
    private String summary;

    @Column(name = "scan_date")
    private LocalDateTime scanDate;

    @OneToMany(mappedBy = "emailScan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ThreatIndicator> indicators = new ArrayList<>();

    public EmailScan() {
    }

    @PrePersist
    protected void onCreate() {
        scanDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRawHeaders() {
        return rawHeaders;
    }

    public void setRawHeaders(String rawHeaders) {
        this.rawHeaders = rawHeaders;
    }

    public int getThreatScore() {
        return threatScore;
    }

    public void setThreatScore(int threatScore) {
        this.threatScore = threatScore;
    }

    public ThreatLevel getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(ThreatLevel threatLevel) {
        this.threatLevel = threatLevel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getScanDate() {
        return scanDate;
    }

    public void setScanDate(LocalDateTime scanDate) {
        this.scanDate = scanDate;
    }

    public List<ThreatIndicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<ThreatIndicator> indicators) {
        this.indicators = indicators;
    }

    public void addIndicator(ThreatIndicator indicator) {
        indicators.add(indicator);
        indicator.setEmailScan(this);
    }
}
