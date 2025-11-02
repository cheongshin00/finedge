package com.finedge.finedgeapi.controller;


import com.finedge.finedgeapi.entity.Role;
import com.finedge.finedgeapi.entity.User;
import com.finedge.finedgeapi.repository.UserRepository;
import com.finedge.finedgeapi.security.JwtUtil;
import com.finedge.finedgeapi.service.AuthService;
import io.jsonwebtoken.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.finedge.finedgeapi.entity.Role.CUSTOMER;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request){
        String username = request.get("username");
        String password = request.get("password");
        Role role = Role.valueOf(request.getOrDefault("role", "CUSTOMER").toUpperCase());

        authService.register(username, password, role);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> request){
        String username = request.get("username");
        String password = request.get("password");
        Role role = Role.valueOf(request.getOrDefault("role", "ADMIN").toUpperCase());

        authService.register(username, password, role);
        return ResponseEntity.ok("Admin registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request){
        String username = request.get("username");
        String password = request.get("password");

        String token = authService.login(username, password);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
