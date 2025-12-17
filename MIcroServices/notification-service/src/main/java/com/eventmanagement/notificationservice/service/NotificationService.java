package com.eventmanagement.notificationservice.service;

import com.eventmanagement.notificationservice.dto.NotificationDTO;
import com.eventmanagement.notificationservice.dto.SendNotificationRequest;
import com.eventmanagement.notificationservice.enums.NotificationType;

import java.util.List;

/**
 * INTERFACE DU SERVICE NOTIFICATION
 * Définit les opérations métier pour les notifications
 */
public interface NotificationService {
    
    /**
     * ENVOYER UNE NOTIFICATION
     * Crée et envoie une notification à un utilisateur
     */
    NotificationDTO sendNotification(SendNotificationRequest request);
    
    /**
     * ENVOYER DES NOTIFICATIONS EN MASSE
     * Envoie la même notification à plusieurs utilisateurs
     */
    List<NotificationDTO> sendBulkNotifications(List<Long> userIds, SendNotificationRequest request);
    
    /**
     * OBTENIR TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
     */
    List<NotificationDTO> getUserNotifications(Long userId);
    
    /**
     * OBTENIR LES NOTIFICATIONS NON LUES D'UN UTILISATEUR
     */
    List<NotificationDTO> getUnreadNotifications(Long userId);
    
    /**
     * COMPTER LES NOTIFICATIONS NON LUES
     */
    Long countUnreadNotifications(Long userId);
    
    /**
     * OBTENIR LES NOTIFICATIONS RÉCENTES (24h)
     */
    List<NotificationDTO> getRecentNotifications(Long userId);
    
    /**
     * MARQUER UNE NOTIFICATION COMME LUE
     */
    NotificationDTO markAsRead(Long notificationId);
    
    /**
     * MARQUER TOUTES LES NOTIFICATIONS D'UN UTILISATEUR COMME LUES
     */
    void markAllAsRead(Long userId);
    
    /**
     * OBTENIR UNE NOTIFICATION PAR ID
     */
    NotificationDTO getNotificationById(Long id);
    
    /**
     * SUPPRIMER UNE NOTIFICATION
     */
    void deleteNotification(Long id);
    
    /**
     * SUPPRIMER TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
     */
    void deleteAllUserNotifications(Long userId);
    
    /**
     * OBTENIR LES NOTIFICATIONS PAR TYPE
     */
    List<NotificationDTO> getNotificationsByType(NotificationType type);
    
    /**
     * OBTENIR LES NOTIFICATIONS D'UN ÉVÉNEMENT
     */
    List<NotificationDTO> getEventNotifications(Long eventId);
    
    // ==========================================
    // MÉTHODES UTILITAIRES POUR LES TEMPLATES
    // ==========================================
    
    /**
     * Envoyer une notification de confirmation d'inscription
     */
    NotificationDTO sendRegistrationConfirmation(Long userId, Long eventId, String eventTitle);
    
    /**
     * Envoyer une notification d'annulation d'inscription
     */
    NotificationDTO sendRegistrationCancelled(Long userId, Long eventId, String eventTitle);
    
    /**
     * Envoyer une notification de modification d'événement
     */
    NotificationDTO sendEventUpdated(Long userId, Long eventId, String eventTitle, String changes);
    
    /**
     * Envoyer une notification d'annulation d'événement
     */
    NotificationDTO sendEventCancelled(Long userId, Long eventId, String eventTitle);
    
    /**
     * Envoyer un rappel d'événement
     */
    NotificationDTO sendEventReminder(Long userId, Long eventId, String eventTitle, String timeUntil);
}