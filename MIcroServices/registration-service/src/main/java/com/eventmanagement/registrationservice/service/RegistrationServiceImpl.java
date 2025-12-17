package com.eventmanagement.registrationservice.service;

import com.eventmanagement.registrationservice.dto.RegistrationDTO;
import com.eventmanagement.registrationservice.dto.RegistrationRequest;
import com.eventmanagement.registrationservice.enums.RegistrationStatus;
import com.eventmanagement.registrationservice.model.Registration;
import com.eventmanagement.registrationservice.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IMPLÉMENTATION DU SERVICE REGISTRATION - VERSION COMPLÈTE
 * Communique avec User Service, Event Service et Notification Service
 */
@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
    
    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private EventServiceClient eventServiceClient;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private NotificationServiceClient notificationServiceClient;
    
    /**
     * S'INSCRIRE À UN ÉVÉNEMENT - VERSION COMPLÈTE AVEC NOTIFICATIONS
     */
    @Override
    public RegistrationDTO register(RegistrationRequest request) {
        System.out.println("🔵 Début de l'inscription : User " + request.getUserId() + " → Event " + request.getEventId());
        
        // 1. Vérifier que l'utilisateur n'est pas déjà inscrit
        if (registrationRepository.existsByUserIdAndEventId(request.getUserId(), request.getEventId())) {
            throw new RuntimeException("Vous êtes déjà inscrit à cet événement");
        }
        
        // 2. Récupérer les informations de l'utilisateur depuis User Service
        System.out.println("📞 Appel User Service pour récupérer l'utilisateur...");
        Map<String, Object> user = userServiceClient.getUserById(request.getUserId());
        
        // 3. Récupérer les informations de l'événement depuis Event Service
        System.out.println("📞 Appel Event Service pour récupérer l'événement...");
        Map<String, Object> event = eventServiceClient.getEventById(request.getEventId());
        
        // 4. Vérifier que l'événement peut accepter des participants
        if (!eventServiceClient.canAcceptParticipant(event)) {
            throw new RuntimeException("Cet événement n'accepte plus d'inscriptions (complet ou non publié)");
        }
        
        // 5. Créer l'inscription
        Registration registration = new Registration();
        registration.setUserId(request.getUserId());
        registration.setEventId(request.getEventId());
        registration.setNotes(request.getNotes());
        
        // Copier les informations de l'utilisateur
        registration.setUserEmail(userServiceClient.getUserEmail(user));
        registration.setUserName(userServiceClient.getUserName(user));
        
        // Copier les informations de l'événement
        registration.setEventTitle(eventServiceClient.getEventTitle(event));
        registration.setEventLocation(eventServiceClient.getEventLocation(event));
        
        String eventDateStr = eventServiceClient.getEventDate(event);
        if (eventDateStr != null) {
            registration.setEventDate(LocalDateTime.parse(eventDateStr));
        }
        
        registration.setStatus(RegistrationStatus.PENDING);
        
        // 6. Sauvegarder l'inscription
        Registration savedRegistration = registrationRepository.save(registration);
        System.out.println("✅ Inscription créée avec ID : " + savedRegistration.getId());
        
        // 7. Incrémenter le compteur de participants dans Event Service
        try {
            System.out.println("📞 Appel Event Service pour incrémenter le compteur...");
            eventServiceClient.incrementParticipants(request.getEventId());
            System.out.println("✅ Compteur incrémenté");
        } catch (Exception e) {
            // Si l'incrémentation échoue, annuler l'inscription
            registrationRepository.delete(savedRegistration);
            throw new RuntimeException("Erreur lors de l'inscription : " + e.getMessage());
        }
        
        // 8. Envoyer une notification de confirmation
        try {
            System.out.println("📞 Appel Notification Service pour envoyer la confirmation...");
            notificationServiceClient.sendRegistrationConfirmation(
                request.getUserId(),
                request.getEventId(),
                eventServiceClient.getEventTitle(event)
            );
        } catch (Exception e) {
            System.err.println("⚠️ Erreur notification : " + e.getMessage());
        }
        
        System.out.println("🎉 Inscription terminée avec succès !");
        return convertToDTO(savedRegistration);
    }
    
    /**
     * SE DÉSINSCRIRE - VERSION COMPLÈTE AVEC NOTIFICATIONS
     */
    @Override
    public void unregister(Long registrationId) {
        System.out.println("🔵 Début de la désinscription : Registration ID " + registrationId);
        
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée avec l'ID : " + registrationId));
        
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new RuntimeException("Cette inscription est déjà annulée");
        }
        
        if (registration.getEventDate() != null && registration.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de se désinscrire d'un événement passé");
        }
        
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        System.out.println("✅ Statut changé à CANCELLED");
        
        // Décrémenter le compteur
        try {
            System.out.println("📞 Appel Event Service pour décrémenter le compteur...");
            eventServiceClient.decrementParticipants(registration.getEventId());
            System.out.println("✅ Compteur décrémenté");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de la décrémentation : " + e.getMessage());
        }
        
        // Envoyer une notification d'annulation
        try {
            System.out.println("📞 Appel Notification Service pour envoyer l'annulation...");
            notificationServiceClient.sendRegistrationCancelled(
                registration.getUserId(),
                registration.getEventId(),
                registration.getEventTitle()
            );
        } catch (Exception e) {
            System.err.println("⚠️ Erreur notification : " + e.getMessage());
        }
        
        System.out.println("🎉 Désinscription terminée avec succès !");
    }
    
    // ==========================================
    // TOUTES LES AUTRES MÉTHODES (INCHANGÉES)
    // ==========================================
    
    @Override
    public RegistrationDTO getRegistrationById(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée avec l'ID : " + id));
        return convertToDTO(registration);
    }
    
    @Override
    public List<RegistrationDTO> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegistrationDTO> getUserRegistrations(Long userId) {
        return registrationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegistrationDTO> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegistrationDTO> getUserUpcomingEvents(Long userId) {
        return registrationRepository.findUpcomingEventsByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegistrationDTO> getUserPastEvents(Long userId) {
        return registrationRepository.findPastEventsByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public RegistrationDTO updateRegistrationStatus(Long registrationId, RegistrationStatus newStatus) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        
        RegistrationStatus oldStatus = registration.getStatus();
        registration.setStatus(newStatus);
        Registration updatedRegistration = registrationRepository.save(registration);
        
        if (oldStatus == RegistrationStatus.CANCELLED && 
            (newStatus == RegistrationStatus.PENDING || newStatus == RegistrationStatus.CONFIRMED)) {
            try {
                eventServiceClient.incrementParticipants(registration.getEventId());
            } catch (Exception e) {
                System.err.println("Erreur lors de l'incrémentation : " + e.getMessage());
            }
        }
        
        if (oldStatus != RegistrationStatus.CANCELLED && newStatus == RegistrationStatus.CANCELLED) {
            try {
                eventServiceClient.decrementParticipants(registration.getEventId());
            } catch (Exception e) {
                System.err.println("Erreur lors de la décrémentation : " + e.getMessage());
            }
        }
        
        return convertToDTO(updatedRegistration);
    }
    
    @Override
    public List<RegistrationDTO> getRegistrationsByStatus(RegistrationStatus status) {
        return registrationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RegistrationDTO> getConfirmedParticipants(Long eventId) {
        return registrationRepository.findByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long countActiveRegistrations(Long eventId) {
        return registrationRepository.countActiveRegistrationsByEventId(eventId);
    }
    
    @Override
    public boolean isUserRegistered(Long userId, Long eventId) {
        return registrationRepository.existsByUserIdAndEventId(userId, eventId);
    }
    
    private RegistrationDTO convertToDTO(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getId());
        dto.setUserId(registration.getUserId());
        dto.setUserEmail(registration.getUserEmail());
        dto.setUserName(registration.getUserName());
        dto.setEventId(registration.getEventId());
        dto.setEventTitle(registration.getEventTitle());
        dto.setEventDate(registration.getEventDate());
        dto.setEventLocation(registration.getEventLocation());
        dto.setStatus(registration.getStatus());
        dto.setRegisteredAt(registration.getRegisteredAt());
        dto.setUpdatedAt(registration.getUpdatedAt());
        dto.setNotes(registration.getNotes());
        return dto;
    }
}