package com.drawingapp.strategy;

import com.drawingapp.dao.ActionLogRepository;

public class DefaultLogStrategyFactory implements LogStrategyFactory {
    private final ActionLogRepository actionLogRepository;

    public DefaultLogStrategyFactory(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    @Override
    public LogStrategy create(String strategyName) {
        if ("Fichier".equals(strategyName)) {
            return new FileLogStrategy();
        }
        if ("Base de donnees".equals(strategyName)) {
            return new DatabaseLogStrategy(actionLogRepository);
        }
        return new ConsoleLogStrategy();
    }
}
