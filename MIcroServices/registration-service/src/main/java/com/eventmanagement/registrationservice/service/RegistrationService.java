package com.eventmanagement.registrationservice.service;

import com.eventmanagement.registrationservice.dto.RegistrationDTO;
import com.eventmanagement.registrationservice.dto.RegistrationRequest;
import com.eventmanagement.registrationservice.enums.RegistrationStatus;

import java.util.List;

/**
 * INTERFACE DU SERVICE REGISTRATION
 * Définit les opérations métier pour les inscriptions
 */
public interface RegistrationService {
    
    /**
     * S'INSCRIRE À UN ÉVÉNEMENT
     * 1. Vérifie que l'utilisateur n'est pas déjà inscrit
     * 2. Vérifie que l'événement existe et accepte des participants
     * 3. Crée l'inscription
     * 4. Incrémente le compteur de participants dans Event Service
     */
    RegistrationDTO register(RegistrationRequest request);
    
    /**
     * SE DÉSINSCRIRE D'UN ÉVÉNEMENT
     * 1. Vérifie que l'inscription existe
     * 2. Change le statut à CANCELLED
     * 3. Décrémente le compteur de participants dans Event Service
     */
    void unregister(Long registrationId);
    
    /**
     * OBTENIR UNE INSCRIPTION PAR ID
     */
    RegistrationDTO getRegistrationById(Long id);
    
    /**
     * OBTENIR TOUTES LES INSCRIPTIONS
     */
    List<RegistrationDTO> getAllRegistrations();
    
    /**
     * OBTENIR LES INSCRIPTIONS D'UN UTILISATEUR
     * (Pour afficher "Mes événements" côté participant)
     */
    List<RegistrationDTO> getUserRegistrations(Long userId);
    
    /**
     * OBTENIR LES INSCRIPTIONS D'UN ÉVÉNEMENT
     * (Pour afficher la liste des participants côté organisateur)
     */
    List<RegistrationDTO> getEventRegistrations(Long eventId);
    
    /**
     * OBTENIR LES ÉVÉNEMENTS FUTURS D'UN UTILISATEUR
     */
    List<RegistrationDTO> getUserUpcomingEvents(Long userId);
    
    /**
     * OBTENIR LES ÉVÉNEMENTS PASSÉS D'UN UTILISATEUR
     */
    List<RegistrationDTO> getUserPastEvents(Long userId);
    
    /**
     * CHANGER LE STATUT D'UNE INSCRIPTION
     * Exemples : PENDING → CONFIRMED, CONFIRMED → ATTENDED, etc.
     */
    RegistrationDTO updateRegistrationStatus(Long registrationId, RegistrationStatus newStatus);
    
    /**
     * OBTENIR LES INSCRIPTIONS PAR STATUT
     */
    List<RegistrationDTO> getRegistrationsByStatus(RegistrationStatus status);
    
    /**
     * OBTENIR LES PARTICIPANTS CONFIRMÉS D'UN ÉVÉNEMENT
     */
    List<RegistrationDTO> getConfirmedParticipants(Long eventId);
    
    /**
     * COMPTER LES INSCRIPTIONS ACTIVES D'UN ÉVÉNEMENT
     */
    Long countActiveRegistrations(Long eventId);
    
    /**
     * VÉRIFIER SI UN UTILISATEUR EST INSCRIT À UN ÉVÉNEMENT
     */
    boolean isUserRegistered(Long userId, Long eventId);
}