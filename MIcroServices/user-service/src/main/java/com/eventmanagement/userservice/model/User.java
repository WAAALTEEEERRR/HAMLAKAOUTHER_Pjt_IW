package com.eventmanagement.userservice.model;

import com.eventmanagement.userservice.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ENTITÉ USER
 * Représente un utilisateur dans la base de données
 * Cette classe correspond à la table "users"
 */

@Entity // Indique que c'est une table dans la base de données
@Table(name = "users") // Nom de la table
@Data // Lombok : génère automatiquement getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok : génère un constructeur sans paramètres
@AllArgsConstructor // Lombok : génère un constructeur avec tous les paramètres

public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, length = 10)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // ORGANIZER ou PARTICIPANT
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Méthode appelée automatiquement AVANT l'insertion dans la BDD
     * Initialise les dates de création et de mise à jour
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Méthode appelée automatiquement AVANT la mise à jour dans la BDD
     * Met à jour la date de modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}