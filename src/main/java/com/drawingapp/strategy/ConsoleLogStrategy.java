package com.drawingapp.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie de logging en console.
 */
public class ConsoleLogStrategy implements LogStrategy {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void log(String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] " + action);
    }

    @Override
    public String getStrategyName() {
        return "Console";
    }
}
