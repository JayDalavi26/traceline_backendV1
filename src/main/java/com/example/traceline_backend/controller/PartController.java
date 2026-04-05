package com.example.traceline_backend.controller;


import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.PartRepository;
import com.example.traceline_backend.repository.UserRepository;
import com.example.traceline_backend.service.PartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/parts")
public class PartController {

    private final PartService partService;
    private final UserRepository userRepository;
    private final PartRepository partRepository;

    public PartController(PartService partService, UserRepository userRepository, PartRepository partRepository) {
        this.partService = partService;
        this.userRepository = userRepository;
        this.partRepository = partRepository;
    }

    @GetMapping("/{partId}")
    public Part getPartByPartId(@PathVariable String partId,
                                @AuthenticationPrincipal String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        // Admin can access any part
        if ("admin".equals(currentUser.getRole())) {
            return part;
        }

        // Operator can only access parts assigned to them
        if ("operator".equals(currentUser.getRole())) {
            if (part.getOperatorId() != null && part.getOperatorId().equals(currentUser.getOpId())) {
                return part;
            } else {
                throw new RuntimeException("You are not authorized to view this part");
            }
        }

        throw new RuntimeException("Unauthorized");
    }

    @GetMapping
    public List<Part> getAllParts(@AuthenticationPrincipal String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if ("operator".equals(user.getRole())) {
            // Operator sees only parts assigned to them
            return partRepository.findByOperatorId(user.getOpId());
        }
        // Admin sees all parts
        return partRepository.findAll();
    }

    @PostMapping
    public Part registerPart(@RequestBody Part part) {
        return partService.registerPart(part);
    }

    @PutMapping("/{partId}/stage")
    public Part updateStage(@PathVariable String partId, @RequestParam String stage, @RequestParam String operatorId) {
        return partService.updatePartStage(partId, stage, operatorId);
    }

    @DeleteMapping
    public void deleteAllParts() {
        partRepository.deleteAll();
    }
}
