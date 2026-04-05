package com.example.traceline_backend.dto;

public class DashboardMetrics {
    private long partsInProduction;
    private double trackingAccuracy;
    private int anomalies;
    private long blockchainTXs;
    private int activeOperators;
    private double avgCycleTimeHours;
    private double qualityRate;

    public DashboardMetrics() {
    }

    public DashboardMetrics(long partsInProduction, double trackingAccuracy, int anomalies, long blockchainTXs, int activeOperators, double avgCycleTimeHours, double qualityRate) {
        this.partsInProduction = partsInProduction;
        this.trackingAccuracy = trackingAccuracy;
        this.anomalies = anomalies;
        this.blockchainTXs = blockchainTXs;
        this.activeOperators = activeOperators;
        this.avgCycleTimeHours = avgCycleTimeHours;
        this.qualityRate = qualityRate;
    }

    public long getPartsInProduction() {
        return partsInProduction;
    }

    public void setPartsInProduction(long partsInProduction) {
        this.partsInProduction = partsInProduction;
    }

    public double getTrackingAccuracy() {
        return trackingAccuracy;
    }

    public void setTrackingAccuracy(double trackingAccuracy) {
        this.trackingAccuracy = trackingAccuracy;
    }

    public int getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(int anomalies) {
        this.anomalies = anomalies;
    }

    public long getBlockchainTXs() {
        return blockchainTXs;
    }

    public void setBlockchainTXs(long blockchainTXs) {
        this.blockchainTXs = blockchainTXs;
    }

    public int getActiveOperators() {
        return activeOperators;
    }

    public void setActiveOperators(int activeOperators) {
        this.activeOperators = activeOperators;
    }

    public double getAvgCycleTimeHours() {
        return avgCycleTimeHours;
    }

    public void setAvgCycleTimeHours(double avgCycleTimeHours) {
        this.avgCycleTimeHours = avgCycleTimeHours;
    }

    public double getQualityRate() {
        return qualityRate;
    }

    public void setQualityRate(double qualityRate) {
        this.qualityRate = qualityRate;
    }
}
