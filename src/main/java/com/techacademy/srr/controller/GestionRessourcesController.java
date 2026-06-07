package com.techacademy.srr.controller;

import com.techacademy.srr.MainApp;
import com.techacademy.srr.dao.RessourceDAO;
import com.techacademy.srr.model.Ressource;
import com.techacademy.srr.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Contrôleur MVC de la vue de gestion du catalogue ({@code GestionRessources.fxml}).
 *
 * <p>Réservée aux administrateurs. Permet de :</p>
 * <ul>
 *   <li>Lister toutes les ressources.</li>
 *   <li>Ajouter une nouvelle ressource (nom + type).</li>
 *   <li>Modifier une ressource sélectionnée.</li>
 *   <li>Supprimer une ressource (et ses réservations par CASCADE).</li>
 * </ul>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class GestionRessourcesController implements Initializable {

    // -----------------------------------------------------------
    // Composants FXML
    // -----------------------------------------------------------

    /** Tableau listant toutes les ressources. */
    @FXML private ListView<Ressource>   listRessources;

    /** Champ de saisie du nom de la ressource. */
    @FXML private TextField             champNom;

    /** Champ de saisie du type de la ressource. */
    @FXML private TextField             champType;

    /** Label de messages d'information ou d'erreur. */
    @FXML private Label                 messageLabel;

    /** DAO ressource. */
    private final RessourceDAO ressourceDAO = new RessourceDAO();

    // -----------------------------------------------------------
    // Initialisation
    // -----------------------------------------------------------

    /**
     * Vérifie les droits d'accès et charge la liste des ressources.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Sécurité : cette vue est ADMIN uniquement
        if (!SessionManager.estAdmin()) {
            messageLabel.setText("Accès non autorisé.");
            return;
        }
        chargerRessources();

        // Pré-remplissage du formulaire lors d'une sélection dans la liste
        listRessources.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancien, nouveau) -> {
                    if (nouveau != null) {
                        champNom.setText(nouveau.getNom());
                        champType.setText(nouveau.getType());
                    }
                }
        );
    }

    // -----------------------------------------------------------
    // Actions CRUD
    // -----------------------------------------------------------

    /**
     * Ajoute une nouvelle ressource avec les valeurs saisies dans le formulaire.
     */
    @FXML
    private void handleAjouter() {
        String nom  = champNom.getText().trim();
        String type = champType.getText().trim().toUpperCase();

        if (nom.isEmpty() || type.isEmpty()) {
            messageLabel.setText("Veuillez saisir un nom et un type.");
            return;
        }

        try {
            ressourceDAO.creer(nom, type);
            messageLabel.setText("Ressource '" + nom + "' ajoutée.");
            viderFormulaire();
            chargerRessources();

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de l'ajout.");
            System.err.println("SQL handleAjouter : " + e.getMessage());
        }
    }

    /**
     * Modifie la ressource sélectionnée dans la liste avec les nouvelles valeurs.
     */
    @FXML
    private void handleModifier() {
        Ressource selected = listRessources.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setText("Sélectionnez une ressource à modifier.");
            return;
        }

        String nom  = champNom.getText().trim();
        String type = champType.getText().trim().toUpperCase();

        if (nom.isEmpty() || type.isEmpty()) {
            messageLabel.setText("Veuillez saisir un nom et un type.");
            return;
        }

        try {
            ressourceDAO.modifier(selected.getId(), nom, type);
            messageLabel.setText("Ressource modifiée avec succès.");
            viderFormulaire();
            chargerRessources();

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la modification.");
            System.err.println("SQL handleModifier : " + e.getMessage());
        }
    }

    /**
     * Supprime la ressource sélectionnée, après confirmation de l'utilisateur.
     */
    @FXML
    private void handleSupprimer() {
        Ressource selected = listRessources.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setText("Sélectionnez une ressource à supprimer.");
            return;
        }

        // Demande de confirmation avant suppression définitive
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer '" + selected.getNom() + "' ?");
        confirmation.setContentText(
                "Toutes les réservations associées seront également supprimées."
        );

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                try {
                    ressourceDAO.supprimer(selected.getId());
                    messageLabel.setText("Ressource supprimée.");
                    viderFormulaire();
                    chargerRessources();

                } catch (SQLException e) {
                    messageLabel.setText("Erreur lors de la suppression.");
                    System.err.println("SQL handleSupprimer : " + e.getMessage());
                }
            }
        });
    }

    /**
     * Retourne à la vue du planning.
     */
    @FXML
    private void handleRetour() {
        try {
            Stage stage = (Stage) listRessources.getScene().getWindow();
            MainApp.chargerScene(
                    stage,
                    "/com/techacademy/srr/view/Planning.fxml",
                    "SRR – Tech-Academy : Planning"
            );
        } catch (Exception e) {
            messageLabel.setText("Impossible de revenir au planning.");
        }
    }

    // -----------------------------------------------------------
    // Méthodes privées
    // -----------------------------------------------------------

    /** Recharge la liste des ressources depuis la base de données. */
    private void chargerRessources() {
        try {
            listRessources.setItems(
                    FXCollections.observableArrayList(ressourceDAO.listerToutes())
            );
        } catch (SQLException e) {
            messageLabel.setText("Impossible de charger les ressources.");
            System.err.println("SQL chargerRessources : " + e.getMessage());
        }
    }

    /** Vide les champs du formulaire après une opération CRUD. */
    private void viderFormulaire() {
        champNom.clear();
        champType.clear();
        listRessources.getSelectionModel().clearSelection();
    }
}
