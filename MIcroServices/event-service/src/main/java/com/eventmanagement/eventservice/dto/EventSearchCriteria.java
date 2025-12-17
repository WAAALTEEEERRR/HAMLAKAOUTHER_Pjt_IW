package com.eventmanagement.eventservice.dto;

import com.eventmanagement.eventservice.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les critères de recherche d'événements
 * Utilisé pour l'endpoint GET /api/events/search
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchCriteria {
    
    private String keyword;          // Recherche dans le titre et la description
    private String location;         // Recherche par lieu
    private String category;         // Recherche par catégorie
    private LocalDateTime startDate; // Date de début (événements après cette date)
    private LocalDateTime endDate;   // Date de fin (événements avant cette date)
    private EventStatus status;      // Recherche par statut
    private Long organizerId;        // Recherche par organisateur
    private Boolean isFree;          // Recherche événements gratuits/payants
    private Double maxPrice;         // Prix maximum
    private Boolean onlyAvailable;   // Uniquement les événements avec places disponibles
}