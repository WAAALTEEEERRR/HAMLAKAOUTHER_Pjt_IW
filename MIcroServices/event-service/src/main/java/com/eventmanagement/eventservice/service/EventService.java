package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.dto.EventDTO;
import com.eventmanagement.eventservice.dto.EventSearchCriteria;
import com.eventmanagement.eventservice.enums.EventStatus;

import java.util.List;

/**
 * INTERFACE DU SERVICE EVENT
 * Définit les opérations métier pour les événements
 */
public interface EventService {
    
    // CRUD de base
    EventDTO createEvent(EventDTO eventDTO);
    EventDTO getEventById(Long id);
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    void deleteEvent(Long id);
    List<EventDTO> getAllEvents();
    
    // Recherche et filtrage
    List<EventDTO> searchEvents(EventSearchCriteria criteria);
    List<EventDTO> getEventsByOrganizer(Long organizerId);
    List<EventDTO> getEventsByStatus(EventStatus status);
    List<EventDTO> getEventsByCategory(String category);
    List<EventDTO> getAvailableEvents();
    
    // Gestion du statut
    EventDTO publishEvent(Long id);
    EventDTO cancelEvent(Long id);
    EventDTO completeEvent(Long id);
    
    // Gestion des participants (incrémentation/décrémentation)
    EventDTO incrementParticipants(Long id);
    EventDTO decrementParticipants(Long id);
}