// repository/AnomalyRepository.java
package com.example.traceline_backend.repository;

import com.example.traceline_backend.model.Anomaly;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnomalyRepository extends MongoRepository<Anomaly, String> {
    List<Anomaly> findByResolvedFalse();
    List<Anomaly> findBySeverity(String severity);
    long countByDetectedAtAfter(LocalDateTime timestamp);

    long countByResolvedFalse();

    List<Anomaly> findByOperatorId(String opId);

    long countByOperatorId(String opId);
}