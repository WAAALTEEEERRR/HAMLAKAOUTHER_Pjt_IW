package com.eventmanagement.notificationservice.enums;

/**
 * STATUTS D'UNE NOTIFICATION
 */
public enum NotificationStatus {
    PENDING,     // En attente d'envoi
    SENT,        // Envoyée (mais pas encore lue)
    READ,        // Lue par l'utilisateur
    FAILED       // Échec d'envoi
}