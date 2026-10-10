package dev.lokspel.practice.manager.server;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.profile.Profile;
import dev.lokspel.practice.manager.profile.ProfileManager;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InactiveProfileRunnable extends BukkitRunnable {

    private final int deleteAfter = ConfigManager.getInt("PLAYER.DELETE-INACTIVE-USER.DAYS");
    @Getter
    private boolean running = false;

    public void begin() {
        running = true;
        this.runTaskTimerAsynchronously(ZonePractice.getInstance(), 20L * 30, 20L * 60 * 60 * 24);
    }

    @Override
    public void run() {
        int count = 0;

        List<Profile> profiles = new ArrayList<>(ProfileManager.getInstance().getProfiles().values());

        for (Profile profile : profiles) {
            long timeDiff = Math.abs(System.currentTimeMillis() - profile.getLastJoin());
            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

            if (daysDiff > deleteAfter) {
                if (profile.getFile().getFile().delete()) {
                    ProfileManager.getInstance().getProfiles().remove(profile.getUuid());
                    deleteStatsFromMariadb(profile);

                    count++;
                }
            }
        }

        if (count > 0)
            ServerManager.getInstance().alertPlayers("zpp.admin", LanguageManager.getString("PROFILE.INACTIVITY-REMOVED").replace("%count%", String.valueOf(count)));
    }

    private void deleteStatsFromMariadb(Profile profile) {
        ProfileManager.getInstance().deleteProfileFromDatabase(profile.getUuid());
    }

}
