package com.eventmanagement.registrationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * CLIENT POUR COMMUNIQUER AVEC NOTIFICATION SERVICE
 * Permet d'envoyer des notifications aux utilisateurs
 * 
 * UTILISÉ PAR : RegistrationServiceImpl
 * APPELLE : http://localhost:8084/api/notifications/...
 */
@Service
public class NotificationServiceClient {
    
    @Value("${notification.service.url:http://localhost:8084/api/notifications}")
    private String notificationServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public NotificationServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * ENVOYER UNE CONFIRMATION D'INSCRIPTION
     * Appelé quand un utilisateur s'inscrit à un événement
     */
    public void sendRegistrationConfirmation(Long userId, Long eventId, String eventTitle) {
        try {
            String url = notificationServiceUrl + "/registration-confirmation";
            
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("eventId", eventId);
            request.put("eventTitle", eventTitle);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            restTemplate.postForObject(url, entity, Map.class);
            
            System.out.println("✅ Notification de confirmation envoyée à l'utilisateur " + userId);
            
        } catch (Exception e) {
            // Logger l'erreur mais ne pas bloquer l'inscription
            System.err.println("⚠️ Erreur lors de l'envoi de la notification : " + e.getMessage());
        }
    }
    
    /**
     * ENVOYER UNE NOTIFICATION D'ANNULATION
     * Appelé quand un utilisateur se désinscrit d'un événement
     */
    public void sendRegistrationCancelled(Long userId, Long eventId, String eventTitle) {
        try {
            String url = notificationServiceUrl + "/registration-cancelled";
            
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            request.put("eventId", eventId);
            request.put("eventTitle", eventTitle);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            restTemplate.postForObject(url, entity, Map.class);
            
            System.out.println("✅ Notification d'annulation envoyée à l'utilisateur " + userId);
            
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de l'envoi de la notification : " + e.getMessage());
        }
    }
}