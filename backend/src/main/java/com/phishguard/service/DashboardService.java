package com.phishguard.service;

import com.phishguard.dto.DashboardStats;
import com.phishguard.model.EmailScan;
import com.phishguard.model.ThreatLevel;
import com.phishguard.repository.EmailScanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EmailScanRepository repository;

    public DashboardService(EmailScanRepository repository) {
        this.repository = repository;
    }

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();

        long totalScans = repository.count();
        stats.setTotalScans(totalScans);

        long safeCount = repository.countByThreatLevel(ThreatLevel.SAFE);
        stats.setSafeEmails(safeCount);
        stats.setThreatsDetected(totalScans - safeCount);

        stats.setCriticalThreats(repository.countByThreatLevel(ThreatLevel.CRITICAL));

        Double avgScore = repository.getAverageThreatScore();
        stats.setAverageThreatScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);

        // Threats by level
        Map<String, Long> threatsByLevel = new LinkedHashMap<>();
        for (ThreatLevel level : ThreatLevel.values()) {
            threatsByLevel.put(level.name(), repository.countByThreatLevel(level));
        }
        stats.setThreatsByLevel(threatsByLevel);

        // Threats by category
        Map<String, Long> threatsByCategory = new LinkedHashMap<>();
        List<EmailScan> allScans = repository.findAll();
        allScans.stream()
                .flatMap(scan -> scan.getIndicators().stream())
                .forEach(indicator -> {
                    String cat = indicator.getCategory().name();
                    threatsByCategory.merge(cat, 1L, Long::sum);
                });
        stats.setThreatsByCategory(threatsByCategory);

        // Recent scans
        List<DashboardStats.ScanSummary> recentScans = repository.findTop10ByOrderByScanDateDesc().stream()
                .map(scan -> new DashboardStats.ScanSummary(
                        scan.getId(),
                        scan.getSenderEmail(),
                        scan.getSubject(),
                        scan.getThreatScore(),
                        scan.getThreatLevel().name(),
                        scan.getScanDate() != null ? scan.getScanDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : ""
                ))
                .collect(Collectors.toList());
        stats.setRecentScans(recentScans);

        // Daily stats for last 7 days
        List<DashboardStats.DailyStats> dailyStats = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            List<EmailScan> dayScans = repository.findByScanDateAfter(dayStart).stream()
                    .filter(s -> s.getScanDate() != null && s.getScanDate().isBefore(dayEnd))
                    .collect(Collectors.toList());

            long dayThreats = dayScans.stream()
                    .filter(s -> s.getThreatLevel() != ThreatLevel.SAFE)
                    .count();

            dailyStats.add(new DashboardStats.DailyStats(
                    date.format(DateTimeFormatter.ofPattern("MMM dd")),
                    dayScans.size(),
                    dayThreats
            ));
        }
        stats.setDailyStats(dailyStats);

        return stats;
    }
}
