# Système de Réservation de Ressources

Ce projet est une application de bureau développée en Java dans le cadre d'un examen. L'idée de départ est simple : Tech-Academy gérait ses salles de réunion et son matériel pédagogique sur un cahier papier, ce qui donnait inévitablement lieu à des doubles réservations et à des conflits de planning. L'objectif de cette application est de centraliser tout ça proprement, avec une interface graphique et une vraie vérification des disponibilités.

## Ce que fait l'application

L'application permet à deux types d'utilisateurs de travailler ensemble sur le même stock de ressources.

Un utilisateur standard peut consulter les ressources disponibles (salles, vidéoprojecteurs, ordinateurs portables), réserver l'une d'elles sur un créneau horaire précis, et annuler ses propres réservations. Si une ressource est déjà prise sur tout ou partie du créneau demandé, la réservation est bloquée, c'est la contrainte métier centrale du projet.

Un administrateur a en plus la main sur le catalogue de ressources : il peut en ajouter, les modifier, les supprimer. Il voit aussi l'ensemble des réservations de tous les utilisateurs.

## Stack technique

Le projet tourne sur Java 17 avec JavaFX pour l'interface graphique (vues en `.fxml`). La persistance est gérée via JDBC sur une base MySQL ou PostgreSQL. Le tout est construit avec Maven et suit une architecture MVC avec des classes DAO pour toutes les interactions avec la base.

## Lancer le projet

Prérequis : Java 17+, Maven, et une instance MySQL ou PostgreSQL qui tourne en local.

Commencer par créer la base de données et exécuter les deux scripts SQL qui se trouvent dans le dossier `sql/` :

```
schema.sql   -- crée les tables
data.sql     -- insère les données de test
```

Ensuite, copier le fichier `src/main/resources/config.properties.example` en `config.properties` dans le même dossier, et renseigner les paramètres de connexion :

```
db.url=jdbc:mysql://localhost:3306/srr
db.user=votre_utilisateur
db.password=votre_mot_de_passe
```

Puis lancer l'application :

```
mvn clean javafx:run
```

## Comptes de test

Une fois le `data.sql` exécuté, les comptes suivants sont disponibles pour la démonstration :

| Login       | Mot de passe | Rôle          |
|-------------|--------------|---------------|
| admin       | admin123     | Administrateur |
| alice       | alice123     | Utilisateur   |
| bob         | bob123       | Utilisateur   |

## Structure du projet

```
src/
  main/
    java/
      com/techacademy/srr/
        model/          -- classes métier (Utilisateur, Ressource, Reservation)
        dao/            -- interfaces et implémentations DAO
        controller/     -- controllers JavaFX
        util/           -- SessionManager, DatabaseConnection, etc.
    resources/
      com/techacademy/srr/
        view/           -- fichiers .fxml
      config.properties
sql/
  schema.sql
  data.sql
docs/
  diagramme-cas-utilisation.png
  diagramme-classes.png
  mld.png
```

## Livrables du jury

Les trois documents demandés (diagramme de cas d'utilisation, diagramme de classes, schéma relationnel) sont disponibles dans le dossier `docs/`.

## Auteur

Emma Cluzet
