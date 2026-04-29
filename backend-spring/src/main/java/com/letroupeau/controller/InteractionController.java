package com.letroupeau.controller;

import com.letroupeau.dto.InteractionRequest;
import com.letroupeau.model.Contact;
import com.letroupeau.model.Interaction;
import com.letroupeau.model.User;
import com.letroupeau.repository.ContactRepository;
import com.letroupeau.repository.InteractionRepository;
import com.letroupeau.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contacts/{contactId}/interactions")
public class InteractionController {

    private final InteractionRepository interactionRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public InteractionController(InteractionRepository interactionRepository, ContactRepository contactRepository, UserRepository userRepository) {
        this.interactionRepository = interactionRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    // US-3.1 : Ajouter une interaction
    @PostMapping
    public ResponseEntity<Interaction> addInteraction(@PathVariable Long contactId, @Valid @RequestBody InteractionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        
        Contact contact = contactRepository.findById(contactId).orElseThrow();

        // Création de l'interaction
        Interaction interaction = new Interaction();
        interaction.setType(request.type());
        interaction.setSubType(request.subType());
        interaction.setNotes(request.notes());
        interaction.setContact(contact);
        interaction.setCreatedBy(currentUser);

        // Mise à jour de la date sur le contact
        contact.setLastInteractionAt(LocalDateTime.now());
        contactRepository.save(contact);

        return ResponseEntity.status(HttpStatus.CREATED).body(interactionRepository.save(interaction));
    }

    // US-3.2 : Voir l'historique complet
    @GetMapping
    public ResponseEntity<List<Interaction>> getInteractions(@PathVariable Long contactId) {
        return ResponseEntity.ok(interactionRepository.findByContactIdOrderByCreatedAtDesc(contactId));
    }
}