package com.eventmanagement.registrationservice.dto;

import com.eventmanagement.registrationservice.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour transférer les données d'une inscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    
    private Long id;
    
    // Informations utilisateur
    private Long userId;
    private String userEmail;
    private String userName;
    
    // Informations événement
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventLocation;
    
    // Informations inscription
    private RegistrationStatus status;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private String notes;
}