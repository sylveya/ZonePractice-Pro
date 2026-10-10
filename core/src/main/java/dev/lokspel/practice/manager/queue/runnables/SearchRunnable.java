package dev.lokspel.practice.manager.queue.runnables;

import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import dev.lokspel.practice.manager.queue.Queue;
import dev.lokspel.practice.manager.queue.QueueManager;
import dev.lokspel.practice.util.actionbar.ActionBar;
import dev.lokspel.practice.util.actionbar.ActionBarPriority;
import dev.lokspel.practice.util.interfaces.Runnable;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SearchRunnable extends Runnable {

    protected final QueueManager queueManager = QueueManager.getInstance();
    protected final Queue queue;
    protected final ActionBar actionBar;

    protected BukkitTask searching;
    private static final String ACTION_BAR_ID = "queue";

    public SearchRunnable(final Queue queue, long delay, long period, boolean async) {
        super(delay, period, async);
        this.queue = queue;
        this.actionBar = queue.getProfile().getActionBar();
    }

    @Override
    public void cancel() {
        if (!running) return;

        running = false;
        Bukkit.getScheduler().cancelTask(this.getTaskId());

        if (searching != null) {
            searching.cancel();
        }
        queue.cancel();

        // Clear the queue action bar message
        actionBar.removeMessage(ACTION_BAR_ID);
    }

    protected void updateQueueActionBar(String message) {
        String safeMessage = message;
        if (safeMessage == null || safeMessage.trim().isEmpty()) {
            // Keep queue feedback visible even when a language key is missing/misconfigured.
            safeMessage = "<yellow>Searching for a match... <gray>" + queue.getFormattedDuration();
        }

        // Sets an infinite action bar with NORMAL priority.
        this.actionBar.setMessage(ACTION_BAR_ID, safeMessage, -1, ActionBarPriority.NORMAL);
    }

    protected List<NormalLadder> getShuffledQueuedLadders() {
        List<NormalLadder> ladders = new ArrayList<>(queue.getQueuedLadders());
        Collections.shuffle(ladders);
        return ladders;
    }

    public abstract void run();

}
