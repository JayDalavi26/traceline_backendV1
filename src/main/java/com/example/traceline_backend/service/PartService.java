package com.example.traceline_backend.service;

import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartService {

    private final PartRepository partRepository;

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Part registerPart(Part part) {
        part.setRiskScore(0);
        part.setStatus("OK");
        part.setLastScanTime(LocalDateTime.now());
        return partRepository.save(part);
    }

    public Part updatePartStage(String partId, String newStage, String operatorId) {
        Part part = partRepository.findByPartId(partId);
        if (part != null) {
            part.setStage(newStage);
            part.setOperatorId(operatorId);
            part.setLastScanTime(LocalDateTime.now());
            return partRepository.save(part);
        } else {
            log.warn("Part {} not found, creating new part record", partId);
            // Create new part if it doesn't exist
            Part newPart = new Part();
            newPart.setPartId(partId);
            newPart.setStage(newStage);
            newPart.setOperatorId(operatorId);
            newPart.setLastScanTime(LocalDateTime.now());
            newPart.setRiskScore(0);
            newPart.setStatus("OK");
            newPart.setQualityScore(100);
            return partRepository.save(newPart);
        }
    }
}