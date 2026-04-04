package com.example.traceline_backend.dto;


public class RegisterRequest {
    private String username;
    private String password;
    private String role;      // "admin" or "operator"
    private String name;
    private String opId;      // null for admin
    private String operatorKey;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOpId() { return opId; }
    public void setOpId(String opId) { this.opId = opId; }
    public String getOperatorKey() { return operatorKey; }
    public void setOperatorKey(String operatorKey) { this.operatorKey = operatorKey; }
}
