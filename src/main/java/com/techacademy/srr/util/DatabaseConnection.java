package com.techacademy.srr.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton qui gère la connexion JDBC à la base de données.
 *
 * Les paramètres de connexion sont lus depuis le fichier config.properties
 * placé dans src/main/resources. Ce fichier ne doit jamais être versionné
 * (voir .gitignore) ; seul config.properties.example l'est.
 *
 * Usage : Connection conn = DatabaseConnection.getInstance().getConnection();
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        // Chargement silencieux : les erreurs sont remontées via les DAO
    }

    /**
     * Retourne l'instance unique (pattern Singleton).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retourne une connexion active. Si la connexion est fermée ou nulle,
     * elle est ouverte à nouveau à partir des paramètres de config.properties.
     *
     * @throws SQLException si la connexion échoue
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = openConnection();
        }
        return connection;
    }

    /* Ouvre une nouvelle connexion en lisant config.properties. */
    private Connection openConnection() throws SQLException {
        Properties config = loadConfig();
        String url      = config.getProperty("db.url");
        String user     = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException(
                "Paramètres de connexion manquants dans config.properties. " +
                "Vérifiez que le fichier existe dans src/main/resources."
            );
        }

        return DriverManager.getConnection(url, user, password);
    }

    /* Charge les propriétés depuis le classpath. */
    private Properties loadConfig() throws SQLException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new SQLException(
                    "Fichier config.properties introuvable dans le classpath. " +
                    "Copiez config.properties.example en config.properties et renseignez vos valeurs."
                );
            }
            props.load(is);
        } catch (IOException e) {
            throw new SQLException("Erreur lors de la lecture de config.properties : " + e.getMessage(), e);
        }
        return props;
    }

    /* Ferme proprement la connexion (à appeler à l'arrêt de l'application).*/
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
