package com.eventmanagement.eventservice.dto;

import com.eventmanagement.eventservice.enums.EventStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour transférer les données d'un événement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    
    private Long id;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, message = "La description doit contenir au moins 10 caractères")
    private String description;
    
    @NotBlank(message = "Le lieu est obligatoire")
    private String location;
    
    @NotNull(message = "La date de l'événement est obligatoire")
    @Future(message = "La date de l'événement doit être dans le futur")
    private LocalDateTime eventDate;
    
    private LocalDateTime endDate;
    
    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins de 1")
    @Max(value = 100000, message = "La capacité ne peut pas dépasser 100000")
    private Integer capacity;
    
    private Integer currentParticipants = 0;
    
    @NotNull(message = "Le statut est obligatoire")
    private EventStatus status;
    
    @NotNull(message = "L'ID de l'organisateur est obligatoire")
    private Long organizerId;
    
    private String organizerName;
    
    @NotBlank(message = "La catégorie est obligatoire")
    private String category;
    
    private Boolean isFree = true;
    
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double price;
    
    // Liste des fichiers associés
    private List<EventFileDTO> files = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Champs calculés
    private Boolean isAvailable; // Places disponibles ?
    private Integer availableSeats; // Nombre de places restantes
}