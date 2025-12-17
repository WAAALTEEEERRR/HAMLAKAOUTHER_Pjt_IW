package com.eventmanagement.userservice.service;

import com.eventmanagement.userservice.dto.LoginRequest;
import com.eventmanagement.userservice.dto.LoginResponse;
import com.eventmanagement.userservice.dto.UserDTO;
import com.eventmanagement.userservice.enums.UserRole;

import java.util.List;

/**
 * INTERFACE SERVICE (Couche Métier)
 * Définit les opérations métier disponibles pour les utilisateurs
 */
public interface UserService {
    
    /**
     * Inscrire un nouvel utilisateur
     */
    UserDTO register(UserDTO userDTO);
    
    /**
     * Connecter un utilisateur
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * Obtenir un utilisateur par son ID
     */
    UserDTO getUserById(Long id);
    
    /**
     * Mettre à jour les informations d'un utilisateur
     */
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    /**
     * Supprimer un utilisateur
     */
    void deleteUser(Long id);
    
    /**
     * Obtenir tous les utilisateurs
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Obtenir tous les utilisateurs par rôle (ORGANIZER ou PARTICIPANT)
     */
    List<UserDTO> getUsersByRole(UserRole role);
}