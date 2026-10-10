package dev.lokspel.practice.command.practice.arguments;

import dev.lokspel.practice.manager.backend.LanguageManager;
import dev.lokspel.practice.manager.server.ServerManager;
import dev.lokspel.practice.util.Common;
import dev.lokspel.practice.util.GoldenHead;
import dev.lokspel.practice.util.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GoldenHeadArg {

    private GoldenHeadArg() {}

    public static void run(Player player, String label, String[] args) {
        if (!player.hasPermission("ap.setup")) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.NO-PERMISSION"));
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.GOLDENHEAD.INVENTORY-FULL"));
            return;
        }

        GoldenHead goldenHead = ServerManager.getInstance().getGoldenHead();
        if (args.length == 1) {
            player.getInventory().addItem(goldenHead.getItem().clone());
        } else if (args.length == 2) {
            if (NumberUtil.isNotInteger(args[1])) {
                Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.GOLDENHEAD.INVALID-AMOUNT"));
                return;
            }

            int amount = Integer.parseInt(args[1]);
            if (amount < 1 || amount > 64) {
                Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.GOLDENHEAD.INVALID-AMOUNT-2"));
                return;
            }

            ItemStack itemStack = goldenHead.getItem().clone();
            itemStack.setAmount(amount);

            player.getInventory().addItem(itemStack);
        } else
            Common.sendMMMessage(player, LanguageManager.getString("COMMAND.PRACTICE.ARGUMENTS.GOLDENHEAD.COMMAND-HELP").replace("%label%", label));
    }

}
