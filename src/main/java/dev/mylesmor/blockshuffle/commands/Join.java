package dev.mylesmor.blockshuffle.commands;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.game.BlockShuffleBoard;
import dev.mylesmor.blockshuffle.util.*;
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
            if (BlockShuffle.players.size() == 0) {
                BlockShuffle.board = new BlockShuffleBoard();
                BlockShuffle.board.setPlayerBoard(p);
            }
            if (!BlockShuffle.players.containsKey(p) && !BlockShuffle.lostPlayers.containsKey(p)) {
                BlockShuffle.players.put(p, false);
                BlockShuffle.board.updateBoard();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Util.blockShuffleMessage(onlinePlayer, ChatColor.GRAY, ChatColor.GOLD + p.getName() + ChatColor.GRAY + " joined the game! There are now " + ChatColor.YELLOW + BlockShuffle.players.size() + ChatColor.GRAY + " player(s).", null);
                }
            } else {
                if (BlockShuffle.players.containsKey(p)) {
                    Util.blockShuffleMessage(p, ChatColor.RED, "You are already in a game!", null);
                } else {
                    Util.blockShuffleMessage(p, ChatColor.RED, "Sorry, you're already out!", null);
                }
            }
        } else {
            Util.blockShuffleMessage(p, ChatColor.RED, "You don't have permission to use this command!", null);
        }
    }
}
