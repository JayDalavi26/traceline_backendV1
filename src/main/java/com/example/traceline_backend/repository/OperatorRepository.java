// repository/OperatorRepository.java
package com.example.traceline_backend.repository;

import com.example.traceline_backend.model.Operator;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OperatorRepository extends MongoRepository<Operator, String> {
    Optional<Operator> findByOpId(String opId);

    long countByStatus(String on);
}