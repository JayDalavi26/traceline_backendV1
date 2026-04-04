package com.example.traceline_backend.controller;

import com.example.traceline_backend.dto.ScanRequest;
import com.example.traceline_backend.model.ScanEvent;
import com.example.traceline_backend.service.ScanService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/scans")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping
    public ScanEvent recordScan(@RequestBody ScanRequest request) {
        return scanService.recordScan(request.getPartId(), request.getStage(),
                request.getOperatorId(), request.getOperatorName());
    }

    @GetMapping("/recent")
    public List<ScanEvent> getRecentScans() {
        return scanService.getRecentScans();
    }
}
