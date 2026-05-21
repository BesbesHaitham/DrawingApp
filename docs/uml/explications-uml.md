# Explications UML - DrawingApp

## 1. Cas d'utilisation general
- Fichier: `01-cas-utilisation-general.puml`
- Role: presenter les interactions principales entre l'utilisateur et l'application.
- Pattern: vue fonctionnelle globale (pas un GoF pattern specifique).

## 2. Classes global simplifie
- Fichier: `02-classes-global-simplifie.puml`
- Role: donner une vue d'architecture des principaux composants et dependances.
- Pattern: vue structurelle transversale.

## 3. Pattern MVC
- Fichier: `03-pattern-mvc.puml`
- Role: montrer la separation Vue / Controleur / Modele dans JavaFX.
- Pattern: MVC (Model-View-Controller).

## 4. Pattern Factory - ShapeFactory
- Fichier: `04-pattern-factory-shapefactory.puml`
- Role: illustrer la centralisation de creation des formes graphiques.
- Pattern: Factory.

## 5. Pattern Command - Undo/Redo
- Fichier: `05-pattern-command-undo-redo.puml`
- Role: expliquer l'encapsulation des actions pour annulation/refaire.
- Pattern: Command.

## 6. Pattern Strategy - Logging
- Fichier: `06-pattern-strategy-logging.puml`
- Role: representer le changement dynamique de strategie de journalisation.
- Pattern: Strategy.

## 7. Pattern Observer - Historique / notifications
- Fichier: `07-pattern-observer-historique-notifications.puml`
- Role: modeliser la diffusion des evenements d'action vers les observateurs.
- Pattern: Observer.

## 8. Pattern DAO - SQLite
- Fichier: `08-pattern-dao-sqlite.puml`
- Role: separer l'acces aux donnees SQLite de la logique metier.
- Pattern: DAO (Data Access Object).

## 9. Plus court chemin - GraphPathService, Dijkstra, Bellman-Ford
- Fichier: `09-plus-court-chemin-graphpath-dijkstra-bellmanford.puml`
- Role: decrire le calcul de plus court chemin et la selection d'algorithme.
- Pattern: Strategy (algorithmes) + Registry.

## Remarque
- Les noms de classes utilises correspondent aux classes presentes dans le code source du projet.
- Les diagrammes sont volontairement simplifies pour un usage rapport universitaire.
