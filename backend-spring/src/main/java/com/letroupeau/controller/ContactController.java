package com.letroupeau.controller;

import com.letroupeau.model.Contact;
import com.letroupeau.service.ContactService;
import com.letroupeau.dto.ContactRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Endpoint sécurisé. 
     * @PreAuthorize garantit que seuls un ADMIN ou un LEADER peut appeler cette route.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
    public ResponseEntity<List<Contact>> getContacts() {
        // La logique d'isolation est déléguée au Service, le contrôleur reste "propre"
        List<Contact> contacts = contactService.getAllContactsForCurrentUser();
        return ResponseEntity.ok(contacts);
    }

    /**
     * Endpoint pour créer un contact. 
     * Tous les membres authentifiés peuvent l'utiliser (pas besoin de @PreAuthorize spécifique).
     */
    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody ContactRequest request) {
        Contact createdContact = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }
}