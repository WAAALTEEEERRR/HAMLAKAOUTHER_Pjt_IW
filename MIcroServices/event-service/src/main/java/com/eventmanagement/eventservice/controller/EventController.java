package com.eventmanagement.eventservice.controller;

import com.eventmanagement.eventservice.dto.EventDTO;
import com.eventmanagement.eventservice.dto.EventFileDTO;
import com.eventmanagement.eventservice.dto.EventSearchCriteria;
import com.eventmanagement.eventservice.enums.EventStatus;
import com.eventmanagement.eventservice.enums.FileType;
import com.eventmanagement.eventservice.service.EventService;
import com.eventmanagement.eventservice.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CONTROLLER ÉVÉNEMENTS (API REST)
 * Expose tous les endpoints pour gérer les événements
 * Base URL : /api/events
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // ==========================================
    // ENDPOINTS CRUD DE BASE
    // ==========================================
    
    /**
     * ENDPOINT : CRÉER UN ÉVÉNEMENT
     * POST /api/events
     */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : OBTENIR TOUS LES ÉVÉNEMENTS
     * GET /api/events
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * ENDPOINT : OBTENIR UN ÉVÉNEMENT PAR ID
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    /**
     * ENDPOINT : METTRE À JOUR UN ÉVÉNEMENT
     * PUT /api/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, 
                                                @Valid @RequestBody EventDTO eventDTO) {
        EventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }
    
    /**
     * ENDPOINT : SUPPRIMER UN ÉVÉNEMENT
     * DELETE /api/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==========================================
    // ENDPOINTS DE RECHERCHE ET FILTRAGE
    // ==========================================
    
    /**
     * ENDPOINT : RECHERCHE MULTI-CRITÈRES
     * GET /api/events/search?keyword=...&location=...&date=...
     * Exemples :
     * - GET /api/events/search?keyword=conférence
     * - GET /api/events/search?location=Paris&category=Formation
     * - GET /api/events/search?startDate=2024-01-01T00:00:00&onlyAvailable=true
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) Long organizerId,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean onlyAvailable
    ) {
        EventSearchCriteria criteria = new EventSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setLocation(location);
        criteria.setCategory(category);
        criteria.setStatus(status);
        criteria.setOrganizerId(organizerId);
        criteria.setIsFree(isFree);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);
        criteria.setMaxPrice(maxPrice);
        criteria.setOnlyAvailable(onlyAvailable);
        
        List<EventDTO> events = eventService.searchEvents(criteria);
        return ResponseEntity.ok(events);
    }
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS D'UN ORGANISATEUR
     * GET /api/events/organizer/{organizerId}
     */
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventDTO>> getEventsByOrganizer(@PathVariable Long organizerId) {
        List<EventDTO> events = eventService.getEventsByOrganizer(organizerId);
        return ResponseEntity.ok(events);
    }
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS PAR STATUT
     * GET /api/events/status/{status}
     * Exemple : GET /api/events/status/PUBLISHED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventDTO>> getEventsByStatus(@PathVariable EventStatus status) {
        List<EventDTO> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS PAR CATÉGORIE
     * GET /api/events/category/{category}
     * Exemple : GET /api/events/category/Conférence
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(@PathVariable String category) {
        List<EventDTO> events = eventService.getEventsByCategory(category);
        return ResponseEntity.ok(events);
    }
    
    /**
     * ENDPOINT : OBTENIR LES ÉVÉNEMENTS DISPONIBLES
     * GET /api/events/available
     * (Événements publiés avec places disponibles)
     */
    @GetMapping("/available")
    public ResponseEntity<List<EventDTO>> getAvailableEvents() {
        List<EventDTO> events = eventService.getAvailableEvents();
        return ResponseEntity.ok(events);
    }
    
    // ==========================================
    // ENDPOINTS DE GESTION DU STATUT
    // ==========================================
    
    /**
     * ENDPOINT : PUBLIER UN ÉVÉNEMENT
     * POST /api/events/{id}/publish
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<EventDTO> publishEvent(@PathVariable Long id) {
        EventDTO event = eventService.publishEvent(id);
        return ResponseEntity.ok(event);
    }
    
    /**
     * ENDPOINT : ANNULER UN ÉVÉNEMENT
     * POST /api/events/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventDTO> cancelEvent(@PathVariable Long id) {
        EventDTO event = eventService.cancelEvent(id);
        return ResponseEntity.ok(event);
    }
    
    /**
     * ENDPOINT : MARQUER UN ÉVÉNEMENT COMME TERMINÉ
     * POST /api/events/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<EventDTO> completeEvent(@PathVariable Long id) {
        EventDTO event = eventService.completeEvent(id);
        return ResponseEntity.ok(event);
    }
    
    // ==========================================
    // ENDPOINTS DE GESTION DES PARTICIPANTS
    // ==========================================
    
    /**
     * ENDPOINT : INCRÉMENTER LES PARTICIPANTS
     * POST /api/events/{id}/participants/increment
     * (Appelé par le Registration Service lors d'une inscription)
     */
    @PostMapping("/{id}/participants/increment")
    public ResponseEntity<EventDTO> incrementParticipants(@PathVariable Long id) {
        EventDTO event = eventService.incrementParticipants(id);
        return ResponseEntity.ok(event);
    }
    
    /**
     * ENDPOINT : DÉCRÉMENTER LES PARTICIPANTS
     * POST /api/events/{id}/participants/decrement
     * (Appelé par le Registration Service lors d'une désinscription)
     */
    @PostMapping("/{id}/participants/decrement")
    public ResponseEntity<EventDTO> decrementParticipants(@PathVariable Long id) {
        EventDTO event = eventService.decrementParticipants(id);
        return ResponseEntity.ok(event);
    }
    
    // ==========================================
    // ENDPOINTS DE GESTION DES FICHIERS
    // ==========================================
    
    /**
     * ENDPOINT : UPLOADER UN FICHIER
     * POST /api/events/{id}/upload
     * Paramètres :
     * - file : le fichier à uploader
     * - fileType : IMAGE, VIDEO, ou DOCUMENT
     * - description : description optionnelle
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<EventFileDTO> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") FileType fileType,
            @RequestParam(value = "description", required = false) String description
    ) {
        EventFileDTO uploadedFile = fileStorageService.uploadFile(id, file, fileType, description);
        return new ResponseEntity<>(uploadedFile, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : UPLOADER PLUSIEURS FICHIERS
     * POST /api/events/{id}/upload-multiple
     */
    @PostMapping("/{id}/upload-multiple")
    public ResponseEntity<List<EventFileDTO>> uploadMultipleFiles(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("fileType") FileType fileType
    ) {
        List<EventFileDTO> uploadedFiles = fileStorageService.uploadFiles(id, files, fileType);
        return new ResponseEntity<>(uploadedFiles, HttpStatus.CREATED);
    }
    
    /**
     * ENDPOINT : OBTENIR TOUS LES FICHIERS D'UN ÉVÉNEMENT
     * GET /api/events/{id}/files
     */
    @GetMapping("/{id}/files")
    public ResponseEntity<List<EventFileDTO>> getEventFiles(@PathVariable Long id) {
        List<EventFileDTO> files = fileStorageService.getEventFiles(id);
        return ResponseEntity.ok(files);
    }
    
    /**
     * ENDPOINT : OBTENIR LES FICHIERS PAR TYPE
     * GET /api/events/{id}/files/{fileType}
     * Exemple : GET /api/events/1/files/IMAGE
     */
    @GetMapping("/{id}/files/type/{fileType}")
    public ResponseEntity<List<EventFileDTO>> getEventFilesByType(
            @PathVariable Long id,
            @PathVariable FileType fileType
    ) {
        List<EventFileDTO> files = fileStorageService.getEventFilesByType(id, fileType);
        return ResponseEntity.ok(files);
    }
    
    /**
     * ENDPOINT : TÉLÉCHARGER UN FICHIER
     * GET /api/events/{eventId}/files/{fileId}
     */
    @GetMapping("/{eventId}/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long eventId,
            @PathVariable Long fileId
    ) {
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        
        // Déterminer le type de contenu
        String contentType = "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    /**
     * ENDPOINT : SUPPRIMER UN FICHIER
     * DELETE /api/events/{eventId}/files/{fileId}
     */
    @DeleteMapping("/{eventId}/files/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long eventId,
            @PathVariable Long fileId
    ) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
    
    // ==========================================
    // ENDPOINT DE TEST
    // ==========================================
    
    /**
     * ENDPOINT DE TEST
     * GET /api/events/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Event Service fonctionne correctement ! ✅");
    }
}