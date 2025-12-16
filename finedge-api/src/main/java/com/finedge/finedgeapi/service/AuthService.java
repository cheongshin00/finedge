package com.finedge.finedgeapi.service;

import com.finedge.finedgeapi.audit.Audit;
import com.finedge.finedgeapi.entity.Role;
import com.finedge.finedgeapi.entity.User;
import com.finedge.finedgeapi.repository.UserRepository;
import com.finedge.finedgeapi.security.CustomUserDetails;
import com.finedge.finedgeapi.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Audit(action = "Login", logRequest = true, logResponse = false)
    public String login(String username, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(userDetails.getUsername());

    }

    @Audit(action = "Register", logRequest = true, logResponse = false)
    public void register(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        HashSet<Role> role1 = new HashSet<Role>();
        role1.add(role);
        newUser.setRole(role1);

        userRepository.save(newUser);
    }
}
