package com.phishguard.engine;

import com.phishguard.model.ThreatCategory;
import com.phishguard.model.ThreatIndicator;
import com.phishguard.model.ThreatLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UrlAnalyzer {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)", Pattern.CASE_INSENSITIVE);

    private static final List<String> SUSPICIOUS_TLDS = Arrays.asList(
            ".xyz", ".top", ".click", ".loan", ".work", ".gq", ".cf", ".tk", ".ml", ".ga",
            ".buzz", ".club", ".icu", ".cam", ".rest", ".monster", ".hair", ".beauty"
    );

    private static final List<String> URL_SHORTENERS = Arrays.asList(
            "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "is.gd", "buff.ly",
            "adf.ly", "shorte.st", "linktr.ee", "rb.gy", "cutt.ly", "shorturl.at"
    );

    private static final List<String> COMMONLY_SPOOFED_DOMAINS = Arrays.asList(
            "paypal", "apple", "microsoft", "google", "amazon", "netflix", "facebook",
            "instagram", "whatsapp", "linkedin", "twitter", "dropbox", "icloud",
            "chase", "wellsfargo", "bankofamerica", "citibank", "usps", "fedex", "dhl"
    );

    public List<ThreatIndicator> analyze(String body, String subject) {
        List<ThreatIndicator> indicators = new ArrayList<>();
        String combined = (body != null ? body : "") + " " + (subject != null ? subject : "");

        List<String> urls = extractUrls(combined);

        if (urls.isEmpty()) {
            return indicators;
        }

        for (String url : urls) {
            String lowerUrl = url.toLowerCase();

            // Check for IP address in URL
            if (containsIpAddress(lowerUrl)) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.URL, ThreatLevel.HIGH,
                        "IP address detected in URL",
                        "URL contains an IP address instead of a domain name: " + truncateUrl(url),
                        20
                ));
            }

            // Check for suspicious TLDs
            for (String tld : SUSPICIOUS_TLDS) {
                if (lowerUrl.contains(tld + "/") || lowerUrl.endsWith(tld)) {
                    indicators.add(new ThreatIndicator(
                            ThreatCategory.URL, ThreatLevel.MEDIUM,
                            "Suspicious top-level domain detected",
                            "URL uses a suspicious TLD (" + tld + "): " + truncateUrl(url),
                            15
                    ));
                    break;
                }
            }

            // Check for URL shorteners
            for (String shortener : URL_SHORTENERS) {
                if (lowerUrl.contains(shortener)) {
                    indicators.add(new ThreatIndicator(
                            ThreatCategory.URL, ThreatLevel.MEDIUM,
                            "URL shortener detected",
                            "Shortened URLs hide the true destination: " + truncateUrl(url),
                            12
                    ));
                    break;
                }
            }

            // Check for @ symbol in URL (used to trick users)
            if (lowerUrl.contains("@")) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.URL, ThreatLevel.HIGH,
                        "Deceptive @ symbol in URL",
                        "URL contains @ which can be used to disguise the true destination: " + truncateUrl(url),
                        22
                ));
            }

            // Check for excessive subdomains (typosquatting pattern)
            if (countSubdomains(lowerUrl) > 3) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.URL, ThreatLevel.MEDIUM,
                        "Excessive subdomains detected",
                        "URL has many subdomains, which is a common phishing technique: " + truncateUrl(url),
                        10
                ));
            }

            // Check for HTTP (not HTTPS)
            if (lowerUrl.startsWith("http://")) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.URL, ThreatLevel.LOW,
                        "Insecure HTTP connection",
                        "URL uses HTTP instead of HTTPS: " + truncateUrl(url),
                        5
                ));
            }

            // Check for typosquatting of known brands
            for (String brand : COMMONLY_SPOOFED_DOMAINS) {
                if (lowerUrl.contains(brand) && !isLegitDomain(lowerUrl, brand)) {
                    indicators.add(new ThreatIndicator(
                            ThreatCategory.URL, ThreatLevel.HIGH,
                            "Possible brand impersonation in URL",
                            "URL contains '" + brand + "' but may not be the legitimate domain: " + truncateUrl(url),
                            25
                    ));
                    break;
                }
            }

            // Check for data URIs
            if (lowerUrl.startsWith("data:")) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.URL, ThreatLevel.CRITICAL,
                        "Data URI detected",
                        "Data URIs can be used to embed malicious content",
                        30
                ));
            }

            // Check for encoded characters (obfuscation)
            if (countEncodedChars(url) > 5) {
                indicators.add(new ThreatIndicator(
                        ThreatCategory.URL, ThreatLevel.MEDIUM,
                        "Heavily encoded URL detected",
                        "URL contains many encoded characters, possibly to hide malicious content: " + truncateUrl(url),
                        10
                ));
            }
        }

        return indicators;
    }

    private List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }

    private boolean containsIpAddress(String url) {
        return Pattern.matches(".*https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*", url);
    }

    private int countSubdomains(String url) {
        try {
            String domain = url.replaceAll("https?://", "").split("/")[0].split("\\?")[0];
            return domain.split("\\.").length - 2;
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean isLegitDomain(String url, String brand) {
        try {
            String domain = url.replaceAll("https?://", "").split("/")[0];
            return domain.equals(brand + ".com") || domain.endsWith("." + brand + ".com");
        } catch (Exception e) {
            return false;
        }
    }

    private int countEncodedChars(String url) {
        int count = 0;
        for (int i = 0; i < url.length() - 2; i++) {
            if (url.charAt(i) == '%') count++;
        }
        return count;
    }

    private String truncateUrl(String url) {
        return url.length() > 100 ? url.substring(0, 97) + "..." : url;
    }
}
