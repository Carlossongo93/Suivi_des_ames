package com.letroupeau.controller;

import com.letroupeau.dto.AuthRequest;
import com.letroupeau.dto.AuthResponse;
import com.letroupeau.model.User;
import com.letroupeau.repository.UserRepository;
import com.letroupeau.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; // <-- IMPORT MANQUANT AJOUTÉ
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // <-- IMPORT MANQUANT AJOUTÉ
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // <-- IMPORT MANQUANT AJOUTÉ

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
                    
            String jwtToken = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(jwtToken));
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }
}