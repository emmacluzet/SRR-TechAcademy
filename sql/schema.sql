-- =============================================================
-- Schéma de la base de données - Système de Réservation (SRR)
-- Exécuter ce fichier avant data.sql
-- =============================================================

CREATE DATABASE IF NOT EXISTS srr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE srr;

-- Table des utilisateurs
-- Le champ 'role' accepte uniquement USER ou ADMIN
CREATE TABLE IF NOT EXISTS utilisateurs (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    login         VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,  -- stocké hashé en BCrypt
    role          ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER'
);

-- Table des ressources (salles et matériel)
CREATE TABLE IF NOT EXISTS ressources (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    type        VARCHAR(50)  NOT NULL,  -- ex: Salle, Vidéoprojecteur, Ordinateur portable
    description TEXT
);

-- Table des réservations
-- Une réservation lie un utilisateur à une ressource sur un créneau horaire
CREATE TABLE IF NOT EXISTS reservations (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur  INT      NOT NULL,
    id_ressource    INT      NOT NULL,
    date_debut      DATETIME NOT NULL,
    date_fin        DATETIME NOT NULL,
    CONSTRAINT fk_reservation_utilisateur FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateurs(id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_ressource FOREIGN KEY (id_ressource)
        REFERENCES ressources(id) ON DELETE CASCADE,
    -- Contrainte : la date de fin doit être après la date de début
    CONSTRAINT chk_dates CHECK (date_fin > date_debut)
);

-- Index pour accélérer les recherches de disponibilité
CREATE INDEX idx_reservations_ressource ON reservations(id_ressource);
CREATE INDEX idx_reservations_utilisateur ON reservations(id_utilisateur);
