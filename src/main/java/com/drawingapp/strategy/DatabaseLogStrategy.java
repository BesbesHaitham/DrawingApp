package com.drawingapp.strategy;

import com.drawingapp.dao.ActionLogRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie de logging dans la base de données.
 */
public class DatabaseLogStrategy implements LogStrategy {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ActionLogRepository dao;

    public DatabaseLogStrategy(ActionLogRepository dao) {
        this.dao = dao;
    }

    @Override
    public void log(String action) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            boolean saved = dao.saveLog(action, timestamp);
            if (!saved) {
                System.err.println("Erreur enregistrement log base de donnees.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du log en base: " + e.getMessage());
        }
    }

    @Override
    public String getStrategyName() {
        return "Database";
    }
}
