package com.example.traceline_backend.controller;


import com.example.traceline_backend.model.Anomaly;
import com.example.traceline_backend.service.AnomalyDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/anomalies")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyDetectionService anomalyService;

    @GetMapping
    public List<Anomaly> getActiveAnomalies() {
        return anomalyService.getActiveAnomalies();
    }
}
