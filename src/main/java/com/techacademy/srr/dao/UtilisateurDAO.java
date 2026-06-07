package com.techacademy.srr.dao;

import com.techacademy.srr.model.Utilisateur;
import com.techacademy.srr.util.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) pour la table {@code utilisateur}.
 *
 * <p>Isole toutes les requêtes SQL liées aux utilisateurs.
 * Les mots de passe sont systématiquement hachés avec BCrypt
 * avant d'être stockés en base.</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class UtilisateurDAO {

    // -----------------------------------------------------------
    // Authentification
    // -----------------------------------------------------------

    /**
     * Tente d'authentifier un utilisateur.
     *
     * <p>Recherche l'utilisateur par login, puis vérifie que le mot de passe
     * fourni correspond au hash BCrypt stocké en base.</p>
     *
     * @param login       identifiant saisi dans le formulaire
     * @param motDePasse  mot de passe saisi en clair
     * @return un {@link Optional} contenant l'utilisateur si les identifiants
     *         sont corrects, ou vide si la connexion échoue
     * @throws SQLException en cas d'erreur base de données
     */
    public Optional<Utilisateur> authentifier(String login, String motDePasse) throws SQLException {
        String sql = "SELECT id, login, password, role FROM utilisateur WHERE login = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .getConnection().prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashStocke = rs.getString("password");

                    // Vérification BCrypt : compare le clair et le hash
                    if (BCrypt.checkpw(motDePasse, hashStocke)) {
                        Utilisateur u = new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("login"),
                                hashStocke,
                                rs.getString("role")
                        );
                        return Optional.of(u);
                    }
                }
            }
        }
        // Login inconnu ou mot de passe incorrect
        return Optional.empty();
    }

    // -----------------------------------------------------------
    // CRUD (réservé aux administrateurs)
    // -----------------------------------------------------------

    /**
     * Retourne la liste de tous les utilisateurs.
     *
     * @return liste (éventuellement vide)
     * @throws SQLException en cas d'erreur base de données
     */
    public List<Utilisateur> listerTous() throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT id, login, password, role FROM utilisateur ORDER BY login";

        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        }
        return liste;
    }

    /**
     * Crée un nouvel utilisateur en base.
     *
     * <p>Le mot de passe en clair est haché avant l'insertion.</p>
     *
     * @param login      identifiant souhaité
     * @param motDePasse mot de passe en clair (sera haché)
     * @param role       "ADMIN" ou "USER"
     * @throws SQLException si le login existe déjà ou erreur base
     */
    public void creer(String login, String motDePasse, String role) throws SQLException {
        // Hachage du mot de passe avec BCrypt (10 rounds)
        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt(10));

        String sql = "INSERT INTO utilisateur (login, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .getConnection().prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.executeUpdate();
        }
    }

    /**
     * Supprime un utilisateur (et ses réservations, par CASCADE).
     *
     * @param id identifiant de l'utilisateur à supprimer
     * @throws SQLException en cas d'erreur base de données
     */
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .getConnection().prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
