package dev.lokspel.practice.util;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.backend.ConfigManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public final class UpdateChecker {

    private UpdateChecker() {}

    private static final String VERSION_URL =
            "https://raw.githubusercontent.com/sylveya/AstralPractice/dev/VERSION";

    private static final String DOWNLOAD_URL = "https://github.com/sylveya/AstralPractice/releases";

    public static void checkAsync(AstralPractice plugin) {
        if (!ConfigManager.getBoolean("UPDATE-CHECKER.ENABLED", true)) return;

        checkUpdates()
                .thenAccept(version -> {
                    String current = plugin.getPluginMeta().getVersion().split("-")[0];

                    if (version.equals(current)) {
                        Common.sendConsoleMMMessage("<gray>[AstralPractice] <green>You are using the latest version");
                        return;
                    }

                    Common.sendConsoleMMMessage("<gray>[AstralPractice] <yellow>A new version is available: <green>" + version);
                    Common.sendConsoleMMMessage("<gray>[AstralPractice] <yellow>Download: <aqua>" + DOWNLOAD_URL);
                })
                .exceptionally(e -> {
                    plugin.getLogger().warning("[AstralPractice] Update check failed");
                    return null;
                });
    }

    public static CompletableFuture<String> checkUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    URI.create(VERSION_URL).toURL().openStream()))) {

                return reader.readLine().trim();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
