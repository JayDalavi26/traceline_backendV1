// repository/PartRepository.java
package com.example.traceline_backend.repository;

import com.example.traceline_backend.model.Part;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PartRepository extends MongoRepository<Part, String> {
    List<Part> findByOperatorId(String operatorId);
    List<Part> findByStage(String stage);
    Part findByPartId(String partId);

    long countByQualityScoreGreaterThan(int i);
}