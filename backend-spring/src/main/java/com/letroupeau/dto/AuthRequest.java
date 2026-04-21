package com.letroupeau.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// L'utilisation des Record Java 17 permet de créer des DTOs immuables très proprement
public record AuthRequest(
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    String email,
    
    @NotBlank(message = "Le mot de passe est requis")
    String password
) {}