package com.eventmanagement.userservice.service;

import com.eventmanagement.userservice.dto.LoginRequest;
import com.eventmanagement.userservice.dto.LoginResponse;
import com.eventmanagement.userservice.dto.UserDTO;
import com.eventmanagement.userservice.enums.UserRole;
import com.eventmanagement.userservice.model.User;
import com.eventmanagement.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IMPLÉMENTATION DU SERVICE (Couche Métier)
 * Contient toute la logique métier de l'application
 */
@Service // Indique que c'est un service Spring
@Transactional // Gère automatiquement les transactions de base de données
public class UserServiceImpl implements UserService {
    
    @Autowired // Injection automatique du repository
    private UserRepository userRepository;
    
    /**
     * INSCRIPTION (Register)
     * 1. Vérifie que l'email n'existe pas déjà
     * 2. Convertit le DTO en Entity
     * 3. Sauvegarde dans la base de données
     * 4. Retourne le DTO du User créé
     */
    @Override
    public UserDTO register(UserDTO userDTO) {
        // Vérification : l'email existe déjà ?
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }
        
        // Conversion DTO → Entity
        User user = convertToEntity(userDTO);
        
        // Sauvegarde dans la base de données
        User savedUser = userRepository.save(user);
        
        // Conversion Entity → DTO et retour
        return convertToDTO(savedUser);
    }
    
    /**
     * CONNEXION (Login)
     * 1. Cherche l'utilisateur par email et password
     * 2. Si trouvé : retourne ses informations
     * 3. Si pas trouvé : lance une exception
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailAndPassword(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
        ).orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));
        
        // Création de la réponse avec les infos de l'utilisateur
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                "Connexion réussie"
        );
    }
    
    /**
     * OBTENIR UN UTILISATEUR PAR ID
     */
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
        return convertToDTO(user);
    }
    
    /**
     * METTRE À JOUR UN UTILISATEUR
     * 1. Vérifie que l'utilisateur existe
     * 2. Met à jour les champs
     * 3. Si l'email change, vérifie qu'il n'existe pas déjà
     * 4. Sauvegarde les modifications
     */
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));
        
        // Mise à jour des champs
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhone(userDTO.getPhone());
        
        // Si l'email change, vérifier qu'il n'existe pas déjà
        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("Un compte avec cet email existe déjà");
            }
            existingUser.setEmail(userDTO.getEmail());
        }
        
        // Sauvegarde des modifications
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }
    
    /**
     * SUPPRIMER UN UTILISATEUR
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * OBTENIR TOUS LES UTILISATEURS
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * OBTENIR LES UTILISATEURS PAR RÔLE
     */
    @Override
    public List<UserDTO> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ==========================================
    // MÉTHODES UTILITAIRES (Conversions)
    // ==========================================
    
    /**
     * Convertir Entity → DTO
     * Utilisé pour renvoyer les données au client
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword()); // ⚠️ En production : NE JAMAIS retourner le password !
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    /**
     * Convertir DTO → Entity
     * Utilisé pour sauvegarder les données dans la BDD
     */
    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // ⚠️ En production : hasher le password avec BCrypt !
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        return user;
    }
}