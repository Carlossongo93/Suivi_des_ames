-- Insertion d'un utilisateur administrateur par défaut
-- Mot de passe en clair : password123
-- Note : L'ID est souvent auto-incrémenté, donc on ne le précise pas si ta table est configurée ainsi.

INSERT INTO users (email, password, name, role)
VALUES (
    'admin@letroupeau.com', 
    '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5HEpSDRhK2z/Vv2A6.N2p4V.DeXpY2', 
    'Benoît Carl', 
    'ADMIN'
)
ON CONFLICT (email) DO NOTHING;