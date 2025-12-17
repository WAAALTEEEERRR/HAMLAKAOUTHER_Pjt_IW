package com.eventmanagement.userservice.dto;

import com.eventmanagement.userservice.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour User
 * Utilisé pour envoyer/recevoir des données utilisateur via l'API
 * Les annotations @NotBlank, @Email, etc. valident automatiquement les données
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide (ex: user@example.com)")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
    
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;
    
    private String phone;
    
    @NotNull(message = "Le rôle est obligatoire (ORGANIZER ou PARTICIPANT)")
    private UserRole role;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}