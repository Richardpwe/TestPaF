package com.group_d.paf_server.controller;

import com.group_d.paf_server.dto.JwtResponse;
import com.group_d.paf_server.dto.LoginRequest;
import com.group_d.paf_server.dto.MessageResponse;
import com.group_d.paf_server.dto.SignupRequest;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.repository.PlayerRepository;
import com.group_d.paf_server.security.jwt.JwtUtils;
import com.group_d.paf_server.security.PlayerDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticatePlayer(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        PlayerDetailsImpl playerDetails = (PlayerDetailsImpl) authentication.getPrincipal();
        List<String> roles = playerDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                playerDetails.getId(),
                playerDetails.getUsername()
                ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPlayer(@Valid @RequestBody SignupRequest signUpRequest) {
        if (playerRepository.existsByName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new player's account
        Player player = new Player(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));

        playerRepository.save(player);

        return ResponseEntity.ok(new MessageResponse("Player registered successfully!"));
    }

    // Hilfsklassen für Request und Response-Objekte
    // LoginRequest, SignupRequest, MessageResponse, JwtResponse
}
