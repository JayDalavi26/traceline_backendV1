package com.example.traceline_backend.controller;

import com.example.traceline_backend.model.Operator;
import com.example.traceline_backend.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/operators")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorRepository operatorRepository;

    @GetMapping
    public List<Operator> getAllOperators() {
        return operatorRepository.findAll();
    }

    @PostMapping
    public Operator addOperator(@RequestBody Operator operator) {
        return operatorRepository.save(operator);
    }
}