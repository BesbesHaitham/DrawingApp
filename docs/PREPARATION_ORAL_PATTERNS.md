# Preparation Oral - Patterns (Explication Tres Simple)

## 1) C'est quoi ce projet, simplement ?
DrawingApp est une application de dessin.
Tu peux:
- dessiner des formes
- supprimer
- annuler/refaire
- enregistrer des actions (logs)
- ajouter un graphe (noeuds/aretes) et calculer le plus court chemin

Idee importante: le projet utilise des patterns pour garder le code propre, lisible, facile a modifier.

## 2) Pourquoi utiliser des patterns ?
Sans pattern, le code devient vite:
- melange
- difficile a maintenir
- difficile a tester
- fragile quand on ajoute une nouvelle fonctionnalite

Avec pattern:
- chaque partie a un role clair
- on peut evoluer sans casser tout le projet

## 3) Les 4 patterns choisis (ceux en rouge)

### A) Factory Method
Question prof: "Pourquoi Factory Method ?"

Reponse simple:
- Parce qu'on doit creer plusieurs formes (rectangle, cercle, ligne, etc.)
- On veut un seul endroit qui decide quelle classe creer
- Le controleur ne fait pas plein de `new RectangleShape()`, `new CircleShape()` partout

Dans ce projet:
- `ShapeFactory` recoit un type (ex: `RECTANGLE`)
- elle retourne l'objet forme correspondant

Avantage:
- ajouter une nouvelle forme est simple
- on evite du code duplique

---

### B) Command
Question prof: "Pourquoi Command ?"

Reponse simple:
- Parce qu'on veut `Undo/Redo`
- Chaque action utilisateur devient un objet commande
- Cette commande sait faire 2 choses: `execute()` et `undo()`

Exemples:
- DrawCommand
- DeleteCommand
- AddGraphNodeCommand

Avantage:
- annulation propre
- historique des actions facile
- code des actions mieux organise

---

### C) Strategy
Question prof: "Pourquoi Strategy ?"

Reponse simple:
- Parce que le logging peut changer
- Aujourd'hui console, demain fichier, apres demain base de donnees
- On garde la meme interface (`LogStrategy`) et on change juste l'implementation

Avantage:
- pas besoin de modifier toute l'application quand on change le mode de log
- extension facile

---

### D) Observer
Question prof: "Pourquoi Observer ?"

Reponse simple:
- Quand une action est executee/annulee, plusieurs composants doivent etre informes
- Ex: historique + logger
- Avec Observer, le sujet notifie automatiquement les observateurs

Dans ce projet:
- `ActionObservable` notifie
- `DrawingController` (observer) recoit l'evenement et log/historise

Avantage:
- faible couplage
- on peut ajouter d'autres observateurs sans casser le reste

## 4) Question tres probable: "Pourquoi seulement ces patterns ?"
Reponse conseillee:

"J'ai choisi les patterns selon les problemes reels de mon application, pas pour utiliser beaucoup de patterns. Mon besoin principal etait:
1. creation de formes
2. undo/redo
3. changement de logging
4. notifications d'actions

Donc Factory Method, Command, Strategy et Observer couvrent exactement ces besoins. Ajouter d'autres patterns sans besoin aurait complique inutilement le code."

## 5) Question: "Pourquoi pas les autres patterns ?"
Tu peux repondre comme ca:

### Pourquoi pas Abstract Factory ?
- Abstract Factory est utile pour creer des familles d'objets lies ensemble
- Exemple: theme Windows/Mac avec bouton + menu + checkbox coherents
- Ici, on cree surtout un seul objet "forme" a la fois
- Donc Factory Method suffit, plus simple

### Pourquoi pas Adapter ?
- Adapter sert a connecter deux interfaces incompatibles
- Dans ce projet, ce probleme n'est pas central

### Pourquoi pas Decorator ?
- Decorator sert a ajouter des comportements dynamiques en couches
- Ici, le besoin prioritaire etait actions + undo/redo, pas l'empilement de decorations runtime

### Pourquoi pas Singleton ?
- Singleton peut etre utile, mais il augmente le couplage global
- Il complique parfois les tests
- Le projet privilegie des dependances injectees (plus propre)

## 6) Question piege: "Pourquoi Factory Method et pas Abstract Factory ?"
Reponse courte et forte:

"Parce que mon besoin est la creation d'un type d'objet a la fois (forme). Abstract Factory est mieux pour des familles d'objets dependants. Dans mon cas, Abstract Factory serait une sur-architecture. Factory Method est suffisant, simple et maintenable."

## 7) Mini script oral (30-45 secondes)
"L'application suit MVC. Pour la creation de formes, j'utilise Factory Method. Pour supporter undo/redo proprement, j'utilise Command. Pour changer la destination des logs sans toucher la logique metier, j'utilise Strategy. Pour notifier automatiquement l'historique et le logging lors des actions, j'utilise Observer. J'ai choisi seulement ces patterns car ils correspondent exactement aux besoins reels du projet."

## 8) SOLID (version ultra simple)
- S: chaque classe a un role principal
- O: on peut ajouter une nouvelle forme/strategie sans casser l'existant
- L: les implementations peuvent se remplacer via leur interface
- I: interfaces petites et claires (`Command`, `LogStrategy`)
- D: le controleur depend d'abstractions, pas de details

## 9) Point important a dire au prof (honnette)
Dans la documentation, il est ecrit SQLite.
Dans le code actuel `DrawingDAO`, la connexion est configuree en MySQL (`jdbc:mysql://...`).

Tu peux dire:
"Il y a une incoherence doc/code a corriger. L'architecture DAO reste valide, mais la documentation base de donnees doit etre alignee avec l'implementation actuelle."

## 10) Reponses tres courtes (mode rapide)
- Pourquoi ces 4 patterns ?
  - "Parce qu'ils repondent exactement aux besoins fonctionnels du projet."
- Pourquoi pas plus ?
  - "Pour eviter la sur-complexite sans valeur technique reelle."
- Pourquoi Factory Method ?
  - "Creation centralisee des formes, extension facile."
- Pourquoi pas Abstract Factory ?
  - "Pas de familles d'objets complexes a creer, donc inutile ici."

---
Si tu bloques pendant l'oral, utilise la regle:
"1 probleme metier = 1 pattern qui le resout simplement".
