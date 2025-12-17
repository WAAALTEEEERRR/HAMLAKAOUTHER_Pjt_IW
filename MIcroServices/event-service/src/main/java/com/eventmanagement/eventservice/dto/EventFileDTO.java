package com.eventmanagement.eventservice.dto;

import com.eventmanagement.eventservice.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour transférer les données d'un fichier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFileDTO {
    
    private Long id;
    private String fileName;
    private String filePath;
    private FileType fileType;
    private String contentType;
    private Long fileSize;
    private String description;
    private Long eventId;
    private LocalDateTime uploadedAt;
    
    // URL de téléchargement (sera générée par le service)
    private String downloadUrl;
}