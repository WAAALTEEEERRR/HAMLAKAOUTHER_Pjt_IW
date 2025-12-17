package com.eventmanagement.notificationservice.model;

import com.eventmanagement.notificationservice.enums.NotificationStatus;
import com.eventmanagement.notificationservice.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ENTITÉ NOTIFICATION (Table des notifications)
 * Représente une notification envoyée à un utilisateur
 * 
 * IMPORTANT : Stocke l'historique complet de toutes les notifications
 * Permet de tracer qui a reçu quoi et quand
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ID de l'utilisateur destinataire
     * (Référence vers user-service)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * Informations cachées de l'utilisateur (pour affichage)
     */
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "user_name")
    private String userName;
    
    /**
     * ID de l'événement concerné (optionnel)
     * Null pour les notifications générales
     */
    @Column(name = "event_id")
    private Long eventId;
    
    /**
     * Titre de l'événement (pour affichage)
     */
    @Column(name = "event_title")
    private String eventTitle;
    
    /**
     * Type de notification
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    /**
     * Titre de la notification
     * Exemple : "Inscription confirmée", "Événement modifié"
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * Message complet de la notification
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    /**
     * Statut de la notification
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;
    
    /**
     * Date de création
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * Date d'envoi
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    /**
     * Date de lecture
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    /**
     * Priorité (optionnel)
     * HIGH, MEDIUM, LOW
     */
    @Column
    private String priority = "MEDIUM";
    
    /**
     * URL de redirection (optionnel)
     * Exemple : "/events/5" pour rediriger vers les détails de l'événement
     */
    @Column
    private String actionUrl;
    
    /**
     * Données additionnelles au format JSON (optionnel)
     * Pour stocker des infos supplémentaires
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    // Méthodes automatiques
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
        if (priority == null) {
            priority = "MEDIUM";
        }
    }
    
    /**
     * Marquer comme envoyée
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }
    
    /**
     * Marquer comme lue
     */
    public void markAsRead() {
        if (this.status == NotificationStatus.SENT) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }
    
    /**
     * Marquer comme échouée
     */
    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }
}