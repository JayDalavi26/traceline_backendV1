// repository/PartRepository.java
package com.example.traceline_backend.repository;

import com.example.traceline_backend.model.Part;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PartRepository extends MongoRepository<Part, String> {
    List<Part> findByOperatorId(String operatorId);
    long countByOperatorId(String operatorId);
    Optional<Part> findByPartId(String partId);
    long countByQualityScoreGreaterThan(int i);
}