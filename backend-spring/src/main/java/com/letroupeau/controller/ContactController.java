package com.letroupeau.controller;

import com.letroupeau.model.Contact;
import com.letroupeau.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}