package dev.lokspel.practice.manager.gui.guis.leaderboard;

import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.inventory.ItemStack;

public class LbEloGui extends LeaderboardGui {

    public LbEloGui(GUI backTo) {
        super(GUIType.Leaderboard_ELO, "GUIS.STATISTICS.ELO-LEADERBOARD", backTo);
        build();
    }

    @Override
    protected boolean isLadderVisible(NormalLadder ladder) {
        return super.isLadderVisible(ladder) && ladder.isRanked();
    }

    @Override
    protected ItemStack createLadderItem(NormalLadder ladder) {
        return LbGuiUtil.createEloLbItem(ladder);
    }

    @Override
    protected ItemStack createGlobalItem() {
        return LbGuiUtil.createGlobalEloLb();
    }
}
