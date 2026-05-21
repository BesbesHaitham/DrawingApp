package com.drawingapp.strategy;

public interface LogStrategyFactory {
    LogStrategy create(String strategyName);
}
