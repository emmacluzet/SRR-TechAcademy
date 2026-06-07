package com.techacademy.srr;

import com.techacademy.srr.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Point d'entrée de l'application SRR (Système de Réservation de Ressources).
 *
 * <p>Lance l'interface JavaFX et affiche la fenêtre de connexion au démarrage.
 * Ferme proprement la connexion à la base de données à la fermeture.</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class MainApp extends Application {

    /**
     * Méthode principale JavaFX : initialise et affiche la fenêtre de connexion.
     *
     * @param stage la fenêtre principale (fournie par le runtime JavaFX)
     * @throws IOException si le fichier FXML est introuvable
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Chargement de la vue de connexion
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/techacademy/srr/view/Login.fxml")
        );

        Scene scene = new Scene(loader.load(), 400, 300);

        stage.setTitle("SRR – Tech-Academy : Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Ferme la connexion JDBC à la fermeture de l'application.
     * Appelé automatiquement par JavaFX lors du cycle de vie.
     */
    @Override
    public void stop() {
        try {
            DatabaseConnection.getInstance().closeConnection();
        } catch (Exception e) {
            System.err.println("Erreur fermeture connexion : " + e.getMessage());
        }
    }

    // -----------------------------------------------------------
    // Utilitaire : chargement des scènes depuis les contrôleurs
    // -----------------------------------------------------------

    /**
     * Charge et affiche une nouvelle scène FXML dans la fenêtre donnée.
     *
     * @param stage    la fenêtre cible
     * @param fxmlPath chemin du fichier FXML (relatif aux ressources)
     * @param titre    titre de la fenêtre
     * @throws IOException si le FXML est introuvable
     */
    public static void chargerScene(Stage stage, String fxmlPath, String titre)
            throws IOException {

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource(fxmlPath)
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle(titre);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Point d'entrée Java standard.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
