// repository/ScanEventRepository.java
package com.example.traceline_backend.repository;

import com.example.traceline_backend.model.Part;
import com.example.traceline_backend.model.ScanEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScanEventRepository extends MongoRepository<ScanEvent, String> {
    List<ScanEvent> findTop20ByOrderByTimestampDesc();
    List<ScanEvent> findByOperatorId(String operatorId);
    long countByOperatorId(String operatorId);
    long countByTimestampAfter(LocalDateTime timestamp);
}