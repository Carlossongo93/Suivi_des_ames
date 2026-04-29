package com.letroupeau.service;

import com.letroupeau.dto.ContactRequest;
import com.letroupeau.model.Contact;
import com.letroupeau.model.User;
import com.letroupeau.repository.ContactRepository;
import com.letroupeau.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public List<Contact> getAllContactsForCurrentUser() {
        User currentUser = getCurrentUser();
        if (currentUser.getTeam() != null) {
            return contactRepository.findByTeamId(currentUser.getTeam().getId());
        }
        return List.of();
    }

    public Contact createContact(ContactRequest request) {
        User currentUser = getCurrentUser();

        if (currentUser.getTeam() == null) {
            throw new IllegalStateException("Impossible de créer un contact : Vous n'êtes assigné à aucune équipe.");
        }

        Contact contact = new Contact();
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setPhone(request.phone());
        contact.setAddress(request.address());
        
        // Assigne automatiquement l'équipe du créateur
        contact.setTeam(currentUser.getTeam());

        return contactRepository.save(contact);
    }

    public Contact getContactById(Long id) {
        User currentUser = getCurrentUser();
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact introuvable"));
                
        // SÉCURITÉ : Vérifie que le contact appartient bien à l'équipe de l'utilisateur connecté
        if (contact.getTeam() == null || !contact.getTeam().getId().equals(currentUser.getTeam().getId())) {
            throw new RuntimeException("Accès refusé : Ce contact n'appartient pas à votre équipe.");
        }
        return contact;
    }

    public Contact updateContact(Long id, ContactRequest request) {
        // On réutilise getContactById pour bénéficier de la sécurité qui vérifie l'équipe
        Contact contact = getContactById(id); 
        
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setPhone(request.phone());
        contact.setAddress(request.address());
        
        return contactRepository.save(contact);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}