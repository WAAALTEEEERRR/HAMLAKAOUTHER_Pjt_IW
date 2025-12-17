package com.eventmanagement.notificationservice.dto;

import com.eventmanagement.notificationservice.enums.NotificationStatus;
import com.eventmanagement.notificationservice.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour transférer les données d'une notification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    private Long id;
    
    // Informations utilisateur
    private Long userId;
    private String userEmail;
    private String userName;
    
    // Informations événement (optionnel)
    private Long eventId;
    private String eventTitle;
    
    // Contenu de la notification
    private NotificationType type;
    private String title;
    private String message;
    
    // Statut et dates
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    
    // Informations supplémentaires
    private String priority;
    private String actionUrl;
    private String metadata;
    
    // Champs calculés
    private Boolean isRead;
    private Boolean isNew; // Créée dans les dernières 24h
}