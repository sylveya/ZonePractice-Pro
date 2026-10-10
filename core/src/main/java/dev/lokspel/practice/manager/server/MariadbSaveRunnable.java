package dev.lokspel.practice.manager.server;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.profile.ProfileManager;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class MariadbSaveRunnable extends BukkitRunnable {

    private final int interval = ConfigManager.getInt("MARIADB-DATABASE.SAVE-PERIOD");

    public void begin() {
        this.runTaskTimerAsynchronously(ZonePractice.getInstance(), interval * 60 * 20L, interval * 60 * 20L);
    }

    @Override
    public void run() {
        save();
    }

    public void save() {
        ProfileManager.getInstance().saveProfilesToDatabase();
    }

}
