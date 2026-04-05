package com.example.traceline_backend.controller;
import com.example.traceline_backend.dto.DashboardMetrics;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.*;
import com.example.traceline_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final PartRepository partRepository;
    private final ScanEventRepository scanEventRepository;
    private final AnomalyRepository anomalyRepository;
    private final BlockRepository blockRepository;

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
    @GetMapping("/metrics")
    public DashboardMetrics getMetrics(@AuthenticationPrincipal String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Operator: return only their own metrics
        if ("operator".equals(user.getRole())) {
            long operatorParts = partRepository.countByOperatorId(user.getOpId());
            long operatorScans = scanEventRepository.countByOperatorId(user.getOpId());
            long operatorAnomalies = anomalyRepository.countByOperatorId(user.getOpId());
            // Operators see only their own blockchain transactions? For simplicity, use total blocks.
            long totalBlocks = blockRepository.count();

            // Calculate tracking accuracy for this operator (based on their scans and anomalies)
            double trackingAccuracy = operatorScans == 0 ? 100.0 :
                    (1 - (double) operatorAnomalies / operatorScans) * 100;

            return new DashboardMetrics(
                    operatorParts,               // partsInProduction
                    Math.round(trackingAccuracy * 10) / 10.0,
                    (int) operatorAnomalies,
                    totalBlocks,
                    (int) operatorScans,         // activeOperators placeholder
                    0.0,                         // avgCycleTimeHours – operator specific (optional)
                    100.0                     // qualityRate – operator specific (optional)
            );
        }

        // Admin: global metrics
        return dashboardService.getCurrentMetrics();
    }

}
