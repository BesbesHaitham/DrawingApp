package com.drawingapp.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observable pour les actions de dessin.
 * Notifie les observateurs (notamment le système de logging).
 */
public class ActionObservable {
    private final List<ActionObserver> observers;

    public ActionObservable() {
        this.observers = new ArrayList<>();
    }

    /**
     * Ajoute un observateur.
     */
    public void addObserver(ActionObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Retire un observateur.
     */
    public void removeObserver(ActionObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifie tous les observateurs qu'une action a été exécutée.
     */
    public void notifyActionExecuted(String actionDescription) {
        for (ActionObserver observer : observers) {
            observer.onActionExecuted(actionDescription);
        }
    }

    /**
     * Notifie tous les observateurs qu'une action a été annulée.
     */
    public void notifyActionUndone(String actionDescription) {
        for (ActionObserver observer : observers) {
            observer.onActionUndone(actionDescription);
        }
    }

    /**
     * Retourne le nombre d'observateurs.
     */
    public int getObserverCount() {
        return observers.size();
    }
}
