package com.drawingapp.dao;

public interface ActionLogRepository {
    boolean saveLog(String action, String timestamp);
}
