package com.phishguard.service;

import com.phishguard.dto.EmailAnalysisRequest;
import com.phishguard.dto.EmailAnalysisResponse;
import com.phishguard.engine.ContentAnalyzer;
import com.phishguard.engine.HeaderAnalyzer;
import com.phishguard.engine.UrlAnalyzer;
import com.phishguard.model.EmailScan;
import com.phishguard.model.ThreatIndicator;
import com.phishguard.model.ThreatLevel;
import com.phishguard.repository.EmailScanRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhishingDetectionService {

    private final UrlAnalyzer urlAnalyzer;
    private final ContentAnalyzer contentAnalyzer;
    private final HeaderAnalyzer headerAnalyzer;
    private final EmailScanRepository repository;

    public PhishingDetectionService(UrlAnalyzer urlAnalyzer, ContentAnalyzer contentAnalyzer,
                                    HeaderAnalyzer headerAnalyzer, EmailScanRepository repository) {
        this.urlAnalyzer = urlAnalyzer;
        this.contentAnalyzer = contentAnalyzer;
        this.headerAnalyzer = headerAnalyzer;
        this.repository = repository;
    }

    public EmailAnalysisResponse analyzeEmail(EmailAnalysisRequest request) {
        // Run all analyzers
        List<ThreatIndicator> allIndicators = new ArrayList<>();
        allIndicators.addAll(urlAnalyzer.analyze(request.getBody(), request.getSubject()));
        allIndicators.addAll(contentAnalyzer.analyze(request.getBody(), request.getSubject()));
        allIndicators.addAll(headerAnalyzer.analyze(request.getSenderEmail(), request.getSenderName(), request.getRawHeaders()));

        // Calculate threat score
        int totalScore = allIndicators.stream().mapToInt(ThreatIndicator::getScoreImpact).sum();
        totalScore = Math.min(totalScore, 100); // Cap at 100

        // Determine threat level
        ThreatLevel level = calculateThreatLevel(totalScore);

        // Generate summary
        String summary = generateSummary(totalScore, level, allIndicators);

        // Create and save the scan entity
        EmailScan scan = new EmailScan();
        scan.setSenderEmail(request.getSenderEmail());
        scan.setSenderName(request.getSenderName());
        scan.setSubject(request.getSubject());
        scan.setBody(request.getBody());
        scan.setRawHeaders(request.getRawHeaders());
        scan.setThreatScore(totalScore);
        scan.setThreatLevel(level);
        scan.setSummary(summary);

        for (ThreatIndicator indicator : allIndicators) {
            scan.addIndicator(indicator);
        }

        EmailScan savedScan = repository.save(scan);

        // Build response
        return buildResponse(savedScan);
    }

    public EmailAnalysisResponse getScanById(Long id) {
        EmailScan scan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scan not found with id: " + id));
        return buildResponse(scan);
    }

    public List<EmailAnalysisResponse> getAllScans() {
        return repository.findAllByOrderByScanDateDesc().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    public void deleteScan(Long id) {
        repository.deleteById(id);
    }

    private ThreatLevel calculateThreatLevel(int score) {
        if (score >= 80) return ThreatLevel.CRITICAL;
        if (score >= 60) return ThreatLevel.HIGH;
        if (score >= 35) return ThreatLevel.MEDIUM;
        if (score >= 15) return ThreatLevel.LOW;
        return ThreatLevel.SAFE;
    }

    private String generateSummary(int score, ThreatLevel level, List<ThreatIndicator> indicators) {
        if (indicators.isEmpty()) {
            return "No phishing indicators detected. This email appears to be safe.";
        }

        long criticalCount = indicators.stream().filter(i -> i.getSeverity() == ThreatLevel.CRITICAL).count();
        long highCount = indicators.stream().filter(i -> i.getSeverity() == ThreatLevel.HIGH).count();

        StringBuilder sb = new StringBuilder();

        switch (level) {
            case CRITICAL:
                sb.append("⚠️ CRITICAL THREAT DETECTED! This email has multiple strong phishing indicators. ");
                break;
            case HIGH:
                sb.append("🔴 HIGH RISK — This email shows significant signs of phishing. ");
                break;
            case MEDIUM:
                sb.append("🟡 MODERATE RISK — This email has some suspicious characteristics. ");
                break;
            case LOW:
                sb.append("🟢 LOW RISK — Minor concerns detected but email is likely legitimate. ");
                break;
            default:
                sb.append("✅ SAFE — No significant threats detected. ");
        }

        sb.append("Found ").append(indicators.size()).append(" indicator(s) ");
        if (criticalCount > 0) sb.append("including ").append(criticalCount).append(" critical ");
        if (highCount > 0) sb.append("and ").append(highCount).append(" high severity ");
        sb.append("with a threat score of ").append(score).append("/100.");

        return sb.toString();
    }

    private EmailAnalysisResponse buildResponse(EmailScan scan) {
        EmailAnalysisResponse response = new EmailAnalysisResponse();
        response.setId(scan.getId());
        response.setSenderEmail(scan.getSenderEmail());
        response.setSubject(scan.getSubject());
        response.setThreatScore(scan.getThreatScore());
        response.setThreatLevel(scan.getThreatLevel());
        response.setSummary(scan.getSummary());
        response.setScanDate(scan.getScanDate());

        List<EmailAnalysisResponse.IndicatorDto> indicatorDtos = scan.getIndicators().stream()
                .map(i -> new EmailAnalysisResponse.IndicatorDto(
                        i.getCategory(), i.getSeverity(), i.getDescription(),
                        i.getDetails(), i.getScoreImpact()))
                .collect(Collectors.toList());
        response.setIndicators(indicatorDtos);

        return response;
    }
}
