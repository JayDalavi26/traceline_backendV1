package com.example.traceline_backend.service;

import com.example.traceline_backend.model.Block;
import com.example.traceline_backend.model.ScanEvent;
import com.example.traceline_backend.repository.ScanEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScanService {

    private final ScanEventRepository scanEventRepository;
    private final BlockchainService blockchainService;           // Simulated blockchain (MongoDB)
    private final BlockchainContractService blockchainContractService; // Real Ethereum blockchain
    private final AnomalyDetectionService anomalyService;
    private final PartService partService;

    @Value("${blockchain.enabled:true}")
    private boolean blockchainEnabled;

    public ScanEvent recordScan(String partId, String stage, String operatorId, String operatorName) {

        ScanEvent scan = new ScanEvent();
        scan.setPartId(partId);
        scan.setStage(stage);
        scan.setOperatorId(operatorId);
        scan.setOperatorName(operatorName);
        scan.setTimestamp(LocalDateTime.now());
        scan.setStatus("success");

        String ethereumTxHash = null;
        Block simulatedBlock = null;

        // ===== STEP 1: Record on REAL Ethereum blockchain (if enabled) =====
        if (blockchainEnabled) {
            try {
                log.info("⛓️ Recording scan on Ethereum blockchain...");
                ethereumTxHash = blockchainContractService.recordScan(partId, stage, operatorId, operatorName);
                scan.setTransactionHash(ethereumTxHash);
                log.info("✅ Ethereum transaction hash: {}", ethereumTxHash);
            } catch (Exception e) {
                log.error("❌ Ethereum blockchain recording failed: {}", e.getMessage());
                scan.setStatus("blockchain_failed");
                scan.setMessage("Ethereum tx failed: " + e.getMessage());
            }
        }

        // ===== STEP 2: Create simulated blockchain block (MongoDB) for fast lookup =====
        try {
            log.info("📦 Creating simulated block in MongoDB...");
            simulatedBlock = blockchainService.createBlock(
                    String.format("{\"partId\":\"%s\",\"stage\":\"%s\",\"operator\":\"%s\",\"ethereumTxHash\":\"%s\"}",
                            partId, stage, operatorId, ethereumTxHash),
                    partId, "scan"
            );
            scan.setBlockHash(simulatedBlock.getHash());
            scan.setBlockNumber(simulatedBlock.getBlockNumber());
            log.info("✅ Simulated block #{} created with hash: {}", simulatedBlock.getBlockNumber(), simulatedBlock.getHash());
        } catch (Exception e) {
            log.error("❌ Simulated blockchain creation failed: {}", e.getMessage());
            if (scan.getStatus().equals("success")) {
                scan.setStatus("simulated_block_failed");
            }
        }

        // ===== STEP 3: Save scan event to MongoDB =====
        ScanEvent saved = scanEventRepository.save(scan);
        log.info("💾 Scan event saved to database with ID: {}", saved.getId());

        // ===== STEP 4: Update part stage =====
        try {
            partService.updatePartStage(partId, stage, operatorId);
            log.info("📦 Part {} stage updated to: {}", partId, stage);
        } catch (Exception e) {
            log.error("❌ Failed to update part stage: {}", e.getMessage());
        }

        // ===== STEP 5: Run AI anomaly detection =====
        try {
            anomalyService.checkForAnomalies(saved);
            log.info("🤖 AI anomaly detection completed");
        } catch (Exception e) {
            log.error("❌ AI anomaly detection failed: {}", e.getMessage());
        }

        return saved;
    }

    public List<ScanEvent> getRecentScans() {
        return scanEventRepository.findTop20ByOrderByTimestampDesc();
    }
}