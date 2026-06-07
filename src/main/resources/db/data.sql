-- ============================================================
-- data.sql - Données initiales pour les tests
-- Tech-Academy - Système de Réservation de Ressources
-- ============================================================
-- Exécuter après schema.sql.
-- Les mots de passe sont hachés avec BCrypt (rounds=10).
--   admin  -> "admin123"
--   user1  -> "user123"

-- Nettoyage préalable pour les tests répétables
TRUNCATE reservation, ressource, utilisateur RESTART IDENTITY CASCADE;

-- -------------------------------------------------------
-- Comptes utilisateurs de démonstration
-- -------------------------------------------------------
INSERT INTO utilisateur (login, password, role) VALUES
    ('admin',  '$2a$10$9Gj1Fx/lf.BVLn0PvkpjCOxRZZH7Jmqt5UvHNSE8hKtbLm3dJ9Pu', 'ADMIN'),
    ('user1',  '$2a$10$KcqXcWdI3e5fZbKqMb4vAurR7VT6mJ2OPx.DdPCqXKRNOKL9eNmPi', 'USER'),
    ('user2',  '$2a$10$KcqXcWdI3e5fZbKqMb4vAurR7VT6mJ2OPx.DdPCqXKRNOKL9eNmPi', 'USER');

-- -------------------------------------------------------
-- Ressources pédagogiques disponibles
-- -------------------------------------------------------
INSERT INTO ressource (nom, type) VALUES
    ('Salle A101',    'SALLE'),
    ('Salle B202',    'SALLE'),
    ('Salle C303',    'SALLE'),
    ('Projecteur 01', 'PROJECTEUR'),
    ('Projecteur 02', 'PROJECTEUR'),
    ('PC Portable 01','ORDINATEUR'),
    ('PC Portable 02','ORDINATEUR'),
    ('PC Portable 03','ORDINATEUR');

-- -------------------------------------------------------
-- Quelques réservations de démonstration
-- -------------------------------------------------------
INSERT INTO reservation (id_utilisateur, id_ressource, date_debut, date_fin) VALUES
    (2, 1, '2026-06-10 08:00:00', '2026-06-10 10:00:00'),
    (2, 4, '2026-06-10 08:00:00', '2026-06-10 10:00:00'),
    (3, 2, '2026-06-11 14:00:00', '2026-06-11 16:00:00');
