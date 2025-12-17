package com.eventmanagement.eventservice.repository;

import com.eventmanagement.eventservice.enums.EventStatus;
import com.eventmanagement.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORY pour les opérations sur la table Event
 * Spring Data JPA génère automatiquement les implémentations
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Recherche par organisateur
    List<Event> findByOrganizerId(Long organizerId);
    
    // Recherche par statut
    List<Event> findByStatus(EventStatus status);
    
    // Recherche par lieu (contient le texte)
    List<Event> findByLocationContainingIgnoreCase(String location);
    
    // Recherche par catégorie
    List<Event> findByCategory(String category);
    
    // Recherche par date (événements après une date)
    List<Event> findByEventDateAfter(LocalDateTime date);
    
    // Recherche par date (événements avant une date)
    List<Event> findByEventDateBefore(LocalDateTime date);
    
    // Recherche par date (entre deux dates)
    List<Event> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Recherche événements gratuits
    List<Event> findByIsFree(Boolean isFree);
    
    // Recherche événements avec places disponibles
    @Query("SELECT e FROM Event e WHERE e.currentParticipants < e.capacity AND e.status = 'PUBLISHED'")
    List<Event> findAvailableEvents();
    
    // Recherche complexe par mot-clé (titre ou description)
    @Query("SELECT e FROM Event e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeyword(@Param("keyword") String keyword);
    
    // Recherche multi-critères (requête personnalisée)
    @Query("SELECT e FROM Event e WHERE " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:organizerId IS NULL OR e.organizerId = :organizerId) AND " +
           "(:isFree IS NULL OR e.isFree = :isFree) AND " +
           "(:startDate IS NULL OR e.eventDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.eventDate <= :endDate)")
    List<Event> searchEvents(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("category") String category,
        @Param("status") EventStatus status,
        @Param("organizerId") Long organizerId,
        @Param("isFree") Boolean isFree,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}