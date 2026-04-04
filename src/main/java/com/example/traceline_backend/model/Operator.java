package com.example.traceline_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "operators")
public class Operator {
    @Id
    private String id;
    private String opId;          // OP-0042
    private String name;
    private String level;
    private String status;        // on, off, flagged
    private int totalScansToday;
    private double accuracy;
    private int anomalyCount;
    private LocalDateTime lastLogin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalScansToday() {
        return totalScansToday;
    }

    public void setTotalScansToday(int totalScansToday) {
        this.totalScansToday = totalScansToday;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getAnomalyCount() {
        return anomalyCount;
    }

    public void setAnomalyCount(int anomalyCount) {
        this.anomalyCount = anomalyCount;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}