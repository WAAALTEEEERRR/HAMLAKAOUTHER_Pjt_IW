package com.eventmanagement.notificationservice.service;

import com.eventmanagement.notificationservice.dto.NotificationDTO;
import com.eventmanagement.notificationservice.dto.SendNotificationRequest;
import com.eventmanagement.notificationservice.enums.NotificationStatus;
import com.eventmanagement.notificationservice.enums.NotificationType;
import com.eventmanagement.notificationservice.model.Notification;
import com.eventmanagement.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IMPLÉMENTATION DU SERVICE NOTIFICATION
 * Contient toute la logique métier pour les notifications
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    /**
     * ENVOYER UNE NOTIFICATION
     */
    @Override
    public NotificationDTO sendNotification(SendNotificationRequest request) {
        // Créer la notification
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setEventId(request.getEventId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        notification.setActionUrl(request.getActionUrl());
        notification.setMetadata(request.getMetadata());
        
        // Marquer comme envoyée immédiatement
        // Dans un vrai système, on pourrait avoir une queue d'envoi
        notification.markAsSent();
        
        Notification savedNotification = notificationRepository.save(notification);
        
        return convertToDTO(savedNotification);
    }
    
    /**
     * ENVOYER DES NOTIFICATIONS EN MASSE
     */
    @Override
    public List<NotificationDTO> sendBulkNotifications(List<Long> userIds, SendNotificationRequest request) {
        List<NotificationDTO> sentNotifications = new ArrayList<>();
        
        for (Long userId : userIds) {
            SendNotificationRequest userRequest = new SendNotificationRequest();
            userRequest.setUserId(userId);
            userRequest.setEventId(request.getEventId());
            userRequest.setType(request.getType());
            userRequest.setTitle(request.getTitle());
            userRequest.setMessage(request.getMessage());
            userRequest.setPriority(request.getPriority());
            userRequest.setActionUrl(request.getActionUrl());
            userRequest.setMetadata(request.getMetadata());
            
            try {
                NotificationDTO sent = sendNotification(userRequest);
                sentNotifications.add(sent);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de la notification à l'utilisateur " + userId + ": " + e.getMessage());
            }
        }
        
        return sentNotifications;
    }
    
    /**
     * OBTENIR TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
     */
    @Override
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES NOTIFICATIONS NON LUES
     */
    @Override
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadNotificationsByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * COMPTER LES NOTIFICATIONS NON LUES
     */
    @Override
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadNotificationsByUserId(userId);
    }
    
    /**
     * OBTENIR LES NOTIFICATIONS RÉCENTES (24h)
     */
    @Override
    public List<NotificationDTO> getRecentNotifications(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return notificationRepository.findRecentNotificationsByUserId(userId, since).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * MARQUER UNE NOTIFICATION COMME LUE
     */
    @Override
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID : " + notificationId));
        
        notification.markAsRead();
        Notification updatedNotification = notificationRepository.save(notification);
        
        return convertToDTO(updatedNotification);
    }
    
    /**
     * MARQUER TOUTES LES NOTIFICATIONS COMME LUES
     */
    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotificationsByUserId(userId);
        
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }
    
    /**
     * OBTENIR UNE NOTIFICATION PAR ID
     */
    @Override
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID : " + id));
        return convertToDTO(notification);
    }
    
    /**
     * SUPPRIMER UNE NOTIFICATION
     */
    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification non trouvée avec l'ID : " + id);
        }
        notificationRepository.deleteById(id);
    }
    
    /**
     * SUPPRIMER TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
     */
    @Override
    public void deleteAllUserNotifications(Long userId) {
        List<Notification> userNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notificationRepository.deleteAll(userNotifications);
    }
    
    /**
     * OBTENIR LES NOTIFICATIONS PAR TYPE
     */
    @Override
    public List<NotificationDTO> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES NOTIFICATIONS D'UN ÉVÉNEMENT
     */
    @Override
    public List<NotificationDTO> getEventNotifications(Long eventId) {
        return notificationRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ==========================================
    // MÉTHODES TEMPLATES (Notifications pré-formatées)
    // ==========================================
    
    /**
     * Notification de confirmation d'inscription
     */
    @Override
    public NotificationDTO sendRegistrationConfirmation(Long userId, Long eventId, String eventTitle) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId(userId);
        request.setEventId(eventId);
        request.setType(NotificationType.REGISTRATION_CONFIRMATION);
        request.setTitle("✅ Inscription confirmée");
        request.setMessage(String.format(
            "Votre inscription à l'événement '%s' a été confirmée avec succès ! " +
            "Vous recevrez un rappel avant le début de l'événement.",
            eventTitle
        ));
        request.setPriority("HIGH");
        request.setActionUrl("/events/" + eventId);
        
        return sendNotification(request);
    }
    
    /**
     * Notification d'annulation d'inscription
     */
    @Override
    public NotificationDTO sendRegistrationCancelled(Long userId, Long eventId, String eventTitle) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId(userId);
        request.setEventId(eventId);
        request.setType(NotificationType.REGISTRATION_CANCELLED);
        request.setTitle("❌ Désinscription confirmée");
        request.setMessage(String.format(
            "Vous vous êtes désinscrit de l'événement '%s'. " +
            "Vous pouvez vous réinscrire à tout moment si des places sont disponibles.",
            eventTitle
        ));
        request.setPriority("MEDIUM");
        request.setActionUrl("/events/" + eventId);
        
        return sendNotification(request);
    }
    
    /**
     * Notification de modification d'événement
     */
    @Override
    public NotificationDTO sendEventUpdated(Long userId, Long eventId, String eventTitle, String changes) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId(userId);
        request.setEventId(eventId);
        request.setType(NotificationType.EVENT_UPDATED);
        request.setTitle("📝 Événement modifié");
        request.setMessage(String.format(
            "L'événement '%s' auquel vous êtes inscrit a été modifié. " +
            "Modifications : %s. Consultez les détails pour plus d'informations.",
            eventTitle, changes
        ));
        request.setPriority("HIGH");
        request.setActionUrl("/events/" + eventId);
        
        return sendNotification(request);
    }
    
    /**
     * Notification d'annulation d'événement
     */
    @Override
    public NotificationDTO sendEventCancelled(Long userId, Long eventId, String eventTitle) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId(userId);
        request.setEventId(eventId);
        request.setType(NotificationType.EVENT_CANCELLED);
        request.setTitle("🚫 Événement annulé");
        request.setMessage(String.format(
            "Nous sommes désolés de vous informer que l'événement '%s' a été annulé. " +
            "Votre inscription a été automatiquement annulée. " +
            "Nous vous informerons si l'événement est reprogrammé.",
            eventTitle
        ));
        request.setPriority("HIGH");
        request.setActionUrl("/events");
        
        return sendNotification(request);
    }
    
    /**
     * Rappel d'événement
     */
    @Override
    public NotificationDTO sendEventReminder(Long userId, Long eventId, String eventTitle, String timeUntil) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setUserId(userId);
        request.setEventId(eventId);
        request.setType(NotificationType.EVENT_REMINDER);
        request.setTitle("⏰ Rappel d'événement");
        request.setMessage(String.format(
            "L'événement '%s' commence %s ! " +
            "N'oubliez pas d'être présent. Consultez les détails pour plus d'informations.",
            eventTitle, timeUntil
        ));
        request.setPriority("HIGH");
        request.setActionUrl("/events/" + eventId);
        
        return sendNotification(request);
    }
    
    // ==========================================
    // MÉTHODES UTILITAIRES (Conversions)
    // ==========================================
    
    /**
     * Convertir Entity → DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setUserEmail(notification.getUserEmail());
        dto.setUserName(notification.getUserName());
        dto.setEventId(notification.getEventId());
        dto.setEventTitle(notification.getEventTitle());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setStatus(notification.getStatus());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSentAt(notification.getSentAt());
        dto.setReadAt(notification.getReadAt());
        dto.setPriority(notification.getPriority());
        dto.setActionUrl(notification.getActionUrl());
        dto.setMetadata(notification.getMetadata());
        
        // Champs calculés
        dto.setIsRead(notification.getStatus() == NotificationStatus.READ);
        dto.setIsNew(notification.getCreatedAt() != null && 
                     notification.getCreatedAt().isAfter(LocalDateTime.now().minusHours(24)));
        
        return dto;
    }
}