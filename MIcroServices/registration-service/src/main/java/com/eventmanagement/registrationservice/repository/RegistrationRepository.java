package com.eventmanagement.registrationservice.repository;

import com.eventmanagement.registrationservice.enums.RegistrationStatus;
import com.eventmanagement.registrationservice.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY pour les opérations sur la table Registration
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    /**
     * Trouver toutes les inscriptions d'un utilisateur
     * Utile pour afficher "Mes événements" côté participant
     */
    List<Registration> findByUserId(Long userId);
    
    /**
     * Trouver toutes les inscriptions d'un événement
     * Utile pour afficher la liste des participants côté organisateur
     */
    List<Registration> findByEventId(Long eventId);
    
    /**
     * Trouver une inscription spécifique (user + event)
     * Utile pour vérifier si un utilisateur est déjà inscrit
     */
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
    
    /**
     * Vérifier si un utilisateur est déjà inscrit à un événement
     */
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    
    /**
     * Trouver les inscriptions par statut
     */
    List<Registration> findByStatus(RegistrationStatus status);
    
    /**
     * Trouver les inscriptions d'un événement par statut
     * Exemple : tous les participants CONFIRMÉS d'un événement
     */
    List<Registration> findByEventIdAndStatus(Long eventId, RegistrationStatus status);
    
    /**
     * Trouver les inscriptions d'un utilisateur par statut
     * Exemple : tous les événements CONFIRMÉS d'un utilisateur
     */
    List<Registration> findByUserIdAndStatus(Long userId, RegistrationStatus status);
    
    /**
     * Compter les inscriptions d'un événement (par statut)
     */
    Long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
    
    /**
     * Compter toutes les inscriptions actives d'un événement
     * (PENDING + CONFIRMED, sans les CANCELLED)
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.eventId = :eventId " +
           "AND r.status IN ('PENDING', 'CONFIRMED')")
    Long countActiveRegistrationsByEventId(@Param("eventId") Long eventId);
    
    /**
     * Trouver les inscriptions d'un utilisateur pour des événements futurs
     */
    @Query("SELECT r FROM Registration r WHERE r.userId = :userId " +
           "AND r.eventDate >= CURRENT_TIMESTAMP " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.eventDate ASC")
    List<Registration> findUpcomingEventsByUserId(@Param("userId") Long userId);
    
    /**
     * Trouver les inscriptions d'un utilisateur pour des événements passés
     */
    @Query("SELECT r FROM Registration r WHERE r.userId = :userId " +
           "AND r.eventDate < CURRENT_TIMESTAMP " +
           "ORDER BY r.eventDate DESC")
    List<Registration> findPastEventsByUserId(@Param("userId") Long userId);
}