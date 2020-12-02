package dev.mylesmor.blockshuffle.commands;

import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.util.*;
import dev.mylesmor.blockshuffle.BlockShuffle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Start {

    /**
     * Allows the player to start a game.
     * @param p The player running the command.
     * @param args Not required.
     */
    public static void start(Player p, String[] args) {
        if (p.hasPermission(Permissions.START)) {
            if (BlockShuffle.game.getStatus() == Status.LOBBY) {
                if (BlockShuffle.players.size() >= 1) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (!BlockShuffle.players.containsKey(pl)) {
                            pl.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                    BlockShuffle.game.start();
                } else {
                    Util.blockShuffleMessage(p, ChatColor.RED, "Not enough players to start the game!", null);
                }
            } else {
                Util.blockShuffleMessage(p, ChatColor.RED, "A game is already in progress!", null);
            }
        } else {
            Util.blockShuffleMessage(p, ChatColor.RED, "You don't have permission to use this command!", null);
        }
    }
}
