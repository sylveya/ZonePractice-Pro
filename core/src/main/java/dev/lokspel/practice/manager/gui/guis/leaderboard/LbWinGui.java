package dev.lokspel.practice.manager.gui.guis.leaderboard;

import dev.lokspel.practice.manager.gui.GUI;
import dev.lokspel.practice.manager.gui.GUIType;
import dev.lokspel.practice.manager.ladder.abstraction.normal.NormalLadder;
import org.bukkit.inventory.ItemStack;

public class LbWinGui extends LeaderboardGui {

    public LbWinGui(GUI backTo) {
        super(GUIType.Leaderboard_WIN, "GUIS.STATISTICS.WIN-LEADERBOARD", backTo);
        build();
    }

    @Override
    protected ItemStack createLadderItem(NormalLadder ladder) {
        return LbGuiUtil.createWinLbItem(ladder);
    }

    @Override
    protected ItemStack createGlobalItem() {
        return LbGuiUtil.createGlobalWinLb();
    }
}
