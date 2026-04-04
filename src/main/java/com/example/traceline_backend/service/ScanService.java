package com.example.traceline_backend.service;

import com.example.traceline_backend.model.Block;
import com.example.traceline_backend.model.ScanEvent;
import com.example.traceline_backend.repository.ScanEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanEventRepository scanEventRepository;
    private final BlockchainService blockchainService;
    private final AnomalyDetectionService anomalyService;

    public ScanEvent recordScan(String partId, String stage, String operatorId, String operatorName) {
        ScanEvent scan = new ScanEvent();
        scan.setPartId(partId);
        scan.setStage(stage);
        scan.setOperatorId(operatorId);
        scan.setOperatorName(operatorName);
        scan.setTimestamp(LocalDateTime.now());
        scan.setStatus("success");

        // Create blockchain block
        Block block = blockchainService.createBlock(
                String.format("{\"partId\":\"%s\",\"stage\":\"%s\",\"operator\":\"%s\"}", partId, stage, operatorId),
                partId, "scan");
        scan.setBlockHash(block.getHash());
        scan.setBlockNumber(block.getBlockNumber());

        ScanEvent saved = scanEventRepository.save(scan);

        // Run AI anomaly check
        anomalyService.checkForAnomalies(saved);

        return saved;
    }

    public List<ScanEvent> getRecentScans() {
        return scanEventRepository.findTop20ByOrderByTimestampDesc();
    }
}