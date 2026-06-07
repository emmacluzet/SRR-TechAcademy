package com.techacademy.srr.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utilitaire de connexion à la base de données PostgreSQL.
 *
 * <p>Implémente le patron Singleton pour partager une unique connexion
 * pendant toute la durée de vie de l'application.
 * Les paramètres (URL, login, mot de passe) sont lus depuis le fichier
 * {@code config.properties} situé dans les ressources du projet.</p>
 *
 * @author Emma Cluzet
 * @version 1.0
 */
public class DatabaseConnection {

    /** Instance unique (Singleton). */
    private static DatabaseConnection instance;

    /** Connexion JDBC active. */
    private Connection connection;

    // -----------------------------------------------------------
    // Constructeur privé : charge la config et ouvre la connexion
    // -----------------------------------------------------------
    private DatabaseConnection() throws SQLException {
        Properties props = new Properties();

        // Lecture du fichier config.properties dans le classpath
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {

            if (in == null) {
                throw new SQLException("Fichier config.properties introuvable dans les ressources.");
            }
            props.load(in);

        } catch (IOException e) {
            throw new SQLException("Erreur lors de la lecture de config.properties : " + e.getMessage());
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // Ouverture de la connexion JDBC
        this.connection = DriverManager.getConnection(url, user, password);
    }

    // -----------------------------------------------------------
    // Méthode d'accès au Singleton
    // -----------------------------------------------------------

    /**
     * Retourne l'instance unique de {@code DatabaseConnection}.
     * Crée la connexion lors du premier appel.
     *
     * @return l'instance Singleton
     * @throws SQLException si la connexion échoue
     */
    public static DatabaseConnection getInstance() throws SQLException {
        // Réinitialise l'instance si la connexion a été fermée
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retourne l'objet {@link Connection} JDBC.
     *
     * @return la connexion active
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Ferme proprement la connexion à la base de données.
     * À appeler lors de la fermeture de l'application.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
