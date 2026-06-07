package com.techacademy.srr.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant une réservation de ressource.
 *
 * <p>Correspond à la table {@code reservation} de la base de données.
 * Une réservation associe un {@link Utilisateur}, une {@link Ressource}
 * et un créneau horaire (date de début / date de fin).</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class Reservation {

    /** Identifiant unique (clé primaire en base). */
    private int id;

    /** Utilisateur ayant effectué la réservation. */
    private Utilisateur utilisateur;

    /** Ressource réservée. */
    private Ressource ressource;

    /** Date et heure de début du créneau. */
    private LocalDateTime dateDebut;

    /** Date et heure de fin du créneau. */
    private LocalDateTime dateFin;

    // -----------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------

    /** Constructeur sans argument. */
    public Reservation() {}

    /**
     * Constructeur complet.
     *
     * @param id          identifiant en base
     * @param utilisateur propriétaire de la réservation
     * @param ressource   ressource concernée
     * @param dateDebut   début du créneau
     * @param dateFin     fin du créneau
     */
    public Reservation(int id, Utilisateur utilisateur, Ressource ressource, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.ressource = ressource;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // -----------------------------------------------------------
    // Accesseurs
    // -----------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur u) { this.utilisateur = u; }

    public Ressource getRessource() { return ressource; }
    public void setRessource(Ressource r) { this.ressource = r; }

    public LocalDateTime getDateDebut(){ return dateDebut; }
    public void setDateDebut(LocalDateTime d) { this.dateDebut = d; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime d) { this.dateFin = d; }

    // -----------------------------------------------------------
    // Méthodes métier
    // -----------------------------------------------------------

    /**
     * Crée (enregistre) la réservation via le DAO.
     * Déclarée ici pour satisfaire la modélisation UML ; l'appel réel
     * est délégué à {@code ReservationDAO.creer()}.
     */
    public void create() {
        // Délégué à ReservationDAO.creer(this)
    }

    /**
     * Annule (supprime) la réservation via le DAO.
     * Déclarée ici pour satisfaire la modélisation UML ; l'appel réel
     * est délégué à {@code ReservationDAO.annuler()}.
     */
    public void cancel() {
        // Délégué à ReservationDAO.annuler(this.id)
    }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", ressource=" + (ressource != null ? ressource.getNom() : "?") + ", debut=" + dateDebut + ", fin=" + dateFin + "}";
    }
}
