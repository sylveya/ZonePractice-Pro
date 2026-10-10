package dev.lokspel.practice.manager.server.sound;

import dev.lokspel.practice.manager.backend.ConfigManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundEffect {

    @Getter
    private final boolean enabled;
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundEffect(String name) {
        this.enabled = ConfigManager.getBoolean("SOUNDS." + name + ".ENABLED");
        this.sound = resolveSound(ConfigManager.getString("SOUNDS." + name + ".SOUND"));
        this.volume = (float) ConfigManager.getDouble("SOUNDS." + name + ".VOLUME");
        this.pitch = (float) ConfigManager.getDouble("SOUNDS." + name + ".PITCH");
    }

    SoundEffect(boolean enabled) {
        this.enabled = enabled;
        this.sound = null;
        this.volume = 0;
        this.pitch = 0;
    }

    private static Sound resolveSound(String soundName) {
        Sound sound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundName));
        if (sound == null)
            throw new IllegalArgumentException("Unknown sound: " + soundName);
        return sound;
    }

    public void play(Player player) {
        if (enabled)
            player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void play(List<Player> players) {
        if (enabled)
            players.forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
    }

    public void play(Player player, Location location) {
        if (enabled)
            player.playSound(location, sound, volume, pitch);
    }

    public void play(List<Player> players, Location location) {
        if (enabled)
            players.forEach(player -> player.playSound(location, sound, volume, pitch));
    }

}
