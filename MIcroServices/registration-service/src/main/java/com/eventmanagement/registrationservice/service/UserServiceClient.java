package com.eventmanagement.registrationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * CLIENT POUR COMMUNIQUER AVEC USER SERVICE
 * Permet de récupérer les informations des utilisateurs
 * 
 * UTILISÉ PAR : RegistrationServiceImpl
 * APPELLE : http://localhost:8081/api/users/{id}
 */
@Service
public class UserServiceClient {
    
    @Value("${user.service.url}")
    private String userServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public UserServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * RÉCUPÉRER UN UTILISATEUR PAR ID
     * GET http://localhost:8081/api/users/{id}
     */
    public Map<String, Object> getUserById(Long userId) {
        try {
            String url = userServiceUrl + "/" + userId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new RuntimeException("Impossible de récupérer l'utilisateur avec l'ID : " + userId);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la communication avec User Service : " + e.getMessage());
        }
    }
    
    /**
     * EXTRAIRE L'EMAIL D'UN UTILISATEUR
     */
    public String getUserEmail(Map<String, Object> user) {
        return (String) user.get("email");
    }
    
    /**
     * EXTRAIRE LE NOM COMPLET D'UN UTILISATEUR
     */
    public String getUserName(Map<String, Object> user) {
        String firstName = (String) user.get("firstName");
        String lastName = (String) user.get("lastName");
        return firstName + " " + lastName;
    }
}