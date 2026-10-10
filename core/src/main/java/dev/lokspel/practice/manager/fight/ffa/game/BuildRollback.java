package dev.lokspel.practice.manager.fight.ffa.game;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.manager.backend.ConfigManager;
import dev.lokspel.practice.util.PermanentConfig;
import dev.lokspel.practice.util.fightmapchange.FightChangeOptimized;
import dev.lokspel.practice.util.interfaces.Runnable;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class BuildRollback extends Runnable {

    private static final int ROLLBACK_SECONDS = ConfigManager.getInt("FFA.ROLLBACK.SECONDS");

    private final FightChangeOptimized fightChange;
    private final java.lang.Runnable onRollbackComplete;

    public BuildRollback(FightChangeOptimized fightChange, java.lang.Runnable onRollbackComplete) {
        super(20L, 20L, false);
        this.fightChange = fightChange;
        this.onRollbackComplete = onRollbackComplete;
        this.seconds = ROLLBACK_SECONDS;
    }

    @Override
    public void run() {
        if (seconds == 0) {
            this.rollback();
        }

        seconds--;
    }

    @Override
    public void cancel() {
        if (!running) return;

        running = false;
        Bukkit.getScheduler().cancelTask(this.getTaskId());

        this.rollback();
    }

    public void rollback() {
        this.seconds = ROLLBACK_SECONDS;

        if (ZonePractice.getInstance().isEnabled()) {
            fightChange.rollback(PermanentConfig.MATCH_ROLLBACK_MAX_CHECKS, PermanentConfig.MATCH_ROLLBACK_MAX_CHANGES, onRollbackComplete);
        } else {
            fightChange.quickRollback();
            if (onRollbackComplete != null) {
                onRollbackComplete.run();
            }
        }
    }

}
