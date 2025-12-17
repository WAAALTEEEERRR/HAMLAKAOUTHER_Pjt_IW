package com.eventmanagement.eventservice.enums;

/**
 * STATUTS POSSIBLES D'UN ÉVÉNEMENT
 */
public enum EventStatus {
    DRAFT,        // Brouillon (en cours de création)
    PUBLISHED,    // Publié (visible pour les participants)
    CANCELLED,    // Annulé
    COMPLETED     // Terminé
}