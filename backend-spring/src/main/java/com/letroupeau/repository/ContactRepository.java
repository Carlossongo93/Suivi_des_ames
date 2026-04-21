package com.letroupeau.repository;

import com.letroupeau.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // MÉTHODE CRITIQUE : Permet de filtrer au niveau de la base de données 
    // pour s'assurer qu'un Leader ne récupère que les contacts de son équipe.
    List<Contact> findByTeamId(Long teamId);
    
}