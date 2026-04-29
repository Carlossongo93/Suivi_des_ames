package com.letroupeau.controller;

import com.letroupeau.dto.ContactRequest;
import com.letroupeau.model.Contact;
import com.letroupeau.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Récupérer tous les contacts (US-2.2)
    @GetMapping
    public ResponseEntity<List<Contact>> getContacts() {
        List<Contact> contacts = contactService.getAllContactsForCurrentUser();
        return ResponseEntity.ok(contacts);
    }

    // Créer un contact (US-2.1)
    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody ContactRequest request) {
        Contact createdContact = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }

    // Voir une fiche contact (US-2.3)
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        return ResponseEntity.ok(contact);
    }

    // Modifier un contact (US-2.4)
    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @Valid @RequestBody ContactRequest request) {
        Contact updatedContact = contactService.updateContact(id, request);
        return ResponseEntity.ok(updatedContact);
    }
}