package com.techacademy.srr.controller;

import com.techacademy.srr.MainApp;
import com.techacademy.srr.dao.ReservationDAO;
import com.techacademy.srr.dao.RessourceDAO;
import com.techacademy.srr.model.Reservation;
import com.techacademy.srr.model.Ressource;
import com.techacademy.srr.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur MVC de la vue principale ({@code Planning.fxml}).
 *
 * <p>Permet à l'utilisateur connecté de :</p>
 * <ul>
 *   <li>Consulter le planning des réservations (les siennes ou toutes si ADMIN).</li>
 *   <li>Réserver une ressource sur un créneau donné.</li>
 *   <li>Annuler une de ses propres réservations (ou toutes si ADMIN).</li>
 * </ul>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class PlanningController implements Initializable {

    // -----------------------------------------------------------
    // Composants FXML - Tableau du planning
    // -----------------------------------------------------------

    /** Tableau affichant la liste des réservations. */
    @FXML private TableView<Reservation>          tableReservations;
    @FXML private TableColumn<Reservation, String> colRessource;
    @FXML private TableColumn<Reservation, String> colUtilisateur;
    @FXML private TableColumn<Reservation, String> colDebut;
    @FXML private TableColumn<Reservation, String> colFin;

    // -----------------------------------------------------------
    // Composants FXML - Formulaire de réservation
    // -----------------------------------------------------------

    /** Liste déroulante des ressources disponibles. */
    @FXML private ComboBox<Ressource>    comboRessource;

    /** Sélecteur de date de début. */
    @FXML private DatePicker             dateDebut;

    /** Heure de début (format HH:mm). */
    @FXML private TextField              heureDebut;

    /** Sélecteur de date de fin. */
    @FXML private DatePicker             dateFin;

    /** Heure de fin (format HH:mm). */
    @FXML private TextField              heureFin;

    /** Label pour les messages d'information ou d'erreur. */
    @FXML private Label                  messageLabel;

    /** Label indiquant l'utilisateur connecté et son rôle. */
    @FXML private Label                  labelUtilisateur;

    // -----------------------------------------------------------
    // DAOs
    // -----------------------------------------------------------

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RessourceDAO   ressourceDAO   = new RessourceDAO();

    // -----------------------------------------------------------
    // Initialisation (appelée automatiquement par JavaFX)
    // -----------------------------------------------------------

    /**
     * Initialise les colonnes du tableau et charge les données.
     * Appelé automatiquement après le chargement du FXML.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Affichage de l'utilisateur connecté
        if (SessionManager.estConnecte()) {
            labelUtilisateur.setText(
                    "Connecté : " + SessionManager.getUtilisateurConnecte().getLogin()
                    + " (" + SessionManager.getUtilisateurConnecte().getRole() + ")"
            );
        }

        // Configuration des colonnes du tableau
        // On utilise des cellFactories personnalisées pour accéder aux objets imbriqués
        colRessource.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getRessource().getNom()
                        + " (" + data.getValue().getRessource().getType() + ")"
                )
        );
        colUtilisateur.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUtilisateur().getLogin()
                )
        );
        colDebut.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDateDebut().toString().replace("T", " ")
                )
        );
        colFin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDateFin().toString().replace("T", " ")
                )
        );

        // Chargement des données initiales
        chargerRessources();
        chargerPlanning();
    }

    // -----------------------------------------------------------
    // Actions utilisateur
    // -----------------------------------------------------------

    /**
     * Déclenché par le clic sur "Réserver".
     * Lit le formulaire, valide les saisies, puis appelle le DAO.
     */
    @FXML
    private void handleReserver() {
        // Récupération des valeurs du formulaire
        Ressource ressource    = comboRessource.getValue();
        LocalDate dateD        = dateDebut.getValue();
        LocalDate dateF        = dateFin.getValue();
        String    heureDebutTxt = heureDebut.getText().trim();
        String    heureFinTxt   = heureFin.getText().trim();

        // Validation : tous les champs obligatoires
        if (ressource == null || dateD == null || dateF == null
                || heureDebutTxt.isEmpty() || heureFinTxt.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        LocalDateTime debut;
        LocalDateTime fin;

        try {
            // Parsing des heures saisies (format HH:mm)
            LocalTime tDebut = LocalTime.parse(heureDebutTxt);
            LocalTime tFin   = LocalTime.parse(heureFinTxt);
            debut = LocalDateTime.of(dateD, tDebut);
            fin   = LocalDateTime.of(dateF, tFin);
        } catch (Exception e) {
            messageLabel.setText("Format d'heure invalide. Utilisez HH:mm (ex: 09:00).");
            return;
        }

        // Validation métier : fin après début
        if (!fin.isAfter(debut)) {
            messageLabel.setText("La date de fin doit être postérieure à la date de début.");
            return;
        }

        // Construction de l'objet Reservation
        Reservation reservation = new Reservation(
                0, // L'id sera attribué par la base
                SessionManager.getUtilisateurConnecte(),
                ressource,
                debut,
                fin
        );

        try {
            // Appel DAO : vérifie la disponibilité et insère si OK
            reservationDAO.creer(reservation);
            messageLabel.setText("Réservation confirmée !");
            chargerPlanning(); // Rafraîchissement du tableau

        } catch (IllegalStateException e) {
            // Conflit de créneau détecté par le DAO
            messageLabel.setText(e.getMessage());

        } catch (SQLException e) {
            messageLabel.setText("Erreur base de données : " + e.getMessage());
            System.err.println("SQL PlanningController.handleReserver : " + e.getMessage());
        }
    }

    /**
     * Déclenché par le clic sur "Annuler la réservation".
     * Supprime la ligne sélectionnée dans le tableau, sous réserve d'autorisation.
     */
    @FXML
    private void handleAnnuler() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setText("Sélectionnez une réservation à annuler.");
            return;
        }

        // Vérification : l'utilisateur peut-il modifier cette réservation ?
        if (!SessionManager.peutModifier(selected.getUtilisateur().getId())) {
            messageLabel.setText("Vous ne pouvez annuler que vos propres réservations.");
            return;
        }

        try {
            reservationDAO.annuler(selected.getId());
            messageLabel.setText("Réservation annulée.");
            chargerPlanning();

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de l'annulation.");
            System.err.println("SQL PlanningController.handleAnnuler : " + e.getMessage());
        }
    }

    /**
     * Déclenché par le clic sur "Gérer les ressources" (ADMIN uniquement).
     * Charge la vue de gestion du catalogue.
     */
    @FXML
    private void handleGererRessources() {
        if (!SessionManager.estAdmin()) {
            messageLabel.setText("Accès réservé aux administrateurs.");
            return;
        }
        try {
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            MainApp.chargerScene(
                    stage,
                    "/com/techacademy/srr/view/GestionRessources.fxml",
                    "SRR – Gestion des ressources"
            );
        } catch (Exception e) {
            messageLabel.setText("Impossible d'ouvrir la vue de gestion.");
        }
    }

    /**
     * Déclenché par le clic sur "Déconnexion".
     * Ferme la session et revient à l'écran de connexion.
     */
    @FXML
    private void handleDeconnexion() {
        SessionManager.fermer();
        try {
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            MainApp.chargerScene(
                    stage,
                    "/com/techacademy/srr/view/Login.fxml",
                    "SRR – Tech-Academy : Connexion"
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    // -----------------------------------------------------------
    // Méthodes privées : chargement des données
    // -----------------------------------------------------------

    /**
     * Charge la liste des ressources dans la ComboBox.
     */
    private void chargerRessources() {
        try {
            List<Ressource> ressources = ressourceDAO.listerToutes();
            comboRessource.setItems(FXCollections.observableArrayList(ressources));
        } catch (SQLException e) {
            messageLabel.setText("Impossible de charger les ressources.");
            System.err.println("SQL chargerRessources : " + e.getMessage());
        }
    }

    /**
     * Charge le planning dans le tableau :
     * toutes les réservations si ADMIN, seulement les siennes sinon.
     */
    private void chargerPlanning() {
        try {
            List<Reservation> liste;

            if (SessionManager.estAdmin()) {
                // L'administrateur voit tout le planning
                liste = reservationDAO.listerToutes();
            } else {
                // L'utilisateur standard ne voit que ses propres réservations
                liste = reservationDAO.listerParUtilisateur(
                        SessionManager.getUtilisateurConnecte().getId()
                );
            }

            tableReservations.setItems(FXCollections.observableArrayList(liste));

        } catch (SQLException e) {
            messageLabel.setText("Impossible de charger le planning.");
            System.err.println("SQL chargerPlanning : " + e.getMessage());
        }
    }
}
