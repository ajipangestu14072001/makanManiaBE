package com.example.makanmaniabe.controller;

import com.example.makanmaniabe.jwt.JwtUtils;
import com.example.makanmaniabe.model.ApiResponse;
import com.example.makanmaniabe.model.ERole;
import com.example.makanmaniabe.model.Role;
import com.example.makanmaniabe.model.User;
import com.example.makanmaniabe.payload.JwtResponse;
import com.example.makanmaniabe.payload.LoginRequest;
import com.example.makanmaniabe.payload.SignupRequest;
import com.example.makanmaniabe.repository.RoleRepository;
import com.example.makanmaniabe.repository.UserRepository;
import com.example.makanmaniabe.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getNamaLengkap(),userDetails.getTelepon(),userDetails.getEmail(), roles);
        ApiResponse<JwtResponse> response = new ApiResponse<>(200, "Authentication successful", jwtResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Error: Email is already in use!", null));
        }

        User user = new User(signUpRequest.getNamaLengkap(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),signUpRequest.getTelepon());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        ApiResponse<User> response = new ApiResponse<>(200, "User registered successfully!", user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "User not found", null));
        }

        User user = userOptional.get();
        ApiResponse<User> response = new ApiResponse<>(200, "User profile retrieved successfully", user);

        return ResponseEntity.ok(response);
    }
}

