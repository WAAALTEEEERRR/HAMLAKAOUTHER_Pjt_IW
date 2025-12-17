package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.dto.EventFileDTO;
import com.eventmanagement.eventservice.enums.FileType;
import com.eventmanagement.eventservice.model.Event;
import com.eventmanagement.eventservice.model.EventFile;
import com.eventmanagement.eventservice.repository.EventFileRepository;
import com.eventmanagement.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * IMPLÉMENTATION DU SERVICE DE STOCKAGE DES FICHIERS
 */
@Service
@Transactional
public class FileStorageServiceImpl implements FileStorageService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventFileRepository eventFileRepository;
    
    @Value("${file.upload-dir:uploads/events}")
    private String uploadDir;
    
    private Path fileStorageLocation;
    
    /**
     * Initialisation du dossier de stockage
     */
    @Autowired
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage", e);
        }
    }
    
    /**
     * UPLOADER UN FICHIER
     */
    @Override
    public EventFileDTO uploadFile(Long eventId, MultipartFile file, FileType fileType, String description) {
        // Vérifier que l'événement existe
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        
        // Valider le fichier
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }
        
        // Générer un nom de fichier unique
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // Vérifier que le nom du fichier est valide
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Le nom du fichier contient une séquence de chemin invalide");
            }
            
            // Créer un sous-dossier pour l'événement
            Path eventFolder = this.fileStorageLocation.resolve("event_" + eventId);
            Files.createDirectories(eventFolder);
            
            // Copier le fichier dans le dossier de destination
            Path targetLocation = eventFolder.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Créer l'entité EventFile
            EventFile eventFile = new EventFile();
            eventFile.setFileName(originalFilename);
            eventFile.setFilePath(targetLocation.toString());
            eventFile.setFileType(fileType);
            eventFile.setContentType(file.getContentType());
            eventFile.setFileSize(file.getSize());
            eventFile.setDescription(description);
            eventFile.setEvent(event);
            
            EventFile savedFile = eventFileRepository.save(eventFile);
            
            return convertToDTO(savedFile);
            
        } catch (IOException e) {
            throw new RuntimeException("Impossible de stocker le fichier " + originalFilename, e);
        }
    }
    
    /**
     * UPLOADER PLUSIEURS FICHIERS
     */
    @Override
    public List<EventFileDTO> uploadFiles(Long eventId, List<MultipartFile> files, FileType fileType) {
        return files.stream()
                .map(file -> uploadFile(eventId, file, fileType, null))
                .collect(Collectors.toList());
    }
    
    /**
     * CHARGER UN FICHIER POUR LE TÉLÉCHARGEMENT
     */
    @Override
    public Resource loadFileAsResource(Long fileId) {
        EventFile eventFile = eventFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
        
        try {
            Path filePath = Paths.get(eventFile.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier non trouvé ou non lisible : " + eventFile.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fichier non trouvé : " + eventFile.getFileName(), e);
        }
    }
    
    /**
     * OBTENIR TOUS LES FICHIERS D'UN ÉVÉNEMENT
     */
    @Override
    public List<EventFileDTO> getEventFiles(Long eventId) {
        return eventFileRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES FICHIERS D'UN ÉVÉNEMENT PAR TYPE
     */
    @Override
    public List<EventFileDTO> getEventFilesByType(Long eventId, FileType fileType) {
        return eventFileRepository.findByEventIdAndFileType(eventId, fileType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * SUPPRIMER UN FICHIER
     */
    @Override
    public void deleteFile(Long fileId) {
        EventFile eventFile = eventFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
        
        try {
            // Supprimer le fichier physique
            Path filePath = Paths.get(eventFile.getFilePath());
            Files.deleteIfExists(filePath);
            
            // Supprimer l'entrée dans la base de données
            eventFileRepository.delete(eventFile);
            
        } catch (IOException e) {
            throw new RuntimeException("Impossible de supprimer le fichier", e);
        }
    }
    
    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================
    
    /**
     * Convertir EventFile → EventFileDTO
     */
    private EventFileDTO convertToDTO(EventFile file) {
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
    
    /**
     * Extraire l'extension du fichier
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}