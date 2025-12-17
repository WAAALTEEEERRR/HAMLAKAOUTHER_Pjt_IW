package com.eventmanagement.notificationservice.dto;

import com.eventmanagement.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour envoyer une notification
 * Contient les informations minimales nécessaires
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
    
    private Long eventId; // Optionnel (pour les notifications liées à un événement)
    
    @NotNull(message = "Le type de notification est obligatoire")
    private NotificationType type;
    
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    @NotBlank(message = "Le message est obligatoire")
    private String message;
    
    private String priority; // HIGH, MEDIUM, LOW (par défaut : MEDIUM)
    
    private String actionUrl; // URL de redirection (optionnel)
    
    private String metadata; // Données supplémentaires au format JSON (optionnel)
}