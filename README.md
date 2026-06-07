# SRR – Système de Réservation de Ressources
**Tech-Academy | BTS SIO SLAM – Session 2026**  
Candidat : Emma Cluzet – N° 02544744221

---

## Présentation

Application desktop Java permettant la réservation sans chevauchement de créneaux
pour les ressources pédagogiques de Tech-Academy (salles, projecteurs, ordinateurs).

---

## Stack technique

| Couche         | Technologie         |
|----------------|---------------------|
| Langage        | Java 17 (JDK)       |
| Interface      | JavaFX 17 + FXML    |
| Persistance    | JDBC + PostgreSQL   |
| Sécurité mots de passe | BCrypt (jbcrypt 0.4) |
| Build          | Maven 3             |
| Versionnage    | Git / GitHub        |

---

## Structure du projet

```
SRR/
├── pom.xml                          ← Dépendances Maven
└── src/main/
    ├── java/com/techacademy/srr/
    │   ├── MainApp.java             ← Point d'entrée JavaFX
    │   ├── model/
    │   │   ├── Utilisateur.java
    │   │   ├── Ressource.java
    │   │   └── Reservation.java
    │   ├── dao/
    │   │   ├── UtilisateurDAO.java
    │   │   ├── RessourceDAO.java    ← Contient verifierDisponibilite()
    │   │   └── ReservationDAO.java
    │   ├── controller/
    │   │   ├── LoginController.java
    │   │   ├── PlanningController.java
    │   │   └── GestionRessourcesController.java
    │   └── util/
    │       ├── DatabaseConnection.java  ← Singleton JDBC
    │       └── SessionManager.java
    └── resources/
        ├── config.properties            ← Paramètres BDD (à configurer)
        ├── db/
        │   ├── schema.sql               ← Création des tables
        │   └── data.sql                 ← Données de test
        └── com/techacademy/srr/view/
            ├── Login.fxml
            ├── Planning.fxml
            └── GestionRessources.fxml
```

---

## Installation et configuration

### 1. Prérequis

- Java 17 (JDK)
- Maven 3.8+
- PostgreSQL 14+

### 2. Créer la base de données

```bash
psql -U postgres -c "CREATE DATABASE srr_db;"
psql -U postgres -d srr_db -f src/main/resources/db/schema.sql
psql -U postgres -d srr_db -f src/main/resources/db/data.sql
```

### 3. Configurer la connexion

Éditer `src/main/resources/config.properties` :

```properties
db.url=jdbc:postgresql://localhost:5432/srr_db
db.user=postgres
db.password=votre_mot_de_passe
```

### 4. Lancer l'application

```bash
mvn javafx:run
```

---

## Comptes de test

| Login | Mot de passe | Rôle  |
|-------|-------------|-------|
| admin | admin123    | ADMIN |
| user1 | user123     | USER  |
| user2 | user123     | USER  |

---

## Contrainte métier principale

Avant toute insertion en base, `RessourceDAO.verifierDisponibilite()` exécute :

```sql
SELECT COUNT(*) FROM reservation
WHERE id_ressource = ?
  AND id           != ?
  AND date_debut   <  ?   -- fin demandée
  AND date_fin     >  ?   -- début demandé
```

Si le compteur est > 0, un conflit est détecté et la réservation est refusée.

---

## Liens

- Code source : https://github.com/emmacluzet/SSR-TechAcademy
- Portfolio   : https://emmacluzet.github.io/portfolio/#projet
