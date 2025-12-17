package com.eventmanagement.eventservice.model;

import com.eventmanagement.eventservice.enums.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ENTITÉ EVENT_FILE (Table des fichiers)
 * Représente un fichier lié à un événement (image, vidéo, document)
 */
@Entity
@Table(name = "event_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName; // Nom original du fichier
    
    @Column(nullable = false)
    private String filePath; // Chemin de stockage du fichier
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType; // Type de fichier (IMAGE, VIDEO, DOCUMENT)
    
    @Column
    private String contentType; // Type MIME (ex: image/jpeg, application/pdf)
    
    @Column
    private Long fileSize; // Taille du fichier en octets
    
    @Column
    private String description; // Description optionnelle du fichier
    
    // Relation ManyToOne avec Event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}