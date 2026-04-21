package com.letroupeau.repository;

import com.letroupeau.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Essentiel pour l'authentification : retrouver l'utilisateur par son email
    Optional<User> findByEmail(String email);
    
}