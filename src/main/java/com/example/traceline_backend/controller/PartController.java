package com.example.traceline_backend.controller;


import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.service.PartService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public List<Part> getAllParts() {
        return partService.getAllParts();
    }

    @PostMapping
    public Part registerPart(@RequestBody Part part) {
        return partService.registerPart(part);
    }

    @PutMapping("/{partId}/stage")
    public Part updateStage(@PathVariable String partId, @RequestParam String stage, @RequestParam String operatorId) {
        return partService.updatePartStage(partId, stage, operatorId);
    }
}
