package com.eventmanagement.eventservice.repository;

import com.eventmanagement.eventservice.enums.FileType;
import com.eventmanagement.eventservice.model.EventFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY pour les opérations sur la table EventFile
 */
@Repository
public interface EventFileRepository extends JpaRepository<EventFile, Long> {
    
    // Trouver tous les fichiers d'un événement
    List<EventFile> findByEventId(Long eventId);
    
    // Trouver les fichiers d'un événement par type
    List<EventFile> findByEventIdAndFileType(Long eventId, FileType fileType);
    
    // Compter les fichiers d'un événement
    Long countByEventId(Long eventId);
}