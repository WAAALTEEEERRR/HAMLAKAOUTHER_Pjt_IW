package com.eventmanagement.userservice.dto;

import com.eventmanagement.userservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse de connexion (Login)
 * Retourne les informations de l'utilisateur connecté
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String message;

    // Constructeur pour envoyer uniquement un message (en cas d'erreur)
    public LoginResponse(String message) {
        this.message = message;
    }
}