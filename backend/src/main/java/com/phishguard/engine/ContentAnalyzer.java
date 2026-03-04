package com.phishguard.engine;

import com.phishguard.model.ThreatCategory;
import com.phishguard.model.ThreatIndicator;
import com.phishguard.model.ThreatLevel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class ContentAnalyzer {

    private static final Map<String, List<String>> THREAT_PATTERNS = new LinkedHashMap<>();

    static {
        THREAT_PATTERNS.put("URGENCY", Arrays.asList(
                "act now", "immediate action", "urgent", "within 24 hours", "within 48 hours",
                "expires today", "last chance", "don't delay", "time sensitive", "limited time",
                "respond immediately", "action required", "must respond", "final warning",
                "your account will be", "failure to", "deadline", "asap", "time is running out"
        ));

        THREAT_PATTERNS.put("CREDENTIAL_REQUEST", Arrays.asList(
                "verify your account", "confirm your identity", "update your password",
                "verify your identity", "confirm your account", "login credentials",
                "enter your password", "update your information", "verify your email",
                "confirm your details", "reset your password", "sign in to verify",
                "validate your account", "authenticate your", "re-enter your"
        ));

        THREAT_PATTERNS.put("FINANCIAL", Arrays.asList(
                "bank account", "credit card", "wire transfer", "social security",
                "routing number", "account number", "payment details", "billing information",
                "tax refund", "prize money", "lottery", "inheritance", "investment opportunity",
                "bitcoin", "cryptocurrency", "western union", "money gram", "gift card"
        ));

        THREAT_PATTERNS.put("THREAT_LANGUAGE", Arrays.asList(
                "account will be closed", "account suspended", "unauthorized access",
                "suspicious activity", "security breach", "compromised", "illegal activity",
                "law enforcement", "legal action", "arrest warrant", "irs notice",
                "account locked", "permanently disabled", "terminated"
        ));

        THREAT_PATTERNS.put("IMPERSONATION", Arrays.asList(
                "dear customer", "dear user", "dear account holder", "valued customer",
                "dear sir/madam", "dear member", "dear client", "dear valued"
        ));

        THREAT_PATTERNS.put("CLICK_BAIT", Arrays.asList(
                "click here", "click below", "click the link", "click this link",
                "open attachment", "download now", "see attached", "view document",
                "open the document", "review attachment"
        ));
    }

    private static final List<String> SENSITIVE_DATA_PATTERNS = Arrays.asList(
            "ssn", "social security", "date of birth", "mother's maiden",
            "driver's license", "passport number", "pin number", "cvv",
            "security code", "tax id", "employee id"
    );

    public List<ThreatIndicator> analyze(String body, String subject) {
        List<ThreatIndicator> indicators = new ArrayList<>();
        String combined = ((body != null ? body : "") + " " + (subject != null ? subject : "")).toLowerCase();

        // Check for threat patterns by category
        for (Map.Entry<String, List<String>> entry : THREAT_PATTERNS.entrySet()) {
            String category = entry.getKey();
            List<String> phrases = entry.getValue();
            List<String> foundPhrases = new ArrayList<>();

            for (String phrase : phrases) {
                if (combined.contains(phrase.toLowerCase())) {
                    foundPhrases.add(phrase);
                }
            }

            if (!foundPhrases.isEmpty()) {
                ThreatLevel severity;
                int impact;
                String description;

                switch (category) {
                    case "URGENCY":
                        severity = foundPhrases.size() >= 3 ? ThreatLevel.HIGH : ThreatLevel.MEDIUM;
                        impact = Math.min(foundPhrases.size() * 5, 20);
                        description = "Urgency manipulation detected";
                        break;
                    case "CREDENTIAL_REQUEST":
                        severity = ThreatLevel.HIGH;
                        impact = Math.min(foundPhrases.size() * 8, 25);
                        description = "Credential/password request detected";
                        break;
                    case "FINANCIAL":
                        severity = ThreatLevel.HIGH;
                        impact = Math.min(foundPhrases.size() * 7, 22);
                        description = "Financial information request detected";
                        break;
                    case "THREAT_LANGUAGE":
                        severity = ThreatLevel.HIGH;
                        impact = Math.min(foundPhrases.size() * 6, 20);
                        description = "Threatening/intimidation language detected";
                        break;
                    case "IMPERSONATION":
                        severity = ThreatLevel.LOW;
                        impact = Math.min(foundPhrases.size() * 3, 10);
                        description = "Generic greeting (possible impersonation)";
                        break;
                    case "CLICK_BAIT":
                        severity = ThreatLevel.MEDIUM;
                        impact = Math.min(foundPhrases.size() * 4, 15);
                        description = "Suspicious call-to-action detected";
                        break;
                    default:
                        severity = ThreatLevel.LOW;
                        impact = 5;
                        description = "Suspicious content detected";
                }

                indicators.add(new ThreatIndicator(
                        ThreatCategory.CONTENT, severity, description,
                        "Found " + foundPhrases.size() + " indicator(s): " + String.join(", ", foundPhrases),
                        impact
                ));
            }
        }

        // Check for sensitive data requests
        List<String> sensitiveFound = new ArrayList<>();
        for (String pattern : SENSITIVE_DATA_PATTERNS) {
            if (combined.contains(pattern.toLowerCase())) {
                sensitiveFound.add(pattern);
            }
        }
        if (!sensitiveFound.isEmpty()) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.CONTENT, ThreatLevel.CRITICAL,
                    "Sensitive personal data request detected",
                    "Email requests: " + String.join(", ", sensitiveFound),
                    Math.min(sensitiveFound.size() * 10, 30)
            ));
        }

        // Check for excessive capitalization (SHOUTING)
        if (body != null) {
            long upperCount = body.chars().filter(Character::isUpperCase).count();
            long letterCount = body.chars().filter(Character::isLetter).count();
            if (letterCount > 20 && (double) upperCount / letterCount > 0.5) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.CONTENT, ThreatLevel.LOW,
                        "Excessive capitalization detected",
                        "Over 50% of text is uppercase, which is a common spam/phishing technique",
                        8
                ));
            }
        }

        // Check for suspicious patterns in HTML
        if (body != null && body.toLowerCase().contains("<form")) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.CONTENT, ThreatLevel.HIGH,
                    "Embedded form detected in email",
                    "Email contains an HTML form, which may be used to steal credentials",
                    20
            ));
        }

        if (body != null && Pattern.compile("style\\s*=\\s*[\"'].*display\\s*:\\s*none", Pattern.CASE_INSENSITIVE).matcher(body).find()) {
            indicators.add(new ThreatIndicator(
                    ThreatCategory.CONTENT, ThreatLevel.MEDIUM,
                    "Hidden content detected",
                    "Email contains hidden elements (display:none), possibly hiding malicious content",
                    12
            ));
        }

        return indicators;
    }
}
