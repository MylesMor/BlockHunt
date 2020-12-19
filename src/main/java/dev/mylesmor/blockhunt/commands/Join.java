package dev.mylesmor.blockhunt.commands;

import dev.mylesmor.blockhunt.BlockHunt;
import dev.mylesmor.blockhunt.data.Status;
import dev.mylesmor.blockhunt.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Join {

    /**
     * Allows the player to join a game.
     * @param p The player running the command.
     * @param args Not required.
     */
    public static void join(Player p, String[] args) {
        if (p.hasPermission(Permissions.JOIN)) {
            if (!BlockHunt.players.containsKey(p)) {
                BlockHunt.players.put(p, false);
                BlockHunt.board.destroySingular(p);
                BlockHunt.board.setScoreboard(p);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Util.blockShuffleMessage(onlinePlayer, ChatColor.GRAY, ChatColor.GOLD + p.getName() + ChatColor.GRAY + " joined the game! There are now " + ChatColor.YELLOW + BlockHunt.players.size() + ChatColor.GRAY + " player(s).", null);
                }
            } else {
                if (BlockHunt.players.containsKey(p)) {
                    Util.blockShuffleMessage(p, ChatColor.RED, "You are already in a game!", null);
                } else {
                    if (BlockHunt.game.getStatus() == Status.INGAME) {
                        Util.blockShuffleMessage(p, ChatColor.RED, "Sorry, you're already out!", null);
                    }
                }
            }
        } else {
            Util.blockShuffleMessage(p, ChatColor.RED, "You don't have permission to use this command!", null);
        }
    }
}
