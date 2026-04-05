package com.example.traceline_backend.controller;

import com.example.traceline_backend.model.Operator;
import com.example.traceline_backend.repository.OperatorRepository;
import com.example.traceline_backend.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/operators")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OperatorController {

    private final OperatorRepository operatorRepository;
    private final EmailService emailService;

    public OperatorController(OperatorRepository operatorRepository, EmailService emailService) {
        this.operatorRepository = operatorRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public List<Operator> getAllOperators() {
        return operatorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Operator getOperatorById(@PathVariable String id) {
        return operatorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operator not found"));
    }

    @PostMapping
    public Operator addOperator(@RequestBody Operator operator) {
        operator.setStatus("active");
        operator.setTotalScansToday(0);
        operator.setAccuracy(100.0);
        operator.setAnomalyCount(0);
        return operatorRepository.save(operator);
    }

    @PutMapping("/{id}")
    public Operator updateOperator(@PathVariable String id, @RequestBody Operator operator) {
        if (!operatorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operator not found");
        }
        operator.setId(id);
        return operatorRepository.save(operator);
    }

    @DeleteMapping("/{id}")
    public void deleteOperator(@PathVariable String id, @RequestParam(required = false) String notifyEmail) {
        Operator op = operatorRepository.findById(id).orElse(null);
        if (op == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operator not found");
        }
        // Send email if provided
       if (op.getEmail() != null && !op.getEmail().isEmpty()) {
            emailService.sendDeletionNotice(op.getEmail(), op.getName());
        }
        operatorRepository.deleteById(id);
    }

    @PatchMapping("/{id}/status")
    public Operator updateStatus(@PathVariable String id, @RequestParam String status) {
        Operator op = operatorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operator not found"));
        op.setStatus(status);
        return operatorRepository.save(op);
    }

    @PatchMapping("/{id}/suspend")
    public Operator suspendOperator(@PathVariable String id,
                                    @RequestParam String reason,
                                    @RequestParam int days,
                                    @RequestParam(required = false) String notifyEmail) {
        Operator op = operatorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operator not found"));
        op.setStatus("suspended");
        op.setSuspensionReason(reason);
        op.setSuspensionEndDate(LocalDateTime.now().plusDays(days));
        operatorRepository.save(op);

        // Send email notification
        if (op.getEmail() != null && !op.getEmail().isEmpty()) {
            emailService.sendSuspensionNotice(op.getEmail(), op.getName(), reason, days);
        }
        return op;
    }
}