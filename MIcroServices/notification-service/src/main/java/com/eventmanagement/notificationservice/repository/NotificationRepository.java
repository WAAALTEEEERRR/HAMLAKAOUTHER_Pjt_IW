package com.eventmanagement.notificationservice.repository;

import com.eventmanagement.notificationservice.enums.NotificationStatus;
import com.eventmanagement.notificationservice.enums.NotificationType;
import com.eventmanagement.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORY pour les opérations sur la table Notification
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Trouver toutes les notifications d'un utilisateur
     * Triées par date de création (plus récentes en premier)
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Trouver les notifications non lues d'un utilisateur
     */
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
    
    /**
     * Trouver les notifications non lues d'un utilisateur (triées)
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND n.status IN ('SENT', 'PENDING') " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByUserId(@Param("userId") Long userId);
    
    /**
     * Compter les notifications non lues d'un utilisateur
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
           "AND n.status IN ('SENT', 'PENDING')")
    Long countUnreadNotificationsByUserId(@Param("userId") Long userId);
    
    /**
     * Trouver les notifications d'un événement
     */
    List<Notification> findByEventId(Long eventId);
    
    /**
     * Trouver les notifications par type
     */
    List<Notification> findByType(NotificationType type);
    
    /**
     * Trouver les notifications d'un utilisateur par type
     */
    List<Notification> findByUserIdAndType(Long userId, NotificationType type);
    
    /**
     * Trouver les notifications récentes (dernières 24h)
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND n.createdAt >= :since " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUserId(
        @Param("userId") Long userId, 
        @Param("since") LocalDateTime since
    );
    
    /**
     * Trouver les notifications par priorité
     */
    List<Notification> findByPriority(String priority);
    
    /**
     * Trouver les notifications en attente d'envoi
     */
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);
    
    /**
     * Supprimer les anciennes notifications (nettoyage)
     * Exemple : supprimer les notifications de plus de 90 jours
     */
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Trouver les notifications d'un événement pour un utilisateur spécifique
     */
    List<Notification> findByUserIdAndEventId(Long userId, Long eventId);
}