package com.eventmanagement.eventservice.service;

import com.eventmanagement.eventservice.dto.EventFileDTO;
import com.eventmanagement.eventservice.enums.FileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * INTERFACE DU SERVICE DE GESTION DES FICHIERS
 * Gère l'upload et le téléchargement des fichiers
 */
public interface FileStorageService {
    
    // Upload d'un fichier pour un événement
    EventFileDTO uploadFile(Long eventId, MultipartFile file, FileType fileType, String description);
    
    // Upload de plusieurs fichiers
    List<EventFileDTO> uploadFiles(Long eventId, List<MultipartFile> files, FileType fileType);
    
    // Télécharger un fichier
    Resource loadFileAsResource(Long fileId);
    
    // Obtenir tous les fichiers d'un événement
    List<EventFileDTO> getEventFiles(Long eventId);
    
    // Obtenir les fichiers d'un événement par type
    List<EventFileDTO> getEventFilesByType(Long eventId, FileType fileType);
    
    // Supprimer un fichier
    void deleteFile(Long fileId);
}