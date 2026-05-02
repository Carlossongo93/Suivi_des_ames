package com.letroupeau.dto;

// Utilisation d'un "record" Java (très pratique pour les DTOs simples)
public record UserStatsDto(
    Long id,
    String fullName,
    String email,
    String role,
    String teamName,
    long contactsCount // Nombre d'âmes suivies
) {}