package com.eventmanagement.registrationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer une nouvelle inscription
 * Contient uniquement les informations minimales nécessaires
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
    
    @NotNull(message = "L'ID de l'événement est obligatoire")
    private Long eventId;
    
    private String notes; // Commentaires optionnels
}