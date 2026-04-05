package com.example.traceline_backend.controller;

import com.example.traceline_backend.dto.ScanRequest;
import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.model.ScanEvent;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.PartRepository;
import com.example.traceline_backend.repository.ScanEventRepository;
import com.example.traceline_backend.repository.UserRepository;
import com.example.traceline_backend.service.ScanService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/scans")
public class ScanController {

    private final ScanService scanService;
    private final UserRepository userRepository;
    private final ScanEventRepository scanEventRepository;
    private final PartRepository partRepository;

    public ScanController(ScanService scanService, UserRepository userRepository, ScanEventRepository scanEventRepository, PartRepository partRepository) {
        this.scanService = scanService;
        this.userRepository = userRepository;
        this.scanEventRepository = scanEventRepository;
        this.partRepository = partRepository;
    }

    @PostMapping
    public ScanEvent recordScan(@RequestBody ScanRequest request,
                                @AuthenticationPrincipal String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Ensure operator can only scan parts assigned to them
        Part part = partRepository.findByPartId(request.getPartId())
                .orElseThrow(() -> new RuntimeException("Part not found"));
        if ("operator".equals(user.getRole()) && !part.getOperatorId().equals(user.getOpId())) {
            throw new RuntimeException("You are not authorized to scan this part");
        }
        return scanService.recordScan(request.getPartId(), request.getStage(),
                request.getOperatorId(), request.getOperatorName());
    }

    @GetMapping("/recent")
    public List<ScanEvent> getRecentScans(@AuthenticationPrincipal String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if ("operator".equals(user.getRole())) {
            return scanEventRepository.findByOperatorId(user.getOpId());
        }
        return scanEventRepository.findTop20ByOrderByTimestampDesc();
    }

    @DeleteMapping
    public void deleteAllScans() {
        scanEventRepository.deleteAll();
    }
}
