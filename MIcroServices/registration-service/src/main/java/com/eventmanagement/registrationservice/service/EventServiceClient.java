package com.eventmanagement.registrationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * CLIENT POUR COMMUNIQUER AVEC EVENT SERVICE - VERSION POST (SANS PATCH)
 * 
 * Ce service permet au Registration Service de communiquer avec l'Event Service
 * IMPORTANT : Utilise POST au lieu de PATCH pour éviter les problèmes de compatibilité
 */
@Service
public class EventServiceClient {
    
    @Value("${event.service.url}")
    private String eventServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public EventServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * RÉCUPÉRER UN ÉVÉNEMENT PAR ID
     * GET http://localhost:8082/api/events/{id}
     */
    public Map<String, Object> getEventById(Long eventId) {
        try {
            String url = eventServiceUrl + "/" + eventId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new RuntimeException("Impossible de récupérer l'événement avec l'ID : " + eventId);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la communication avec Event Service : " + e.getMessage());
        }
    }
    
    /**
     * INCRÉMENTER LE NOMBRE DE PARTICIPANTS - VERSION POST
     * POST http://localhost:8082/api/events/{id}/participants/increment
     */
    public void incrementParticipants(Long eventId) {
        try {
            String url = eventServiceUrl + "/" + eventId + "/participants/increment";
            
            // Utiliser POST au lieu de PATCH
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de l'incrémentation des participants");
            }
            
            System.out.println("✅ Participants incrémentés pour l'événement " + eventId);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'incrémentation des participants : " + e.getMessage());
        }
    }
    
    /**
     * DÉCRÉMENTER LE NOMBRE DE PARTICIPANTS - VERSION POST
     * POST http://localhost:8082/api/events/{id}/participants/decrement
     */
    public void decrementParticipants(Long eventId) {
        try {
            String url = eventServiceUrl + "/" + eventId + "/participants/decrement";
            
            // Utiliser POST au lieu de PATCH
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Échec de la décrémentation des participants");
            }
            
            System.out.println("✅ Participants décrémentés pour l'événement " + eventId);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la décrémentation des participants : " + e.getMessage());
        }
    }
    
    /**
     * VÉRIFIER SI UN ÉVÉNEMENT PEUT ACCEPTER DES PARTICIPANTS
     */
    public boolean canAcceptParticipant(Map<String, Object> event) {
        String status = (String) event.get("status");
        Boolean isAvailable = (Boolean) event.get("isAvailable");
        
        return "PUBLISHED".equals(status) && Boolean.TRUE.equals(isAvailable);
    }
    
    /**
     * EXTRAIRE LES INFORMATIONS D'UN ÉVÉNEMENT
     */
    public String getEventTitle(Map<String, Object> event) {
        return (String) event.get("title");
    }
    
    public String getEventLocation(Map<String, Object> event) {
        return (String) event.get("location");
    }
    
    public String getEventDate(Map<String, Object> event) {
        return (String) event.get("eventDate");
    }
}