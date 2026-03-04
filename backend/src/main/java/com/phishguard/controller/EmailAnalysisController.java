package com.phishguard.controller;

import com.phishguard.dto.EmailAnalysisRequest;
import com.phishguard.dto.EmailAnalysisResponse;
import com.phishguard.service.PhishingDetectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
public class EmailAnalysisController {

    private final PhishingDetectionService detectionService;

    public EmailAnalysisController(PhishingDetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<EmailAnalysisResponse> analyzeEmail(@Valid @RequestBody EmailAnalysisRequest request) {
        EmailAnalysisResponse response = detectionService.analyzeEmail(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EmailAnalysisResponse>> getAllScans() {
        return ResponseEntity.ok(detectionService.getAllScans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailAnalysisResponse> getScanById(@PathVariable Long id) {
        return ResponseEntity.ok(detectionService.getScanById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScan(@PathVariable Long id) {
        detectionService.deleteScan(id);
        return ResponseEntity.noContent().build();
    }
}
