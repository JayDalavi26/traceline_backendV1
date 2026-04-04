package com.example.traceline_backend.controller;


import com.example.traceline_backend.dto.LoginRequest;
import com.example.traceline_backend.dto.RegisterRequest;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.UserRepository;
import com.example.traceline_backend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user != null && encoder.matches(request.getPassword(), user.getPassword())
                && user.getRole().equals(request.getRole())) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("name", user.getName());
            response.put("opId", user.getOpId());
            return response;
        }
        throw new RuntimeException("Invalid credentials");
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setName(request.getName());
        user.setOpId(request.getOpId());
        user.setOperatorKey(request.getOperatorKey());

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }
}
