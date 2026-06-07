package com.techacademy.srr.model;

/**
 * Modèle représentant un utilisateur du système SRR.
 *
 * <p>Correspond à la table {@code utilisateur} de la base de données.
 * Les rôles possibles sont :</p>
 * <ul>
 *   <li>{@code ADMIN} : peut gérer les ressources et voir toutes les réservations</li>
 *   <li>{@code USER}  : peut consulter les ressources et gérer ses propres réservations</li>
 * </ul>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class Utilisateur {

    /** Identifiant unique (clé primaire en base). */
    private int id;

    /** Identifiant de connexion (unique). */
    private String login;

    /**
     * Mot de passe haché (BCrypt).
     * Ne jamais stocker ni afficher le mot de passe en clair.
     */
    private String passwordHash;

    /** Rôle de l'utilisateur : "ADMIN" ou "USER". */
    private String role;

    // -----------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------

    /** Constructeur sans argument (requis pour instanciation générique). */
    public Utilisateur() {}

    /**
     * Constructeur complet.
     *
     * @param id           identifiant de la base de données
     * @param login        nom d'utilisateur
     * @param passwordHash hash BCrypt du mot de passe
     * @param role         rôle ("ADMIN" ou "USER")
     */
    public Utilisateur(int id, String login, String passwordHash, String role) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // -----------------------------------------------------------
    // Accesseurs (getters / setters)
    // -----------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String h) { this.passwordHash = h; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // -----------------------------------------------------------
    // Méthodes utilitaires
    // -----------------------------------------------------------

    /**
     * Indique si cet utilisateur est administrateur.
     *
     * @return {@code true} si le rôle est ADMIN
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", login='" + login + "', role='" + role + "'}";
    }
}
