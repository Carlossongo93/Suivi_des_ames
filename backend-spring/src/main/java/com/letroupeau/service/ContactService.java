package com.letroupeau.service;

import com.letroupeau.model.Contact;
import com.letroupeau.model.Role;
import com.letroupeau.model.User;
import com.letroupeau.repository.ContactRepository;
import com.letroupeau.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    /**
     * MÉTHODE CRITIQUE (Axe B ADD) : Récupération isolée des contacts.
     * La sécurité est gérée côté serveur. Le Frontend ne peut pas tricher.
     */
    public List<Contact> getAllContactsForCurrentUser() {
        User currentUser = getCurrentUser();

        // Règle Métier 1 : L'Admin voit tout le troupeau
        if (currentUser.getRole() == Role.ADMIN) {
            return contactRepository.findAll();
        } 
        // Règle Métier 2 : Le Leader ne voit QUE son équipe
        else if (currentUser.getRole() == Role.LEADER && currentUser.getTeam() != null) {
            return contactRepository.findByTeamId(currentUser.getTeam().getId());
        }

        // Règle par défaut : un simple membre ne voit personne (ou retourne une liste vide)
        return List.of();
    }

    /**
     * Helper pour récupérer l'utilisateur actuellement connecté via le token JWT
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé dans le contexte de sécurité"));
    }
}