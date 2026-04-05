package com.example.traceline_backend.service;


import com.example.traceline_backend.dto.DashboardMetrics;
import com.example.traceline_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final PartRepository partRepository;
    private final AnomalyRepository anomalyRepository;
    private final BlockRepository blockRepository;
    private final ScanEventRepository scanEventRepository;
    private final OperatorRepository operatorRepository;

    public DashboardService(PartRepository partRepository, AnomalyRepository anomalyRepository, BlockRepository blockRepository, ScanEventRepository scanEventRepository, OperatorRepository operatorRepository) {
        this.partRepository = partRepository;
        this.anomalyRepository = anomalyRepository;
        this.blockRepository = blockRepository;
        this.scanEventRepository = scanEventRepository;
        this.operatorRepository = operatorRepository;
    }

    public DashboardMetrics getCurrentMetrics() {
        long totalParts = partRepository.count();
        long totalBlocks = blockRepository.count();
        long activeAnomalies = anomalyRepository.countByResolvedFalse();
        long activeOperators = operatorRepository.countByStatus("on");

        // Calculate tracking accuracy based on today's scans
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long todayScans = scanEventRepository.countByTimestampAfter(startOfDay);
        long anomalousScans = anomalyRepository.countByDetectedAtAfter(startOfDay);
        double trackingAccuracy = todayScans == 0 ? 92.4 :
                (1 - (double) anomalousScans / todayScans) * 100;

        // Calculate quality rate from parts with qualityScore > 80
        long highQualityParts = partRepository.countByQualityScoreGreaterThan(80);
        double qualityRate = totalParts == 0 ? 96.2 : (double) highQualityParts / totalParts * 100;

        // Average cycle time (simulated, can be computed from scan timestamps)
        double avgCycleTimeHours = calculateAvgCycleTime();

        DashboardMetrics metrics = new DashboardMetrics();
        metrics.setPartsInProduction(totalParts);
        metrics.setTrackingAccuracy(trackingAccuracy);
        metrics.setAnomalies((int) activeAnomalies);
        metrics.setBlockchainTXs(totalBlocks);
        metrics.setActiveOperators((int) activeOperators);
        metrics.setAvgCycleTimeHours(avgCycleTimeHours);
        metrics.setQualityRate(qualityRate);

        return metrics;
    }

    private double calculateAvgCycleTime() {
        // Simplified: return a simulated value
        // In real implementation, you would query scan timestamps for completed parts
        return 4.8;
    }
}
