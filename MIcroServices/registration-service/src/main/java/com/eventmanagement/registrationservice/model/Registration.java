package com.eventmanagement.registrationservice.model;

import com.eventmanagement.registrationservice.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ENTITÉ REGISTRATION (Table des inscriptions)
 * Représente l'inscription d'un utilisateur à un événement
 * 
 * IMPORTANT : Cette table fait le lien entre User (user-service) et Event (event-service)
 * Elle stocke uniquement les IDs, pas les objets complets
 */
@Entity
@Table(name = "registrations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
// La contrainte unique empêche un utilisateur de s'inscrire plusieurs fois au même événement
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * ID de l'utilisateur (référence vers user-service)
     * On ne stocke que l'ID, pas l'objet User complet
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * Informations cachées de l'utilisateur pour affichage
     * (évite d'appeler user-service à chaque fois)
     */
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "user_name")
    private String userName;
    
    /**
     * ID de l'événement (référence vers event-service)
     * On ne stocke que l'ID, pas l'objet Event complet
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    /**
     * Informations cachées de l'événement pour affichage
     * (évite d'appeler event-service à chaque fois)
     */
    @Column(name = "event_title")
    private String eventTitle;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    
    @Column(name = "event_location")
    private String eventLocation;
    
    /**
     * Statut de l'inscription
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;
    
    /**
     * Date d'inscription
     */
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    
    /**
     * Date de mise à jour (changement de statut)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Notes ou commentaires (optionnel)
     */
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // Méthodes automatiques
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = RegistrationStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}