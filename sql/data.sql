-- =============================================================
-- Jeu d'essai - Données de démonstration
-- Les mots de passe sont hashés en BCrypt (voir PasswordUtil.java)
-- Mots de passe en clair : admin123, alice123, bob123, charlie123, diana123
-- =============================================================

USE srr;

-- Nettoyage des données existantes (dans l'ordre pour respecter les FK)
DELETE FROM reservations;
DELETE FROM ressources;
DELETE FROM utilisateurs;

-- Réinitialisation des auto-increments
ALTER TABLE reservations AUTO_INCREMENT = 1;
ALTER TABLE ressources AUTO_INCREMENT = 1;
ALTER TABLE utilisateurs AUTO_INCREMENT = 1;

-- -------------------------
-- Utilisateurs
-- -------------------------
INSERT INTO utilisateurs (login, mot_de_passe, role) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
    ('alice', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PkVJo8sclgbKm', 'USER'),
    ('bob', '$2a$10$8K1p/a0dR1xqM8K3Z.FtTuMpbUbMJq3G2Z9EEFxMDHdKS7Ql/8Vsa', 'USER'),
    ('charlie', '$2a$10$LkPIIcl/9B2Z8rLkFWQCxe2J8LxQ7j1h4Vm8xK7Pq3r4m5t6n7o8p', 'USER'),
    ('diana',   '$2a$10$Rq3s4t5u6v7w8x9y0z1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r', 'USER');

-- -------------------------
-- Ressources
-- -------------------------
INSERT INTO ressources (nom, type, description) VALUES
    ('Salle Ampère', 'Salle', 'Grande salle de cours, 40 places, tableau blanc interactif'),
    ('Salle Curie', 'Salle', 'Salle de TP informatique, 20 postes fixes'),
    ('Salle Turing', 'Salle', 'Salle de réunion, 10 places, écran de présentation'),
    ('Vidéoprojecteur VP-01', 'Vidéoprojecteur', 'Epson EB-W51, résolution WXGA, avec télécommande'),
    ('Vidéoprojecteur VP-02', 'Vidéoprojecteur', 'BenQ MX535, résolution XGA, câble HDMI inclus'),
    ('Laptop Dell-01', 'Ordinateur portable', 'Dell Latitude 5520, i5, 16Go RAM, chargeur inclus'),
    ('Laptop Dell-02', 'Ordinateur portable', 'Dell Latitude 5520, i5, 16Go RAM, chargeur inclus'),
    ('Laptop HP-01', 'Ordinateur portable', 'HP EliteBook 840, i7, 32Go RAM, pour démos avancées');

-- -------------------------
-- Réservations (passées et futures pour la démo)
-- -------------------------
INSERT INTO reservations (id_utilisateur, id_ressource, date_debut, date_fin) VALUES
    -- Réservations passées
    (2, 1, '2025-03-10 09:00:00', '2025-03-10 11:00:00'),  -- alice, Salle Ampère
    (3, 4, '2025-03-11 14:00:00', '2025-03-11 16:00:00'),  -- bob, VP-01
    (4, 6, '2025-03-12 10:00:00', '2025-03-12 12:00:00'),  -- charlie, Laptop Dell-01

    -- Réservations futures (pour la démo de la détection de conflit)
    (2, 1, DATEADD(NOW(), INTERVAL 1 DAY), DATEADD(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR), -- alice, Salle Ampère demain matin
    (3, 1, DATEADD(NOW(), INTERVAL 2 DAY), DATEADD(NOW(), INTERVAL 2 DAY) + INTERVAL 3 HOUR), -- bob, Salle Ampère dans 2 jours
    (5, 3, DATEADD(NOW(), INTERVAL 1 DAY), DATEADD(NOW(), INTERVAL 1 DAY) + INTERVAL 1 HOUR), -- diana, Salle Turing
    (4, 4, DATEADD(NOW(), INTERVAL 3 DAY), DATEADD(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR), -- charlie, VP-01
    (2, 7, DATEADD(NOW(), INTERVAL 4 DAY), DATEADD(NOW(), INTERVAL 4 DAY) + INTERVAL 4 HOUR); -- alice, Laptop Dell-02

-- Note pour la démo : pour tester le conflit, essayer de réserver la Salle Ampère
-- sur le même créneau qu'alice (demain). L'application doit bloquer la réservation.
