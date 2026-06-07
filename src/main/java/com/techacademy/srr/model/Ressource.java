package com.techacademy.srr.model;

/**
 * Modèle représentant une ressource pédagogique réservable.
 *
 * <p>Correspond à la table {@code ressource} de la base de données.
 * Une ressource peut être une salle, un projecteur ou un ordinateur portable.</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class Ressource {

    /** Identifiant unique (clé primaire en base). */
    private int id;

    /** Nom lisible de la ressource (ex. "Salle A101"). */
    private String nom;

    /** Catégorie de la ressource (ex. "SALLE", "PROJECTEUR", "ORDINATEUR"). */
    private String type;

    // -----------------------------------------------------------
    // Constructeurs
    // -----------------------------------------------------------

    /** Constructeur sans argument. */
    public Ressource() {}

    /**
     * Constructeur complet.
     *
     * @param id   identifiant de la base de données
     * @param nom  nom de la ressource
     * @param type catégorie de la ressource
     */
    public Ressource(int id, String nom, String type) {
        this.id = id;
        this.nom = nom;
        this.type = type;
    }

    // -----------------------------------------------------------
    // Accesseurs
    // -----------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    /**
     * Vérifie si la ressource est disponible pour un créneau donné.
     * Cette méthode délègue le calcul au {@code RessourceDAO}.
     * Elle est déclarée ici pour satisfaire la modélisation UML de la fiche.
     *
     * @return {@code false} par défaut (le DAO effectue la vraie vérification)
     */
    public boolean estDisponible() {
        // La logique réelle est dans RessourceDAO.verifierDisponibilite()
        return false;
    }

    // -----------------------------------------------------------
    // Affichage dans les ComboBox JavaFX
    // -----------------------------------------------------------

    /**
     * Retourne une représentation lisible pour les listes déroulantes.
     *
     * @return "{nom} ({type})"
     */
    @Override
    public String toString() {
        return nom + " (" + type + ")";
    }
}
