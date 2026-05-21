package com.drawingapp.strategy;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogStrategy implements LogStrategy {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_DIRECTORY = Paths.get("data", "logs");
    private static final Path LOG_FILE = LOG_DIRECTORY.resolve("drawing-actions.log");

    public FileLogStrategy() {
        ensureLogTarget();
    }

    @Override
    public void log(String action) {
        ensureLogTarget();

        try (Writer writer = Files.newBufferedWriter(
                LOG_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.write("[" + timestamp + "] " + action + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Erreur ecriture log fichier: " + e.getMessage());
        }
    }

    @Override
    public String getStrategyName() {
        return "File";
    }

    private void ensureLogTarget() {
        try {
            Files.createDirectories(LOG_DIRECTORY);
            if (!Files.exists(LOG_FILE)) {
                Files.createFile(LOG_FILE);
            }
        } catch (IOException e) {
            System.err.println("Erreur creation log fichier: " + e.getMessage());
        }
    }
}
