package com.phishguard.engine;

import com.phishguard.model.ThreatCategory;
import com.phishguard.model.ThreatIndicator;
import com.phishguard.model.ThreatLevel;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HeaderAnalyzer {

    private static final List<String> FREE_EMAIL_PROVIDERS = Arrays.asList(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "aol.com",
            "mail.com", "protonmail.com", "zoho.com", "yandex.com", "gmx.com",
            "icloud.com", "live.com", "msn.com"
    );

    private static final List<String> BUSINESS_KEYWORDS = Arrays.asList(
            "bank", "paypal", "amazon", "microsoft", "apple", "google", "facebook",
            "netflix", "support", "security", "admin", "billing", "service",
            "helpdesk", "noreply", "notification"
    );

    public List<ThreatIndicator> analyze(String senderEmail, String senderName, String rawHeaders) {
        List<ThreatIndicator> indicators = new ArrayList<>();

        if (senderEmail != null && !senderEmail.isBlank()) {
            analyzeSenderEmail(senderEmail, senderName, indicators);
        }

        if (rawHeaders != null && !rawHeaders.isBlank()) {
            analyzeRawHeaders(rawHeaders, senderEmail, indicators);
        }

        return indicators;
    }

    private void analyzeSenderEmail(String senderEmail, String senderName, List<ThreatIndicator> indicators) {
        String lowerEmail = senderEmail.toLowerCase();
        String domain = lowerEmail.contains("@") ? lowerEmail.split("@")[1] : "";

        // Check if sender claims to be a business but uses free email
        if (senderName != null) {
            String lowerName = senderName.toLowerCase();
            for (String keyword : BUSINESS_KEYWORDS) {
                if (lowerName.contains(keyword) && isFreeEmail(domain)) {
                    indicators.add(new ThreatIndicator(
                            ThreatCategory.SENDER, ThreatLevel.HIGH,
                            "Business impersonation using free email",
                            "Sender name suggests '" + keyword + "' but uses free email provider (" + domain + ")",
                            22
                    ));
                    break;
                }
            }
        }

        // Check for display name spoofing (name looks like an email)
        if (senderName != null && senderName.contains("@") && !senderName.equals(senderEmail)) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.SENDER, ThreatLevel.HIGH,
                    "Display name spoofing detected",
                    "Sender display name contains an email address different from actual sender: " + senderName,
                    20
            ));
        }

        // Check for suspicious domain patterns
        if (domain.matches(".*\\d{3,}.*")) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.SENDER, ThreatLevel.MEDIUM,
                    "Suspicious sender domain",
                    "Domain contains unusual number sequences: " + domain,
                    12
            ));
        }

        // Check for very long domain names
        if (domain.length() > 30) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.SENDER, ThreatLevel.MEDIUM,
                    "Unusually long sender domain",
                    "Domain name is suspiciously long: " + domain,
                    10
            ));
        }

        // Check for domain with many hyphens (common in phishing)
        long hyphenCount = domain.chars().filter(ch -> ch == '-').count();
        if (hyphenCount >= 3) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.SENDER, ThreatLevel.MEDIUM,
                    "Suspicious hyphenated domain",
                    "Domain contains many hyphens, a common phishing pattern: " + domain,
                    12
            ));
        }

        // Check for recently common phishing TLDs in sender
        List<String> suspiciousTlds = Arrays.asList(".xyz", ".top", ".click", ".tk", ".ml", ".ga", ".cf", ".gq");
        for (String tld : suspiciousTlds) {
            if (domain.endsWith(tld)) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.SENDER, ThreatLevel.MEDIUM,
                        "Sender uses suspicious TLD",
                        "Sender domain uses a TLD commonly associated with spam: " + tld,
                        15
                ));
                break;
            }
        }
    }

    private void analyzeRawHeaders(String rawHeaders, String senderEmail, List<ThreatIndicator> indicators) {
        String lowerHeaders = rawHeaders.toLowerCase();

        // Check for SPF fail
        if (lowerHeaders.contains("spf=fail") || lowerHeaders.contains("spf=softfail")) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.HEADER, ThreatLevel.HIGH,
                    "SPF authentication failed",
                    "The email failed SPF verification, meaning it may not have been sent from the claimed domain",
                    22
            ));
        }

        // Check for DKIM fail
        if (lowerHeaders.contains("dkim=fail")) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.HEADER, ThreatLevel.HIGH,
                    "DKIM authentication failed",
                    "The email's DKIM signature is invalid, suggesting possible tampering or spoofing",
                    22
            ));
        }

        // Check for DMARC fail
        if (lowerHeaders.contains("dmarc=fail")) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.HEADER, ThreatLevel.CRITICAL,
                    "DMARC authentication failed",
                    "The email failed DMARC verification, a strong indicator of spoofing",
                    28
            ));
        }

        // Check for mismatched Reply-To
        if (senderEmail != null) {
            String senderDomain = senderEmail.contains("@") ? senderEmail.split("@")[1].toLowerCase() : "";
            if (lowerHeaders.contains("reply-to:")) {
                String replyTo = extractHeaderValue(lowerHeaders, "reply-to:");
                if (!replyTo.isEmpty() && !replyTo.contains(senderDomain) && !senderDomain.isEmpty()) {
                    indicators.add(new ThreatIndicator(
                            ThreatCategory.HEADER, ThreatLevel.HIGH,
                            "Mismatched Reply-To address",
                            "Reply-To domain differs from sender domain, suggesting email redirection",
                            18
                    ));
                }
            }
        }

        // Check for multiple Received headers (excessive routing)
        int receivedCount = countOccurrences(lowerHeaders, "received:");
        if (receivedCount > 8) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.HEADER, ThreatLevel.LOW,
                    "Unusual email routing detected",
                    "Email passed through " + receivedCount + " servers, which is more than typical",
                    5
            ));
        }

        // Check for X-Mailer or User-Agent suggesting bulk mailer
        List<String> bulkMailers = Arrays.asList("phpmailer", "swiftmailer", "mailchimp", "sendinblue", "mass mail");
        for (String mailer : bulkMailers) {
            if (lowerHeaders.contains(mailer)) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.HEADER, ThreatLevel.LOW,
                        "Bulk mailing tool detected",
                        "Email was sent using a bulk mailing tool: " + mailer,
                        5
                ));
                break;
            }
        }
    }

    private boolean isFreeEmail(String domain) {
        return FREE_EMAIL_PROVIDERS.contains(domain.toLowerCase());
    }

    private String extractHeaderValue(String headers, String headerName) {
        int idx = headers.indexOf(headerName);
        if (idx == -1) return "";
        int endIdx = headers.indexOf("\n", idx);
        if (endIdx == -1) endIdx = headers.length();
        return headers.substring(idx + headerName.length(), endIdx).trim();
    }

    private int countOccurrences(String text, String word) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(word, idx)) != -1) {
            count++;
            idx += word.length();
        }
        return count;
    }
}
