package com.example.traceline_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;


@Document(collection = "parts")
public class Part {
    @Id
    private String id;
    private String partId;          // TL-2024-8821
    private String batch;
    private String material;
    private String stage;           // Cutting, Drilling, etc.
    private String operatorName;
    private String operatorId;
    private LocalDateTime lastScanTime;
    private int riskScore;          // 0-100
    private String status;          // OK, Delay, Anomaly
    private String blockchainBlockHash;
    private int qualityScore;

    // 0-100

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getLastScanTime() {
        return lastScanTime;
    }

    public void setLastScanTime(LocalDateTime lastScanTime) {
        this.lastScanTime = lastScanTime;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBlockchainBlockHash() {
        return blockchainBlockHash;
    }

    public void setBlockchainBlockHash(String blockchainBlockHash) {
        this.blockchainBlockHash = blockchainBlockHash;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

}