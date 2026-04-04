package com.example.traceline_backend.controller;

import com.example.traceline_backend.dto.LoginRequest;
import com.example.traceline_backend.dto.RegisterRequest;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.UserRepository;
import com.example.traceline_backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user != null && encoder.matches(request.getPassword(), user.getPassword())
                && user.getRole().equals(request.getRole())) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            // Create HttpOnly cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);      // prevents JavaScript access
            cookie.setSecure(false);       // set to true in production (HTTPS only)
            cookie.setPath("/");           // available to whole app
            cookie.setMaxAge(24 * 60 * 60); // 1 day (same as token expiration)
            response.addCookie(cookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("role", user.getRole());
            responseBody.put("name", user.getName());
            responseBody.put("opId", user.getOpId());
            responseBody.put("token", token);
            // Do NOT send token in body anymore
            return responseBody;
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

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete cookie
        response.addCookie(cookie);
        return Map.of("message", "Logged out");
    }
}


