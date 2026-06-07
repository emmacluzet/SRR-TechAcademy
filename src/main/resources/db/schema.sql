-- ============================================================
-- schema.sql - Création du schéma de la base de données SRR
-- Tech-Academy - Système de Réservation de Ressources
-- ============================================================
-- Exécuter ce script en premier pour initialiser la base.
-- Commande : psql -U postgres -d srr_db -f schema.sql

-- Suppression des tables dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS ressource   CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

-- -------------------------------------------------------
-- Table UTILISATEUR
-- Stocke les comptes des utilisateurs et administrateurs.
-- -------------------------------------------------------
CREATE TABLE utilisateur (
    id       SERIAL       PRIMARY KEY,
    login    VARCHAR(50)  NOT NULL UNIQUE,
    -- Le mot de passe est stocké sous forme de hash BCrypt (jamais en clair)
    password VARCHAR(255) NOT NULL,
    -- Rôle : 'ADMIN' ou 'USER'
    role     VARCHAR(10)  NOT NULL DEFAULT 'USER'
        CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'USER'))
);

-- -------------------------------------------------------
-- Table RESSOURCE
-- Représente une ressource réservable (salle, projecteur…)
-- -------------------------------------------------------
CREATE TABLE ressource (
    id   SERIAL       PRIMARY KEY,
    nom  VARCHAR(100) NOT NULL,
    -- Type : ex. 'SALLE', 'PROJECTEUR', 'ORDINATEUR'
    type VARCHAR(50)  NOT NULL
);

-- -------------------------------------------------------
-- Table RESERVATION
-- Enregistre les créneaux réservés par les utilisateurs.
-- -------------------------------------------------------
CREATE TABLE reservation (
    id             SERIAL      PRIMARY KEY,
    id_utilisateur INT         NOT NULL
        REFERENCES utilisateur(id) ON DELETE CASCADE,
    id_ressource   INT         NOT NULL
        REFERENCES ressource(id)   ON DELETE CASCADE,
    date_debut     TIMESTAMP   NOT NULL,
    date_fin       TIMESTAMP   NOT NULL,
    -- Contrainte métier : la date de fin doit être après la date de début
    CONSTRAINT chk_dates CHECK (date_fin > date_debut)
);

-- Index pour accélérer les requêtes de vérification de disponibilité
CREATE INDEX idx_reservation_ressource ON reservation(id_ressource, date_debut, date_fin);
