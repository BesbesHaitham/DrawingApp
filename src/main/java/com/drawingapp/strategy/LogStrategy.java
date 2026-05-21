package com.drawingapp.strategy;

/**
 * Interface Strategy pour la journalisation.
 */
public interface LogStrategy {
    /**
     * Enregistre une action.
     */
    void log(String action);

    /**
     * Retourne le nom de la stratégie.
     */
    String getStrategyName();
}
