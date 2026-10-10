package dev.lokspel.practice.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Copies the legacy {@code plugins/ZonePracticePro} data folder to the current
 * plugin folder name. The original folder is left untouched and acts as a backup
 * under its old name.
 */
public final class DataFolderMigrationUtil {

    private static final String LEGACY_FOLDER_NAME = "ZonePracticePro";

    private DataFolderMigrationUtil() {}

    public static void migrate(JavaPlugin plugin) {
        Path dataFolder = plugin.getDataFolder().toPath();
        Path parent = dataFolder.getParent();
        if (parent == null) return;

        Path legacyFolder = parent.resolve(LEGACY_FOLDER_NAME);
        if (!Files.isDirectory(legacyFolder) || Files.exists(dataFolder)) return;

        try {
            copy(legacyFolder, dataFolder);
            Common.sendConsoleMMMessage("<green>Copied data folder "
                    + LEGACY_FOLDER_NAME + " to " + dataFolder.getFileName()
                    + " (original kept as backup).");
        } catch (IOException e) {
            Common.sendConsoleMMMessage("<red>Couldn't migrate the legacy " + LEGACY_FOLDER_NAME
                    + " data folder: " + e.getMessage());
        }
    }

    private static void copy(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
