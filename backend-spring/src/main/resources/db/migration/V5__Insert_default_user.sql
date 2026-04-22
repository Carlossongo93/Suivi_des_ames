-- Insertion de l'utilisateur administrateur par défaut
-- Mot de passe en clair correspondant au hash : password123

INSERT INTO users (email, password, full_name, role)
VALUES (
    'admin@letroupeau.com', 
    '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5HEpSDRhK2z/Vv2A6.N2p4V.DeXpY2', 
    'Benoît Carl', 
    'ADMIN'
)
ON CONFLICT (email) DO NOTHING;