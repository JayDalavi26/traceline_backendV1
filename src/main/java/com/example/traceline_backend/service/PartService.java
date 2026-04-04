package com.example.traceline_backend.service;

import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartService {
    private final PartRepository partRepository;

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Part registerPart(Part part) {
        part.setRiskScore(0);
        part.setStatus("OK");
        return partRepository.save(part);
    }

    public Part updatePartStage(String partId, String newStage, String operatorId) {
        Part part = partRepository.findByPartId(partId);
        if (part != null) {
            part.setStage(newStage);
            part.setOperatorId(operatorId);
            part.setLastScanTime(java.time.LocalDateTime.now());
            return partRepository.save(part);
        }
        return null;
    }
}