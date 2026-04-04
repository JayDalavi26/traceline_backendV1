// repository/BlockRepository.java
package com.example.traceline_backend.repository;

import com.example.traceline_backend.model.Block;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends MongoRepository<Block, String> {
    Optional<Block> findTopByOrderByBlockNumberDesc();
    List<Block> findTop10ByOrderByBlockNumberDesc();
}