package dev.lokspel.practice.manager.backend;

import dev.lokspel.practice.AstralPractice;
import dev.lokspel.practice.manager.gui.GUIItem;
import dev.lokspel.practice.util.Common;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public enum ConfigManager {
    ;

    private static final AstralPractice practice = AstralPractice.getInstance();
    private static File file;
    @Getter
    private static YamlConfiguration config;

    public static void createFile() {
        file = new File(practice.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        reload();
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Common.sendConsoleMMMessage("<red>Error: " + e.getMessage());
        }
    }

    public static void reload() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Common.sendConsoleMMMessage("<red>Error: " + e.getMessage());
        }
    }

    public static Object get(String loc) {
        return getConfig().get(loc);
    }

    public static String getString(String loc) {
        String s = config.getString(loc);
        if (s != null)
            return s;
        return "";
    }

    public static boolean getBoolean(String loc) {
        return getConfig().getBoolean(loc);
    }

    public static boolean getBoolean(String loc, boolean def) {
        return getConfig().getBoolean(loc, def);
    }

    public static boolean isMatchChatIsolated() {
        return getBoolean("CHAT.ISOLATE-MATCH-CHAT");
    }

    public static boolean isShowMatchChatToSpectators() {
        return getBoolean("CHAT.SHOW-MATCH-CHAT-TO-SPECTATORS");
    }

    public static boolean isPartyChatEnabled() {
        return getBoolean("CHAT.PARTY-CHAT.ENABLED");
    }

    public static String getPartyChatShortcut() {
        return getString("CHAT.PARTY-CHAT.SHORTCUT");
    }

    public static boolean isShowPlayersInTab() {
        return getBoolean("MATCH-SETTINGS.SHOW-PLAYERS-IN-TAB");
    }

    public static boolean isShowPlayersInLobbyTab() {
        return getBoolean("MATCH-SETTINGS.SHOW-PLAYERS-IN-LOBBY-TAB");
    }

    public static boolean isShowSpectatorsInTab() {
        return getBoolean("MATCH-SETTINGS.SHOW-SPECTATORS-IN-TAB");
    }

    public static String getSpectatorTabPrefix() {
        return getString("MATCH-SETTINGS.SPECTATOR-TAB-PREFIX");
    }

    public static int getInt(String loc) {
        return getConfig().getInt(loc);
    }

    public static int getInt(String loc, int def) {
        return getConfig().getInt(loc, def);
    }

    public static double getDouble(String loc) {
        return getConfig().getDouble(loc);
    }

    public static double getDouble(String loc, double def) {
        return getConfig().getDouble(loc, def);
    }

    public static Set<String> getConfigSectionKeys(String loc) {
        return Objects.requireNonNull(getConfig().getConfigurationSection(loc)).getKeys(false);
    }

    public static List<String> getList(String loc) {
        return getConfig().getStringList(loc);
    }

    public static GUIItem getGuiItem(String loc) {
        return BackendUtil.getGuiItem(config, loc);
    }

}
