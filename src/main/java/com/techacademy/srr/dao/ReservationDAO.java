package com.techacademy.srr.dao;

import com.techacademy.srr.model.Reservation;
import com.techacademy.srr.model.Ressource;
import com.techacademy.srr.model.Utilisateur;
import com.techacademy.srr.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table {@code reservation}.
 *
 * <p>Gère la création, la lecture et la suppression des réservations.
 * Avant chaque insertion, la disponibilité est vérifiée via
 * {@link RessourceDAO#verifierDisponibilite} afin d'éviter tout
 * chevauchement de créneau (contrainte métier centrale du SRR).</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class ReservationDAO {

    /** DAO utilisé pour la vérification de disponibilité. */
    private final RessourceDAO ressourceDAO = new RessourceDAO();

    // -----------------------------------------------------------
    // Création
    // -----------------------------------------------------------

    /**
     * Crée une nouvelle réservation après vérification de disponibilité.
     *
     * <p>Si la ressource est déjà réservée sur le créneau demandé,
     * une {@link IllegalStateException} est levée et aucune insertion
     * n'est effectuée en base.</p>
     *
     * @param reservation la réservation à enregistrer
     * @throws SQLException          en cas d'erreur base de données
     * @throws IllegalStateException si un conflit de créneau est détecté
     */
    public void creer(Reservation reservation) throws SQLException {
        // Étape 1 : vérification de disponibilité (contrainte métier)
        boolean disponible = ressourceDAO.verifierDisponibilite(reservation.getRessource().getId(), reservation.getDateDebut(), reservation.getDateFin(), 0 );

        if (!disponible) {
            // Conflit détecté : on interrompt sans toucher la base
            throw new IllegalStateException("Conflit détecté : la ressource '" + reservation.getRessource().getNom() + "' est déjà réservée sur ce créneau.");
        }

        // Étape 2 : insertion en base (ressource disponible)
        String sql = """
                INSERT INTO reservation (id_utilisateur, id_ressource, date_debut, date_fin)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setInt(1, reservation.getUtilisateur().getId());
            ps.setInt(2, reservation.getRessource().getId());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getDateFin()));
            ps.executeUpdate();
        }
    }

    // -----------------------------------------------------------
    // Lecture
    // -----------------------------------------------------------

    /**
     * Retourne toutes les réservations (pour la vue planning administrateur).
     *
     * @return liste de toutes les réservations avec utilisateur et ressource
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Reservation> listerToutes() throws SQLException {
        String sql = """
                SELECT r.id, r.date_debut, r.date_fin,
                       u.id AS uid, u.login, u.role,
                       res.id AS rid, res.nom, res.type
                FROM reservation r
                JOIN utilisateur u   ON r.id_utilisateur = u.id
                JOIN ressource   res ON r.id_ressource   = res.id
                ORDER BY r.date_debut
                """;
        return executerRequeteListe(sql, null);
    }

    /**
     * Retourne uniquement les réservations d'un utilisateur donné.
     *
     * @param idUtilisateur identifiant de l'utilisateur
     * @return liste des réservations de cet utilisateur
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Reservation> listerParUtilisateur(int idUtilisateur) throws SQLException {
        String sql = """
                SELECT r.id, r.date_debut, r.date_fin,
                       u.id AS uid, u.login, u.role,
                       res.id AS rid, res.nom, res.type
                FROM reservation r
                JOIN utilisateur u   ON r.id_utilisateur = u.id
                JOIN ressource   res ON r.id_ressource   = res.id
                WHERE u.id = ?
                ORDER BY r.date_debut
                """;
        return executerRequeteListe(sql, idUtilisateur);
    }

    // -----------------------------------------------------------
    // Suppression
    // -----------------------------------------------------------

    /**
     * Annule (supprime) une réservation par son identifiant.
     *
     * <p>Le contrôleur doit avoir vérifié au préalable que l'utilisateur
     * connecté est bien le propriétaire ou un administrateur
     * ({@link com.techacademy.srr.util.SessionManager#peutModifier}).</p>
     *
     * @param idReservation identifiant de la réservation à supprimer
     * @throws SQLException en cas d'erreur base de données
     */
    public void annuler(int idReservation) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setInt(1, idReservation);
            ps.executeUpdate();
        }
    }

    // -----------------------------------------------------------
    // Méthode privée : construction des objets depuis le ResultSet
    // -----------------------------------------------------------

    /**
     * Exécute une requête SELECT et construit la liste de {@link Reservation}.
     *
     * @param sql          requête préparée avec éventuel paramètre entier
     * @param idParam      paramètre entier (ou {@code null} si aucun)
     * @return liste des réservations
     * @throws SQLException en cas d'erreur base de données
     */
    private List<Reservation> executerRequeteListe(String sql, Integer idParam)
            throws SQLException {

        List<Reservation> liste = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .getConnection().prepareStatement(sql)) {

            if (idParam != null) {
                ps.setInt(1, idParam);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // Reconstruction de l'utilisateur
                    Utilisateur u = new Utilisateur(
                            rs.getInt("uid"),
                            rs.getString("login"),
                            "", // hash non retourné pour minimiser l'exposition
                            rs.getString("role")
                    );

                    // Reconstruction de la ressource
                    Ressource res = new Ressource(rs.getInt("rid"), rs.getString("nom"), rs.getString("type"));

                    // Reconstruction de la réservation
                    Reservation r = new Reservation(rs.getInt("id"), u, res, rs.getTimestamp("date_debut").toLocalDateTime(), rs.getTimestamp("date_fin").toLocalDateTime());

                    liste.add(r);
                }
            }
        }
        return liste;
    }
}
