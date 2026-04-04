package com.example.traceline_backend.controller;
import com.example.traceline_backend.dto.DashboardMetrics;
import com.example.traceline_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    public DashboardMetrics getMetrics() {
        return dashboardService.getCurrentMetrics();
    }

    // Additional endpoint for simple KPIs if needed
    @GetMapping("/kpis")
    public Map<String, Object> getKpis() {
        DashboardMetrics m = dashboardService.getCurrentMetrics();
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("partsInProduction", m.getPartsInProduction());
        kpis.put("trackingAccuracy", m.getTrackingAccuracy());
        kpis.put("anomalies", m.getAnomalies());
        kpis.put("blockchainTXs", m.getBlockchainTXs());
        return kpis;
    }
}