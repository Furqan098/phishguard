package com.phishguard.dto;

import jakarta.validation.constraints.NotBlank;

public class EmailAnalysisRequest {

    private String senderEmail;
    private String senderName;
    private String subject;

    @NotBlank(message = "Email body is required")
    private String body;

    private String rawHeaders;

    public EmailAnalysisRequest() {}

    // Getters and Setters
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getRawHeaders() { return rawHeaders; }
    public void setRawHeaders(String rawHeaders) { this.rawHeaders = rawHeaders; }
}
