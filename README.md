# DrawingApp - Application JavaFX de Dessin

Une application de dessin moderne développée en JavaFX avec une architecture basée sur des patterns de conception.

## Fonctionnalités

- ✏️ **Dessin de formes** : Rectangle, Cercle, Ligne
- 🗑️ **Suppression de formes** : Supprimer une forme sélectionnée
- ↩️ **Annulation (Undo)** : Revenir à l'état précédent
- 📝 **Journalisation flexible** : Console, Fichier texte, ou Base de données
- 💾 **Sauvegarde/Ouverture** : Persister les dessins dans une base SQLite
- 📊 **Logging complet** : Suivi de toutes les actions utilisateur

## Architecture

### Patterns de Conception Utilisés

| Pattern | Utilisation |
|---------|------------|
| **Factory Method** | Création dynamique des formes (Rectangle, Circle, Line) |
| **Command** | Encapsulation des actions de dessin, suppression, annulation |
| **Strategy** | Sélection de la stratégie de journalisation (Console/Fichier/BD) |
| **Observer** | Notification du système de logging lors des actions |
| **DAO** | Accès à la base de données |
| **MVC** | Architecture générale (Model/View/Controller) |

### Structure du Projet

```
src/main/java/com/drawingapp/
├── MainApp.java                      # Point d'entrée de l'application
├── controller/
│   └── DrawingController.java        # Contrôleur principal
├── model/
│   ├── Shape.java                    # Interface pour les formes
│   ├── RectangleShape.java           # Implémentation Rectangle
│   ├── CircleShape.java              # Implémentation Cercle
│   └── LineShape.java                # Implémentation Ligne
├── factory/
│   └── ShapeFactory.java             # Fabrique de formes
├── command/
│   ├── Command.java                  # Interface Command
│   ├── DrawCommand.java              # Commande de dessin
│   ├── DeleteCommand.java            # Commande de suppression
│   └── UndoManager.java              # Gestionnaire d'annulation
├── strategy/
│   ├── LogStrategy.java              # Interface Strategy
│   ├── ConsoleLogStrategy.java       # Logging console
│   ├── FileLogStrategy.java          # Logging fichier
│   └── DatabaseLogStrategy.java      # Logging base de données
├── observer/
│   ├── ActionObservable.java         # Observable
│   └── ActionObserver.java           # Observer
├── dao/
│   └── DrawingDAO.java               # Accès à la base de données
└── view/
    └── drawing-view.fxml             # Interface FXML
```

## Installation

### Prérequis

- Java 17 ou supérieur
- Maven 3.8+

### Compilation et Exécution

```bash
# Cloner/télécharger le projet
cd DrawingApp

# Compiler
mvn clean compile

# Exécuter
mvn javafx:run
```

## Utilisation

1. **Sélectionner une forme** : Cliquez sur un bouton (Rectangle, Cercle, Ligne)
2. **Dessiner** : Cliquez et trainez dans la zone de dessin
3. **Annuler** : Cliquez sur le bouton "Annuler"
4. **Supprimer** : Sélectionnez une forme et cliquez "Supprimer"
5. **Journaliser** : Choisissez la stratégie (Console, Fichier, BD)
6. **Sauvegarder** : Enregistrez votre dessin
7. **Ouvrir** : Chargez un dessin existant

## Base de Données

L'application utilise **SQLite** pour la persistence. La base est créée automatiquement au premier lancement dans le dossier `data/`.

## Logging

Les logs sont générés selon la stratégie choisie :
- **Console** : Affichage direct en sortie standard
- **Fichier** : Stockage dans `data/logs/drawing-actions.log`
- **Base de données** : Enregistrement dans la table `logs` de SQLite

## Auteur

Mini-projet JavaFX avec patterns de conception

## Licence

Ce projet est fourni à titre éducatif.
