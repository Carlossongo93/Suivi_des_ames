package com.letroupeau.dto;

import jakarta.validation.constraints.NotBlank;

public record InteractionRequest(
    @NotBlank(message = "Le type d'interaction est requis")
    String type,
    String subType,
    String notes
) {}