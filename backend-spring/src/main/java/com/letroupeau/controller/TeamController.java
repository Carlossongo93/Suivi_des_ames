package com.letroupeau.controller;

import com.letroupeau.dto.UserStatsDto;
import com.letroupeau.model.User;
import com.letroupeau.repository.ContactRepository;
import com.letroupeau.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public TeamController(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<UserStatsDto>> getTeamDashboard() {
        // 1. Identifier qui fait la requête
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        List<User> usersToDisplay;
        String userRole = currentUser.getRole().toString(); // "ADMIN", "SUPERVISEUR", "LEADER", etc.

        // 2. Aiguillage selon le rôle
        if ("ADMIN".equals(userRole) || "SUPERVISEUR".equals(userRole)) {
            // Le Superviseur voit TOUT le monde
            usersToDisplay = userRepository.findAll();
        } else {
            // Le Leader ne voit que les membres de son équipe
            if (currentUser.getTeam() != null) {
                usersToDisplay = userRepository.findByTeamId(currentUser.getTeam().getId());
            } else {
                // Sécurité : s'il n'a pas d'équipe assignée, il ne voit que lui-même
                usersToDisplay = List.of(currentUser);
            }
        }

        // 3. Transformation en DTO avec calcul des statistiques
        List<UserStatsDto> stats = usersToDisplay.stream().map(u -> {
            // Compte le nombre de contacts créés/gérés par cet utilisateur
            long count = contactRepository.countByCreatedById(u.getId()); 
            String teamName = (u.getTeam() != null) ? u.getTeam().getName() : "Aucune équipe";
            
            return new UserStatsDto(
                    u.getId(), 
                    u.getFirstName(), 
                    u.getLastName(), 
                    u.getEmail(), 
                    u.getRole().toString(), 
                    teamName, 
                    count
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(stats);
    }
}