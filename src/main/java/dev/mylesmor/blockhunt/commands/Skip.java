package dev.mylesmor.blockhunt.commands;

import dev.mylesmor.blockhunt.BlockHunt;
import dev.mylesmor.blockhunt.util.Permissions;
import dev.mylesmor.blockhunt.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Skip {

    /**
     * Allows a player to skip the current round.
     * @param p The player running the command.
     * @param args Not required.
     */
    public static void skip(Player p, String[] args) {
        if (p.hasPermission(Permissions.SKIP)) {
            BlockHunt.game.skip();
        } else {
            Util.blockShuffleMessage(p, ChatColor.RED, "You don't have permission to use this command!", null);
        }
    }
}
