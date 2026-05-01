package com.letroupeau.repository;

import com.letroupeau.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    // Permet de récupérer l'historique d'un contact, du plus récent au plus ancien (US-3.2)
    List<Interaction> findByContactIdOrderByCreatedAtDesc(Long contactId);
}