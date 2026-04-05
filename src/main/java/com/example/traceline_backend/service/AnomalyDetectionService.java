package com.example.traceline_backend.service;
import com.example.traceline_backend.model.Anomaly;
import com.example.traceline_backend.model.ScanEvent;
import com.example.traceline_backend.repository.AnomalyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final AnomalyRepository anomalyRepository;
    private final Random random = new Random();

    public void checkForAnomalies(ScanEvent scan) {
        // Simulate AI logic: duplicate scan, time gap, etc.
        if (random.nextInt(100) < 5) {  // 5% chance
            Anomaly anomaly = new Anomaly();
            anomaly.setSeverity(random.nextInt(100) > 80 ? "critical" : "medium");
            anomaly.setTitle("Possible duplicate scan detected");
            anomaly.setDescription("Scan for part " + scan.getPartId() + " at " + scan.getStage() + " appears suspicious.");
            anomaly.setPartId(scan.getPartId());
            anomaly.setOperatorId(scan.getOperatorId());
            anomaly.setLocation(scan.getStage() + " Station");
            anomaly.setDetectedAt(LocalDateTime.now());
            anomaly.setAiConfidence(70 + random.nextInt(25));
            anomaly.setResolved(false);
            anomalyRepository.save(anomaly);
        }
    }

    public List<Anomaly> getActiveAnomalies() {
        return anomalyRepository.findByResolvedFalse();
    }

    public List<Anomaly> findByOperatorId(String opId) {
        return anomalyRepository.findByOperatorId(opId);
    }

    public List<Anomaly> getAllAnomalies() {
        return anomalyRepository.findAll();
    }
}