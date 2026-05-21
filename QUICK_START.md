// DrawingApp - Guide de Démarrage Rapide

## Installation

1. Assurez-vous d'avoir Java 17+ et Maven 3.8+ installés
2. Ouvrez un terminal dans le dossier `DrawingApp`
3. Exécutez:
   ```bash
   mvn clean compile
   mvn javafx:run
   ```

## Utilisation

### 1. Dessiner une forme
- Cliquez sur un bouton de forme (Rectangle, Cercle, Ligne)
- Cliquez et trainez sur le canvas pour dessiner

### 2. Sélectionner une stratégie de logging
- Utilisez le combo box "Logging"
- Options: Console, Fichier, Base de données

### 3. Annuler une action
- Cliquez sur "Annuler" pour revenir en arrière
- L'action annulée est enregistrée dans les logs

### 4. Supprimer une forme
- Sélectionnez une forme en cliquant dessus
- Cliquez sur "Supprimer"

### 5. Sauvegarder/Ouvrir
- Cliquez "Sauvegarder" pour persister le dessin
- Les dessins sont stockés dans `data/drawings.db`

## Logs

Selon la stratégie choisie:
- **Console**: Affichage en terminal
- **Fichier**: Sauvegardé dans `data/logs/drawing-actions.log`
- **Base de données**: Enregistré dans la table `logs` de SQLite

## Structure du Projet

```
src/main/java/com/drawingapp/
├── MainApp.java                 # Point d'entrée
├── controller/DrawingController # Logique de l'interface
├── model/                        # Modèles de formes
├── factory/ShapeFactory          # Factory Method
├── command/                      # Pattern Command
├── strategy/                     # Pattern Strategy
├── observer/                     # Pattern Observer
└── dao/DrawingDAO                # Accès base de données
```

## Patterns de Conception

- **Factory Method**: ShapeFactory crée les formes
- **Command**: Chaque action (dessin, suppression) est une commande
- **Strategy**: Stratégies de logging interchandeables
- **Observer**: Notification des actions pour le logging
- **DAO**: Isolation de l'accès à la base de données
- **MVC**: Architecture globale

Bon dessin!
