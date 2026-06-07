package com.techacademy.srr.util;

import com.techacademy.srr.model.Utilisateur;

/**
 * Gestionnaire de session utilisateur.
 *
 * <p>Stocke l'utilisateur actuellement connecté afin que les contrôleurs
 * puissent vérifier son identité et son rôle sans repasser par la base.
 * Un utilisateur ne peut modifier que ses propres réservations, sauf
 * s'il possède le rôle ADMIN.</p>
 *
 * <p>Cette classe utilise un patron Singleton statique simple,
 * adapté à une application desktop mono-utilisateur.</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class SessionManager {

    /** Utilisateur actuellement connecté, ou {@code null} si déconnecté. */
    private static Utilisateur utilisateurConnecte;

    /** Constructeur privé : classe utilitaire, pas d'instanciation. */
    private SessionManager() {}

    // -----------------------------------------------------------
    // Gestion de la session
    // -----------------------------------------------------------

    /**
     * Ouvre une session pour l'utilisateur donné.
     *
     * @param utilisateur l'utilisateur qui vient de s'authentifier
     */
    public static void ouvrir(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    /**
     * Ferme la session (déconnexion).
     */
    public static void fermer() {
        utilisateurConnecte = null;
    }

    /**
     * Retourne l'utilisateur actuellement connecté.
     *
     * @return l'utilisateur, ou {@code null} si aucune session n'est active
     */
    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    /**
     * Indique si une session est active.
     *
     * @return {@code true} si un utilisateur est connecté
     */
    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    /**
     * Indique si l'utilisateur connecté est administrateur.
     *
     * @return {@code true} si le rôle est ADMIN
     */
    public static boolean estAdmin() {
        return estConnecte()
                && "ADMIN".equals(utilisateurConnecte.getRole());
    }

    /**
     * Vérifie que l'utilisateur connecté est bien le propriétaire d'une
     * réservation (ou qu'il est administrateur).
     *
     * @param idProprietaire l'identifiant du propriétaire de la réservation
     * @return {@code true} si l'action est autorisée
     */
    public static boolean peutModifier(int idProprietaire) {
        if (!estConnecte()) return false;
        return estAdmin() || utilisateurConnecte.getId() == idProprietaire;
    }
}
