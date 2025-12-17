package com.eventmanagement.registrationservice.enums;

/**
 * STATUTS POSSIBLES D'UNE INSCRIPTION
 */
public enum RegistrationStatus {
    PENDING,      // En attente de confirmation
    CONFIRMED,    // Confirmée
    CANCELLED,    // Annulée (désinscription)
    ATTENDED,     // A participé (présent à l'événement)
    NO_SHOW       // Absent (inscrit mais pas venu)
}