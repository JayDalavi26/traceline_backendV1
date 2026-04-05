package com.example.traceline_backend.controller;


import com.example.traceline_backend.model.Anomaly;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.UserRepository;
import com.example.traceline_backend.service.AnomalyDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/anomalies")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyDetectionService anomalyService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Anomaly> getAnomalies(@AuthenticationPrincipal String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if ("operator".equals(user.getRole())) {
            return anomalyService.findByOperatorId(user.getOpId());
        }
        return anomalyService.getAllAnomalies();
    }
}
