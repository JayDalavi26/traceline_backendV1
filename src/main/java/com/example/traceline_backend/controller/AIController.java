package com.example.traceline_backend.controller;


import com.example.traceline_backend.service.AIPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AIController {

    private final AIPredictionService aiService;

    @GetMapping("/insights")
    public Map<String, Object> getInsights() {
        return aiService.getDashboardInsights();
    }

    @GetMapping("/bottleneck")
    public Map<String, Integer> getBottleneck() {
        return aiService.getBottleneckForecast();
    }
}
