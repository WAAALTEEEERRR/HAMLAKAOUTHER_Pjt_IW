package com.eventmanagement.notificationservice.enums;

/**
 * TYPES DE NOTIFICATIONS
 */
public enum NotificationType {
    REGISTRATION_CONFIRMATION,    // Confirmation d'inscription
    REGISTRATION_CANCELLED,       // Annulation d'inscription (désinscription)
    EVENT_UPDATED,                // Modification d'un événement
    EVENT_CANCELLED,              // Annulation d'un événement
    EVENT_REMINDER,               // Rappel avant l'événement (24h, 1h, etc.)
    EVENT_STARTING_SOON,          // L'événement commence bientôt
    EVENT_COMPLETED,              // Événement terminé (remerciement)
    NEW_EVENT_PUBLISHED,          // Nouvel événement publié (notification globale)
    REGISTRATION_STATUS_CHANGED,  // Changement de statut d'inscription
    GENERAL                       // Notification générale
}