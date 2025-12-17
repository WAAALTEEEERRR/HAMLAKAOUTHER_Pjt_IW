package com.eventmanagement.userservice.controller;

import com.eventmanagement.userservice.dto.LoginRequest;
import com.eventmanagement.userservice.dto.LoginResponse;
import com.eventmanagement.userservice.dto.UserDTO;
import com.eventmanagement.userservice.enums.UserRole;
import com.eventmanagement.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER (Couche Web - API REST)
 * Expose les endpoints pour gérer les utilisateurs
 * Tous les endpoints commencent par : /api/users
 */
@RestController // Indique que c'est un contrôleur REST
@RequestMapping("/api/users") // Préfixe pour tous les endpoints
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")

public class UserController {
    
    @Autowired // Injection automatique du service
    private UserService userService;
    
    /**
     * ENDPOINT : INSCRIPTION
     * POST /api/users/register
     * Crée un nouveau compte utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.register(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED); // Status 201
    }
    
    /**
     * ENDPOINT : CONNEXION
     * POST /api/users/login
     * Connecte un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response); // Status 200
    }
    
    /**
     * ENDPOINT : OBTENIR TOUS LES UTILISATEURS
     * GET /api/users
     * Retourne la liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * ENDPOINT : OBTENIR UN UTILISATEUR PAR ID
     * GET /api/users/{id}
     * Exemple : GET /api/users/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * ENDPOINT : OBTENIR LES UTILISATEURS PAR RÔLE
     * GET /api/users/role/{role}
     * Exemple : GET /api/users/role/ORGANIZER
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable UserRole role) {
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    /**
     * ENDPOINT : METTRE À JOUR UN UTILISATEUR
     * PUT /api/users/{id}
     * Exemple : PUT /api/users/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, 
                                              @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * ENDPOINT : SUPPRIMER UN UTILISATEUR
     * DELETE /api/users/{id}
     * Exemple : DELETE /api/users/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Status 204
    }
    
    /**
     * ENDPOINT DE TEST
     * GET /api/users/test
     * Permet de vérifier que le microservice fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("User Service fonctionne correctement ! ✅");
    }
}