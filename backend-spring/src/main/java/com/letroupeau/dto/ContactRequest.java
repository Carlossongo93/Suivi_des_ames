package com.letroupeau.dto;

import jakarta.validation.constraints.NotBlank;

public record ContactRequest(
    @NotBlank(message = "Le prénom est requis")
    String firstName,
    
    @NotBlank(message = "Le nom est requis")
    String lastName,
    
    String phone,
    String address
) {}