package com.phishguard.repository;

import com.phishguard.model.EmailScan;
import com.phishguard.model.ThreatLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailScanRepository extends JpaRepository<EmailScan, Long> {

    List<EmailScan> findTop10ByOrderByScanDateDesc();

    List<EmailScan> findAllByOrderByScanDateDesc();

    long countByThreatLevel(ThreatLevel level);

    long countByThreatLevelNot(ThreatLevel level);

    @Query("SELECT COUNT(e) FROM EmailScan e WHERE e.threatLevel IN ('HIGH', 'CRITICAL')")
    long countHighAndCriticalThreats();

    @Query("SELECT AVG(e.threatScore) FROM EmailScan e")
    Double getAverageThreatScore();

    List<EmailScan> findByScanDateAfter(LocalDateTime date);

    @Query("SELECT e.threatLevel, COUNT(e) FROM EmailScan e GROUP BY e.threatLevel")
    List<Object[]> countByThreatLevelGrouped();
}
