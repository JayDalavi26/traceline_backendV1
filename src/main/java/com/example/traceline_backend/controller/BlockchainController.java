package com.example.traceline_backend.controller;


import com.example.traceline_backend.model.Block;
import com.example.traceline_backend.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/blockchain")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockRepository blockRepository;

    @GetMapping("/blocks")
    public List<Block> getLatestBlocks() {
        return blockRepository.findTop10ByOrderByBlockNumberDesc();
    }

    @GetMapping("/latest")
    public Block getLatestBlock() {
        return blockRepository.findTopByOrderByBlockNumberDesc().orElse(null);
    }
}
