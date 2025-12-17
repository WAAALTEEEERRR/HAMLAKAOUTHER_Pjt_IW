package com.eventmanagement.eventservice.model;

import com.eventmanagement.eventservice.enums.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ EVENT (Table des événements)
 * Représente un événement dans la base de données
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title; // Titre de l'événement
    
    @Column(columnDefinition = "TEXT")
    private String description; // Description détaillée
    
    @Column(nullable = false)
    private String location; // Lieu de l'événement
    
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate; // Date et heure de l'événement
    
    @Column(name = "end_date")
    private LocalDateTime endDate; // Date de fin (optionnel)
    
    @Column(nullable = false)
    private Integer capacity; // Nombre maximum de participants
    
    @Column(name = "current_participants")
    private Integer currentParticipants = 0; // Nombre actuel de participants
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT; // Statut de l'événement
    
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId; // ID de l'organisateur (référence au User Service)
    
    @Column(name = "organizer_name")
    private String organizerName; // Nom de l'organisateur (pour affichage)
    
    @Column
    private String category; // Catégorie (ex: Conférence, Concert, Formation, etc.)
    
    @Column(name = "is_free")
    private Boolean isFree = true; // Événement gratuit ou payant
    
    @Column
    private Double price; // Prix (si payant)
    
    // Relation OneToMany avec les fichiers
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventFile> files = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Méthodes automatiques appelées avant persist et update
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentParticipants == null) {
            currentParticipants = 0;
        }
        if (status == null) {
            status = EventStatus.DRAFT;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public boolean isFull() {
        return currentParticipants >= capacity;
    }
    
    public boolean canAcceptParticipant() {
        return status == EventStatus.PUBLISHED && !isFull();
    }
}