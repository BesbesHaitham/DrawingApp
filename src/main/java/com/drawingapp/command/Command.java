package com.drawingapp.command;

/**
 * Interface Command pour les actions de dessin.
 */
public interface Command {
    /**
     * Exécute la commande.
     */
    void execute();

    /**
     * Annule la commande.
     */
    void undo();

    /**
     * Retourne une description de la commande.
     */
    String getDescription();
}
