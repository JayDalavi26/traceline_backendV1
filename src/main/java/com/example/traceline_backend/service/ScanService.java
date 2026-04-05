package com.example.traceline_backend.service;

import com.example.traceline_backend.model.Block;
import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.model.ScanEvent;
import com.example.traceline_backend.repository.PartRepository;
import com.example.traceline_backend.repository.ScanEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanEventRepository scanEventRepository;
    private final BlockchainService blockchainService;
    private final AnomalyDetectionService anomalyService;
    private final PartRepository partRepository;

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

        Optional<Part> optionalPart = partRepository.findByPartId(partId);
        if (optionalPart.isPresent()) {
            Part part = optionalPart.get();
            part.setStage(stage);
            partRepository.save(part);
        }
        ScanEvent saved = scanEventRepository.save(scan);

        // Run AI anomaly check
        anomalyService.checkForAnomalies(saved);

        return saved;
    }

    public List<ScanEvent> getRecentScans() {
        return scanEventRepository.findTop20ByOrderByTimestampDesc();
    }
}