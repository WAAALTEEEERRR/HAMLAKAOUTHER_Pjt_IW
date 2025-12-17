package com.eventmanagement.registrationservice.controller;

import com.eventmanagement.registrationservice.dto.RegistrationDTO;
import com.eventmanagement.registrationservice.dto.RegistrationRequest;
import com.eventmanagement.registrationservice.enums.RegistrationStatus;
import com.eventmanagement.registrationservice.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER INSCRIPTIONS (API REST)
 * Expose tous les endpoints pour gérer les inscriptions
 * Base URL : /api/registrations
 */
@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    // ==========================================
    // ENDPOINTS PRINCIPAUX
    // ==========================================
    
    /**
     * ENDPOINT : S'INSCRIRE À UN ÉVÉNEMENT
     * POST /api/registrations
     * 
     * Body JSON :
     * {
     *   "userId": 1,
     *   "eventId": 5,
     *   "notes": "J'ai hâte d'y participer !"
     * }
     */
    @PostMapping
    public ResponseEntity<RegistrationDTO> register(@Valid @RequestBody RegistrationRequest request) {
        RegistrationDTO registration = registrationService.register(request);
        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : SE DÉSINSCRIRE D'UN ÉVÉNEMENT
     * DELETE /api/registrations/{id}
     * 
     * Exemple : DELETE /api/registrations/10
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> unregister(@PathVariable Long id) {
        registrationService.unregister(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Désinscription réussie");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT : OBTENIR UNE INSCRIPTION PAR ID
     * GET /api/registrations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(@PathVariable Long id) {
        RegistrationDTO registration = registrationService.getRegistrationById(id);
        return ResponseEntity.ok(registration);
    }
    
    /**
     * ENDPOINT : OBTENIR TOUTES LES INSCRIPTIONS
     * GET /api/registrations
     * (Utilisé principalement par les administrateurs)
     */
    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
        List<RegistrationDTO> registrations = registrationService.getAllRegistrations();
        return ResponseEntity.ok(registrations);
    }
    
    // ==========================================
    // ENDPOINTS POUR LES PARTICIPANTS
    // ==========================================
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS D'UN UTILISATEUR
     * GET /api/registrations/user/{userId}
     * 
     * Affiche tous les événements auxquels l'utilisateur est inscrit
     * Exemple : GET /api/registrations/user/1
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RegistrationDTO>> getUserRegistrations(@PathVariable Long userId) {
        List<RegistrationDTO> registrations = registrationService.getUserRegistrations(userId);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS FUTURS D'UN UTILISATEUR
     * GET /api/registrations/user/{userId}/upcoming
     * 
     * Affiche uniquement les événements futurs (non passés)
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<RegistrationDTO>> getUserUpcomingEvents(@PathVariable Long userId) {
        List<RegistrationDTO> registrations = registrationService.getUserUpcomingEvents(userId);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS PASSÉS D'UN UTILISATEUR
     * GET /api/registrations/user/{userId}/past
     * 
     * Affiche uniquement les événements passés (historique)
     */
    @GetMapping("/user/{userId}/past")
    public ResponseEntity<List<RegistrationDTO>> getUserPastEvents(@PathVariable Long userId) {
        List<RegistrationDTO> registrations = registrationService.getUserPastEvents(userId);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * ENDPOINT : VÉRIFIER SI UN UTILISATEUR EST INSCRIT À UN ÉVÉNEMENT
     * GET /api/registrations/check?userId=1&eventId=5
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkUserRegistration(
            @RequestParam Long userId,
            @RequestParam Long eventId
    ) {
        boolean isRegistered = registrationService.isUserRegistered(userId, eventId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isRegistered", isRegistered);
        
        return ResponseEntity.ok(response);
    }
    
    // ==========================================
    // ENDPOINTS POUR LES ORGANISATEURS
    // ==========================================
    
    /**
     * ENDPOINT : OBTENIR LA LISTE DES PARTICIPANTS D'UN ÉVÉNEMENT
     * GET /api/registrations/event/{eventId}
     * 
     * Affiche tous les participants inscrits à un événement
     * Exemple : GET /api/registrations/event/5
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RegistrationDTO>> getEventRegistrations(@PathVariable Long eventId) {
        List<RegistrationDTO> registrations = registrationService.getEventRegistrations(eventId);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * ENDPOINT : OBTENIR LES PARTICIPANTS CONFIRMÉS D'UN ÉVÉNEMENT
     * GET /api/registrations/event/{eventId}/confirmed
     * 
     * Affiche uniquement les participants avec statut CONFIRMED
     */
    @GetMapping("/event/{eventId}/confirmed")
    public ResponseEntity<List<RegistrationDTO>> getConfirmedParticipants(@PathVariable Long eventId) {
        List<RegistrationDTO> registrations = registrationService.getConfirmedParticipants(eventId);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * ENDPOINT : COMPTER LES INSCRIPTIONS ACTIVES D'UN ÉVÉNEMENT
     * GET /api/registrations/event/{eventId}/count
     * 
     * Retourne le nombre de participants actifs (PENDING + CONFIRMED)
     */
    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<Map<String, Long>> countActiveRegistrations(@PathVariable Long eventId) {
        Long count = registrationService.countActiveRegistrations(eventId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
    
    // ==========================================
    // GESTION DES STATUTS
    // ==========================================
    
    /**
     * ENDPOINT : CHANGER LE STATUT D'UNE INSCRIPTION
     * PUT /api/registrations/{id}/status
     * 
     * Body JSON :
     * {
     *   "status": "CONFIRMED"
     * }
     * 
     * Statuts possibles : PENDING, CONFIRMED, CANCELLED, ATTENDED, NO_SHOW
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RegistrationDTO> updateRegistrationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String statusStr = request.get("status");
        
        if (statusStr == null || statusStr.isEmpty()) {
            throw new RuntimeException("Le statut est obligatoire");
        }
        
        RegistrationStatus newStatus;
        try {
            newStatus = RegistrationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide. Valeurs possibles : PENDING, CONFIRMED, CANCELLED, ATTENDED, NO_SHOW");
        }
        
        RegistrationDTO registration = registrationService.updateRegistrationStatus(id, newStatus);
        return ResponseEntity.ok(registration);
    }
    
    /**
     * ENDPOINT : OBTENIR LES INSCRIPTIONS PAR STATUT
     * GET /api/registrations/status/{status}
     * 
     * Exemple : GET /api/registrations/status/PENDING
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByStatus(@PathVariable RegistrationStatus status) {
        List<RegistrationDTO> registrations = registrationService.getRegistrationsByStatus(status);
        return ResponseEntity.ok(registrations);
    }
    
    // ==========================================
    // ENDPOINT DE TEST
    // ==========================================
    
    /**
     * ENDPOINT DE TEST
     * GET /api/registrations/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Registration Service fonctionne correctement ! ✅");
    }
}