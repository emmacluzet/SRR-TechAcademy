package com.techacademy.srr.controller;

import com.techacademy.srr.MainApp;
import com.techacademy.srr.dao.UtilisateurDAO;
import com.techacademy.srr.model.Utilisateur;
import com.techacademy.srr.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Contrôleur MVC de la vue de connexion ({@code Login.fxml}).
 *
 * <p>Gère le formulaire d'authentification :
 * <ol>
 *   <li>Récupère login et mot de passe depuis les champs FXML.</li>
 *   <li>Délègue la vérification à {@link UtilisateurDAO#authentifier}.</li>
 *   <li>Si succès, ouvre la session via {@link SessionManager} et charge
 *       la vue principale adaptée au rôle de l'utilisateur.</li>
 *   <li>Si échec, affiche un message d'erreur.</li>
 * </ol>
 * </p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class LoginController {

    // -----------------------------------------------------------
    // Composants FXML injectés automatiquement par JavaFX
    // -----------------------------------------------------------

    /** Champ de saisie de l'identifiant. */
    @FXML private TextField loginField;

    /** Champ de saisie du mot de passe (masqué). */
    @FXML private PasswordField passwordField;

    /** Label d'affichage des messages d'erreur. */
    @FXML private Label messageLabel;

    /** DAO pour la vérification des identifiants. */
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    // -----------------------------------------------------------
    // Actions utilisateur
    // -----------------------------------------------------------

    /**
     * Déclenché par le clic sur le bouton "Se connecter".
     *
     * <p>Valide les champs, authentifie l'utilisateur et redirige vers
     * la vue principale si les identifiants sont corrects.</p>
     */
    @FXML
    private void handleConnexion() {
        String login    = loginField.getText().trim();
        String password = passwordField.getText();

        // Validation : champs non vides
        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Optional<Utilisateur> optUtilisateur = utilisateurDAO.authentifier(login, password);

            if (optUtilisateur.isPresent()) {
                // Authentification réussie : ouverture de session
                SessionManager.ouvrir(optUtilisateur.get());

                // Redirection vers la vue principale
                Stage stage = (Stage) loginField.getScene().getWindow();
                MainApp.chargerScene(
                        stage,
                        "/com/techacademy/srr/view/Planning.fxml",
                        "SRR – Tech-Academy : Planning"
                );

            } else {
                // Identifiants incorrects : message générique (sécurité)
                messageLabel.setText("Identifiant ou mot de passe incorrect.");
                passwordField.clear();
            }

        } catch (SQLException e) {
            messageLabel.setText("Erreur de connexion à la base de données.");
            System.err.println("Erreur SQL LoginController : " + e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("Erreur inattendue. Contactez l'administrateur.");
            System.err.println("Erreur LoginController : " + e.getMessage());
        }
    }
}
