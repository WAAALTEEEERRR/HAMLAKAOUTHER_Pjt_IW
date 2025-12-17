package com.eventmanagement.userservice.repository;

import com.eventmanagement.userservice.enums.UserRole;
import com.eventmanagement.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY (Couche DAO - Data Access Object)
 * Interface pour gérer les opérations sur la table "users"
 * Spring Data JPA génère automatiquement l'implémentation !
 */

@Repository
@RepositoryRestResource // Crée automatiquement des endpoints REST (optionnel)

public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Trouver un utilisateur par son email
     * Spring génère automatiquement la requête SQL : SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Vérifier si un email existe déjà dans la base de données
     * Spring génère automatiquement : SELECT COUNT(*) FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouver tous les utilisateurs ayant un rôle spécifique (ORGANIZER ou PARTICIPANT)
     * Spring génère automatiquement : SELECT * FROM users WHERE role = ?
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Trouver un utilisateur par email ET mot de passe (pour la connexion)
     * Spring génère automatiquement : SELECT * FROM users WHERE email = ? AND password = ?
     */
    Optional<User> findByEmailAndPassword(String email, String password);
}