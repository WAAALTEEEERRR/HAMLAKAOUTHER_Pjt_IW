package com.eventmanagement.notificationservice.controller;

import com.eventmanagement.notificationservice.dto.NotificationDTO;
import com.eventmanagement.notificationservice.dto.SendNotificationRequest;
import com.eventmanagement.notificationservice.enums.NotificationType;
import com.eventmanagement.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER NOTIFICATIONS (API REST)
 * Expose tous les endpoints pour gérer les notifications
 * Base URL : /api/notifications
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    // ==========================================
    // ENDPOINTS D'ENVOI DE NOTIFICATIONS
    // ==========================================
    
    /**
     * ENDPOINT : ENVOYER UNE NOTIFICATION
     * POST /api/notifications/send
     * 
     * Body JSON :
     * {
     *   "userId": 1,
     *   "eventId": 5,
     *   "type": "REGISTRATION_CONFIRMATION",
     *   "title": "Inscription confirmée",
     *   "message": "Votre inscription a été confirmée",
     *   "priority": "HIGH"
     * }
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationDTO> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        NotificationDTO notification = notificationService.sendNotification(request);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : ENVOYER DES NOTIFICATIONS EN MASSE
     * POST /api/notifications/send-bulk
     * 
     * Body JSON :
     * {
     *   "userIds": [1, 2, 3, 4, 5],
     *   "notification": {
     *     "type": "NEW_EVENT_PUBLISHED",
     *     "title": "Nouvel événement",
     *     "message": "Un nouvel événement vient d'être publié !",
     *     "eventId": 10
     *   }
     * }
     */
    @PostMapping("/send-bulk")
    public ResponseEntity<List<NotificationDTO>> sendBulkNotifications(
            @RequestBody Map<String, Object> request
    ) {
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) request.get("userIds");
        @SuppressWarnings("unchecked")
        Map<String, Object> notificationData = (Map<String, Object>) request.get("notification");
        
        SendNotificationRequest notificationRequest = new SendNotificationRequest();
        notificationRequest.setType(NotificationType.valueOf((String) notificationData.get("type")));
        notificationRequest.setTitle((String) notificationData.get("title"));
        notificationRequest.setMessage((String) notificationData.get("message"));
        notificationRequest.setEventId(notificationData.get("eventId") != null ? 
                                       ((Number) notificationData.get("eventId")).longValue() : null);
        notificationRequest.setPriority((String) notificationData.getOrDefault("priority", "MEDIUM"));
        
        List<NotificationDTO> notifications = notificationService.sendBulkNotifications(userIds, notificationRequest);
        return new ResponseEntity<>(notifications, HttpStatus.CREATED);
    }
    
    // ==========================================
    // ENDPOINTS TEMPLATES (Notifications pré-formatées)
    // ==========================================
    
    /**
     * ENDPOINT : ENVOYER CONFIRMATION D'INSCRIPTION
     * POST /api/notifications/registration-confirmation
     * 
     * Body JSON :
     * {
     *   "userId": 1,
     *   "eventId": 5,
     *   "eventTitle": "Conférence Tech 2024"
     * }
     */
    @PostMapping("/registration-confirmation")
    public ResponseEntity<NotificationDTO> sendRegistrationConfirmation(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventTitle = (String) request.get("eventTitle");
        
        NotificationDTO notification = notificationService.sendRegistrationConfirmation(userId, eventId, eventTitle);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : ENVOYER ANNULATION D'INSCRIPTION
     * POST /api/notifications/registration-cancelled
     */
    @PostMapping("/registration-cancelled")
    public ResponseEntity<NotificationDTO> sendRegistrationCancelled(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventTitle = (String) request.get("eventTitle");
        
        NotificationDTO notification = notificationService.sendRegistrationCancelled(userId, eventId, eventTitle);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : ENVOYER NOTIFICATION DE MODIFICATION D'ÉVÉNEMENT
     * POST /api/notifications/event-updated
     * 
     * Body JSON :
     * {
     *   "userId": 1,
     *   "eventId": 5,
     *   "eventTitle": "Conférence Tech 2024",
     *   "changes": "Date modifiée : 15/12/2024 → 20/12/2024"
     * }
     */
    @PostMapping("/event-updated")
    public ResponseEntity<NotificationDTO> sendEventUpdated(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventTitle = (String) request.get("eventTitle");
        String changes = (String) request.get("changes");
        
        NotificationDTO notification = notificationService.sendEventUpdated(userId, eventId, eventTitle, changes);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : ENVOYER ANNULATION D'ÉVÉNEMENT
     * POST /api/notifications/event-cancelled
     */
    @PostMapping("/event-cancelled")
    public ResponseEntity<NotificationDTO> sendEventCancelled(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventTitle = (String) request.get("eventTitle");
        
        NotificationDTO notification = notificationService.sendEventCancelled(userId, eventId, eventTitle);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : ENVOYER RAPPEL D'ÉVÉNEMENT
     * POST /api/notifications/event-reminder
     * 
     * Body JSON :
     * {
     *   "userId": 1,
     *   "eventId": 5,
     *   "eventTitle": "Conférence Tech 2024",
     *   "timeUntil": "dans 1 heure"
     * }
     */
    @PostMapping("/event-reminder")
    public ResponseEntity<NotificationDTO> sendEventReminder(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long eventId = ((Number) request.get("eventId")).longValue();
        String eventTitle = (String) request.get("eventTitle");
        String timeUntil = (String) request.get("timeUntil");
        
        NotificationDTO notification = notificationService.sendEventReminder(userId, eventId, eventTitle, timeUntil);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
    
    // ==========================================
    // ENDPOINTS DE CONSULTATION
    // ==========================================
    
    /**
     * ENDPOINT : OBTENIR TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
     * GET /api/notifications/user/{userId}
     * 
     * Retourne toutes les notifications (lues et non lues)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * ENDPOINT : OBTENIR LES NOTIFICATIONS NON LUES
     * GET /api/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * ENDPOINT : COMPTER LES NOTIFICATIONS NON LUES
     * GET /api/notifications/user/{userId}/unread-count
     * 
     * Utile pour afficher un badge avec le nombre de notifications
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@PathVariable Long userId) {
        Long count = notificationService.countUnreadNotifications(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT : OBTENIR LES NOTIFICATIONS RÉCENTES (24h)
     * GET /api/notifications/user/{userId}/recent
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<NotificationDTO>> getRecentNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getRecentNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * ENDPOINT : OBTENIR UNE NOTIFICATION PAR ID
     * GET /api/notifications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
    
    /**
     * ENDPOINT : OBTENIR LES NOTIFICATIONS D'UN ÉVÉNEMENT
     * GET /api/notifications/event/{eventId}
     * 
     * Utile pour voir toutes les notifications envoyées pour un événement
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<NotificationDTO>> getEventNotifications(@PathVariable Long eventId) {
        List<NotificationDTO> notifications = notificationService.getEventNotifications(eventId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * ENDPOINT : OBTENIR LES NOTIFICATIONS PAR TYPE
     * GET /api/notifications/type/{type}
     * 
     * Exemple : GET /api/notifications/type/REGISTRATION_CONFIRMATION
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByType(@PathVariable NotificationType type) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }
    
    // ==========================================
    // ENDPOINTS DE GESTION
    // ==========================================
    
    /**
     * ENDPOINT : MARQUER UNE NOTIFICATION COMME LUE
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        NotificationDTO notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }
    
    /**
     * ENDPOINT : MARQUER TOUTES LES NOTIFICATIONS COMME LUES
     * PUT /api/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Toutes les notifications ont été marquées comme lues");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT : SUPPRIMER UNE NOTIFICATION
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification supprimée avec succès");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT : SUPPRIMER TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
     * DELETE /api/notifications/user/{userId}
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, String>> deleteAllUserNotifications(@PathVariable Long userId) {
        notificationService.deleteAllUserNotifications(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Toutes les notifications ont été supprimées");
        
        return ResponseEntity.ok(response);
    }
    
    // ==========================================
    // ENDPOINT DE TEST
    // ==========================================
    
    /**
     * ENDPOINT DE TEST
     * GET /api/notifications/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Notification Service fonctionne correctement ! ✅");
    }
}