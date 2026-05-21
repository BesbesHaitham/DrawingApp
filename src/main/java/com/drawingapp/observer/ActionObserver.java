package com.drawingapp.observer;

/**
 * Interface Observer pour les notifications d'actions.
 */
public interface ActionObserver {
    /**
     * Appelée quand une action est exécutée.
     */
    void onActionExecuted(String actionDescription);

    /**
     * Appelée quand une action est annulée.
     */
    void onActionUndone(String actionDescription);

    /**
     * Retourne le nom de l'observateur.
     */
    String getObserverName();
}
