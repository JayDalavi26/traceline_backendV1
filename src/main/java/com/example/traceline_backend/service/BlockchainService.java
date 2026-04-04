package com.example.traceline_backend.service;

import com.example.traceline_backend.model.Block;
import com.example.traceline_backend.repository.BlockRepository;
import com.example.traceline_backend.util.BlockchainHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlockchainService {

    private final BlockRepository blockRepository;

    public Block createBlock(String data, String partId, String type) {
        Optional<Block> lastBlockOpt = blockRepository.findTopByOrderByBlockNumberDesc();
        int newNumber = lastBlockOpt.map(b -> b.getBlockNumber() + 1).orElse(1);
        String previousHash = lastBlockOpt.map(Block::getHash).orElse("0".repeat(64));

        String hashData = previousHash + data + newNumber + LocalDateTime.now().toString();
        String hash = BlockchainHasher.sha256(hashData);

        Block block = new Block();
        block.setBlockNumber(newNumber);
        block.setPreviousHash(previousHash);
        block.setHash(hash);
        block.setTimestamp(LocalDateTime.now());
        block.setData(data);
        block.setPartId(partId);
        block.setType(type);

        return blockRepository.save(block);
    }

    public Block getLatestBlock() {
        return blockRepository.findTopByOrderByBlockNumberDesc().orElse(null);
    }
}