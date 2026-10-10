package dev.lokspel.practice.util.interfaces;

import dev.lokspel.practice.ZonePractice;
import dev.lokspel.practice.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class Runnable extends BukkitRunnable {

    protected boolean hasRun = false;
    protected boolean running = false;

    protected final long delay;
    protected final long period;
    protected final boolean async;

    @Setter
    protected int seconds = 0;

    public Runnable(long delay, long period, boolean async) {
        this.delay = delay;
        this.period = period;
        this.async = async;
    }

    public boolean begin() {
        if (this.hasRun)
            return false;

        this.running = true;
        this.hasRun = true;

        if (async)
            this.runTaskTimerAsynchronously(ZonePractice.getInstance(), delay, period);
        else
            this.runTaskTimer(ZonePractice.getInstance(), delay, period);

        return true;
    }

    @Override
    public void cancel() {
        if (!running) return;

        running = false;
        Bukkit.getScheduler().cancelTask(this.getTaskId());
    }

    public abstract void run();

    public String getFormattedTime() {
        return TimeUtil.formatDuration(seconds * 1000L);
    }

}
