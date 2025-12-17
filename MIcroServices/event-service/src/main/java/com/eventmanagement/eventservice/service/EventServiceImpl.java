package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.dto.EventDTO;
import com.eventmanagement.eventservice.dto.EventFileDTO;
import com.eventmanagement.eventservice.dto.EventSearchCriteria;
import com.eventmanagement.eventservice.enums.EventStatus;
import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.eventservice.model.EventFile;
import com.eventmanagement.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IMPLÉMENTATION DU SERVICE EVENT
 * Contient toute la logique métier pour les événements
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    /**
     * CRÉER UN ÉVÉNEMENT
     */
    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        // Validation : la date de l'événement doit être dans le futur
        if (eventDTO.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date de l'événement doit être dans le futur");
        }
        
        // Validation : si payant, le prix doit être > 0
        if (!eventDTO.getIsFree() && (eventDTO.getPrice() == null || eventDTO.getPrice() <= 0)) {
            throw new RuntimeException("Le prix doit être supérieur à 0 pour un événement payant");
        }
        
        Event event = convertToEntity(eventDTO);
        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }
    
    /**
     * OBTENIR UN ÉVÉNEMENT PAR ID
     */
    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID : " + id));
        return convertToDTO(event);
    }
    
    /**
     * METTRE À JOUR UN ÉVÉNEMENT
     */
    @Override
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID : " + id));
        
        // Mise à jour des champs
        existingEvent.setTitle(eventDTO.getTitle());
        existingEvent.setDescription(eventDTO.getDescription());
        existingEvent.setLocation(eventDTO.getLocation());
        existingEvent.setEventDate(eventDTO.getEventDate());
        existingEvent.setEndDate(eventDTO.getEndDate());
        existingEvent.setCapacity(eventDTO.getCapacity());
        existingEvent.setCategory(eventDTO.getCategory());
        existingEvent.setIsFree(eventDTO.getIsFree());
        existingEvent.setPrice(eventDTO.getPrice());
        existingEvent.setStatus(eventDTO.getStatus());
        
        Event updatedEvent = eventRepository.save(existingEvent);
        return convertToDTO(updatedEvent);
    }
    
    /**
     * SUPPRIMER UN ÉVÉNEMENT
     */
    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Événement non trouvé avec l'ID : " + id);
        }
        eventRepository.deleteById(id);
    }
    
    /**
     * OBTENIR TOUS LES ÉVÉNEMENTS
     */
    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * RECHERCHE MULTI-CRITÈRES
     */
    @Override
    public List<EventDTO> searchEvents(EventSearchCriteria criteria) {
        List<Event> events = eventRepository.searchEvents(
            criteria.getKeyword(),
            criteria.getLocation(),
            criteria.getCategory(),
            criteria.getStatus(),
            criteria.getOrganizerId(),
            criteria.getIsFree(),
            criteria.getStartDate(),
            criteria.getEndDate()
        );
        
        // Filtrer par places disponibles si demandé
        if (criteria.getOnlyAvailable() != null && criteria.getOnlyAvailable()) {
            events = events.stream()
                    .filter(Event::canAcceptParticipant)
                    .collect(Collectors.toList());
        }
        
        // Filtrer par prix maximum si spécifié
        if (criteria.getMaxPrice() != null) {
            events = events.stream()
                    .filter(e -> e.getIsFree() || (e.getPrice() != null && e.getPrice() <= criteria.getMaxPrice()))
                    .collect(Collectors.toList());
        }
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES ÉVÉNEMENTS D'UN ORGANISATEUR
     */
    @Override
    public List<EventDTO> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES ÉVÉNEMENTS PAR STATUT
     */
    @Override
    public List<EventDTO> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES ÉVÉNEMENTS PAR CATÉGORIE
     */
    @Override
    public List<EventDTO> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES ÉVÉNEMENTS DISPONIBLES (avec places)
     */
    @Override
    public List<EventDTO> getAvailableEvents() {
        return eventRepository.findAvailableEvents().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * PUBLIER UN ÉVÉNEMENT
     */
    @Override
    public EventDTO publishEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de publier un événement passé");
        }
        
        event.setStatus(EventStatus.PUBLISHED);
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    /**
     * ANNULER UN ÉVÉNEMENT
     */
    @Override
    public EventDTO cancelEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        
        event.setStatus(EventStatus.CANCELLED);
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    /**
     * MARQUER UN ÉVÉNEMENT COMME TERMINÉ
     */
    @Override
    public EventDTO completeEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        
        event.setStatus(EventStatus.COMPLETED);
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    /**
     * INCRÉMENTER LE NOMBRE DE PARTICIPANTS
     * (Appelé quand quelqu'un s'inscrit)
     */
    @Override
    public EventDTO incrementParticipants(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        
        if (event.isFull()) {
            throw new RuntimeException("L'événement est complet");
        }
        
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    /**
     * DÉCRÉMENTER LE NOMBRE DE PARTICIPANTS
     * (Appelé quand quelqu'un se désinscrit)
     */
    @Override
    public EventDTO decrementParticipants(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        
        if (event.getCurrentParticipants() > 0) {
            event.setCurrentParticipants(event.getCurrentParticipants() - 1);
        }
        
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    // ==========================================
    // MÉTHODES UTILITAIRES (Conversions)
    // ==========================================
    
    /**
     * Convertir Entity → DTO
     */
    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setEventDate(event.getEventDate());
        dto.setEndDate(event.getEndDate());
        dto.setCapacity(event.getCapacity());
        dto.setCurrentParticipants(event.getCurrentParticipants());
        dto.setStatus(event.getStatus());
        dto.setOrganizerId(event.getOrganizerId());
        dto.setOrganizerName(event.getOrganizerName());
        dto.setCategory(event.getCategory());
        dto.setIsFree(event.getIsFree());
        dto.setPrice(event.getPrice());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        
        // Calcul des champs dérivés
        dto.setIsAvailable(!event.isFull() && event.getStatus() == EventStatus.PUBLISHED);
        dto.setAvailableSeats(event.getCapacity() - event.getCurrentParticipants());
        
        // Conversion des fichiers
        if (event.getFiles() != null) {
            dto.setFiles(event.getFiles().stream()
                    .map(this::convertFileToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convertir DTO → Entity
     */
    private Event convertToEntity(EventDTO dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setEndDate(dto.getEndDate());
        event.setCapacity(dto.getCapacity());
        event.setCurrentParticipants(dto.getCurrentParticipants() != null ? dto.getCurrentParticipants() : 0);
        event.setStatus(dto.getStatus() != null ? dto.getStatus() : EventStatus.DRAFT);
        event.setOrganizerId(dto.getOrganizerId());
        event.setOrganizerName(dto.getOrganizerName());
        event.setCategory(dto.getCategory());
        event.setIsFree(dto.getIsFree() != null ? dto.getIsFree() : true);
        event.setPrice(dto.getPrice());
        return event;
    }
    
    /**
     * Convertir EventFile → EventFileDTO
     */
    private EventFileDTO convertFileToDTO(EventFile file) {
        EventFileDTO dto = new EventFileDTO();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFilePath(file.getFilePath());
        dto.setFileType(file.getFileType());
        dto.setContentType(file.getContentType());
        dto.setFileSize(file.getFileSize());
        dto.setDescription(file.getDescription());
        dto.setEventId(file.getEvent().getId());
        dto.setUploadedAt(file.getUploadedAt());
        dto.setDownloadUrl("/api/events/" + file.getEvent().getId() + "/files/" + file.getId());
        return dto;
    }
}