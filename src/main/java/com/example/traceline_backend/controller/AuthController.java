package com.example.traceline_backend.controller;

import com.example.traceline_backend.dto.LoginRequest;
import com.example.traceline_backend.dto.RegisterOperatorRequest;
import com.example.traceline_backend.dto.RegisterRequest;
import com.example.traceline_backend.model.Operator;
import com.example.traceline_backend.model.User;
import com.example.traceline_backend.repository.OperatorRepository;
import com.example.traceline_backend.repository.UserRepository;
import com.example.traceline_backend.service.EmailService;
import com.example.traceline_backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final OperatorRepository operatorRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, OperatorRepository operatorRepository, EmailService emailService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.operatorRepository = operatorRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/users")
    public Object getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        // ========== ADD SUSPENSION CHECK HERE ==========
        if (user != null && "operator".equals(user.getRole())) {
            Operator operator = operatorRepository.findByOpId(user.getOpId()).orElse(null);
            if (operator != null && "suspended".equals(operator.getStatus())) {
                LocalDateTime now = LocalDateTime.now();
                if (operator.getSuspensionEndDate() != null && now.isBefore(operator.getSuspensionEndDate())) {
                    throw new RuntimeException("Account suspended until " + operator.getSuspensionEndDate() +
                            ". Reason: " + operator.getSuspensionReason());
                } else if (operator.getSuspensionEndDate() == null || now.isAfter(operator.getSuspensionEndDate())) {
                    // Auto‑reactivate if suspension period ended
                    operator.setStatus("active");
                    operator.setSuspensionReason(null);
                    operator.setSuspensionEndDate(null);
                    operatorRepository.save(operator);
                }
            }
        }
        // =============================================
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("jaya123"));
//        System.out.println("User Password"+user.getPassword()+ "User name"+user.getName()+"Role"+user.getRole());
//        boolean value = encoder.matches(request.getPassword(), user.getPassword());
//        System.out.println("Value : "+value);
        if (user != null && encoder.matches(request.getPassword(), user.getPassword())
                && user.getRole().equals(request.getRole())) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("role", user.getRole());
            responseBody.put("name", user.getName());
            responseBody.put("opId", user.getOpId());
            responseBody.put("token", token);   // optionally keep or remove
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

    @PostMapping("/register-operator")
    public Map<String, String> registerOperator(@RequestBody RegisterOperatorRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("operator");
        user.setName(request.getName());
        user.setOpId(request.getOpId());
        user.setOperatorKey(request.getName());
        userRepository.save(user);

        // Also save to Operator collection (optional)
        Operator operator = new Operator();
        operator.setOpId(request.getOpId());
        operator.setName(request.getName());
        operator.setLevel(request.getLevel());
        operator.setStatus("on");
        operator.setTotalScansToday(0);
        operator.setAccuracy(100.0);
        operator.setAnomalyCount(0);
        operator.setEmail(request.getEmail());  // add this line
        operatorRepository.save(operator);

        // Send email
        emailService.sendCredentials(request.getEmail(), request.getUsername(), request.getPassword(), "Operator");

        return Map.of("message", "Operator registered and email sent");
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


