package com.techacademy.srr.dao;

import com.techacademy.srr.model.Ressource;
import com.techacademy.srr.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table {@code ressource}.
 *
 * <p>Fournit les opérations CRUD sur les ressources pédagogiques,
 * ainsi que la vérification de disponibilité qui constitue la
 * contrainte métier centrale de l'application SRR.</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class RessourceDAO {

    // -----------------------------------------------------------
    // Lecture
    // -----------------------------------------------------------

    /**
     * Retourne toutes les ressources enregistrées.
     *
     * @return liste de toutes les ressources
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Ressource> listerToutes() throws SQLException {
        List<Ressource> liste = new ArrayList<>();
        String sql = "SELECT id, nom, type FROM ressource ORDER BY type, nom";

        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(new Ressource( rs.getInt("id"),rs.getString("nom"), rs.getString("type")));
            }
        }
        return liste;
    }

    /**
     * Recherche une ressource par son identifiant.
     *
     * @param id l'identifiant à rechercher
     * @return la ressource, ou {@code null} si introuvable
     * @throws SQLException en cas d'erreur base de données
     */
    public Ressource trouverParId(int id) throws SQLException {
        String sql = "SELECT id, nom, type FROM ressource WHERE id = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ressource(rs.getInt("id"),rs.getString("nom"),rs.getString("type"));
                }
            }
        }
        return null;
    }

    // -----------------------------------------------------------
    // Vérification de disponibilité (contrainte métier centrale)
    // -----------------------------------------------------------

    /**
     * Vérifie qu'une ressource est disponible sur l'intégralité du créneau demandé.
     *
     * <p>La requête SQL détecte tout chevauchement avec une réservation existante
     * en utilisant la logique : un conflit existe si et seulement si
     * {@code debut_existant < fin_demandee} ET {@code fin_existante > debut_demandee}.
     * On peut exclure une réservation en cours de modification via {@code excludeId}.</p>
     *
     * @param idRessource identifiant de la ressource à tester
     * @param debut       date/heure de début souhaitée
     * @param fin         date/heure de fin souhaitée
     * @param excludeId   identifiant d'une réservation à exclure (0 pour aucune)
     * @return {@code true} si la ressource est disponible, {@code false} sinon
     * @throws SQLException en cas d'erreur base de données
     */
    public boolean verifierDisponibilite(int idRessource,LocalDateTime debut, LocalDateTime fin, int excludeId) throws SQLException {

        // Détecte tout chevauchement de créneau (hors réservation exclue)
        String sql = """
                SELECT COUNT(*) FROM reservation
                WHERE id_ressource = ?
                  AND id           != ?
                  AND date_debut   <  ?
                  AND date_fin     >  ?
                """;

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setInt(1, idRessource);
            ps.setInt(2, excludeId);
            ps.setTimestamp(3, Timestamp.valueOf(fin));
            ps.setTimestamp(4, Timestamp.valueOf(debut));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si le compteur est 0 → aucun conflit : ressource disponible
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    // -----------------------------------------------------------
    // Création / Modification / Suppression (ADMIN uniquement)
    // -----------------------------------------------------------

    /**
     * Ajoute une nouvelle ressource.
     *
     * @param nom  nom de la ressource
     * @param type catégorie (SALLE, PROJECTEUR, ORDINATEUR…)
     * @throws SQLException en cas d'erreur base de données
     */
    public void creer(String nom, String type) throws SQLException {
        String sql = "INSERT INTO ressource (nom, type) VALUES (?, ?)";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, type);
            ps.executeUpdate();
        }
    }

    /**
     * Met à jour le nom et le type d'une ressource existante.
     *
     * @param id   identifiant de la ressource à modifier
     * @param nom  nouveau nom
     * @param type nouveau type
     * @throws SQLException en cas d'erreur base de données
     */
    public void modifier(int id, String nom, String type) throws SQLException {
        String sql = "UPDATE ressource SET nom = ?, type = ? WHERE id = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, type);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    /**
     * Supprime une ressource et toutes ses réservations associées (CASCADE).
     *
     * @param id identifiant de la ressource à supprimer
     * @throws SQLException en cas d'erreur base de données
     */
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM ressource WHERE id = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
