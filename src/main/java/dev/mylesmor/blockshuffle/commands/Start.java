package dev.mylesmor.blockshuffle.commands;

import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.game.BlockShuffleGame;
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
            if (args != null) {
                if (args.length == 1) {
                    BlockShuffle.game = BlockShuffle.games.get(args[0].toLowerCase());
                    if (BlockShuffle.game != null) {
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
                        Util.blockShuffleMessage(p, ChatColor.RED, "Incorrect command usage. Correct usage: " + ChatColor.LIGHT_PURPLE + "/bs start <preset>", null);
                    }
                } else {
                    Util.blockShuffleMessage(p, ChatColor.RED, "This preset was not found in the config!", null);
                }
            } else {
                Util.blockShuffleMessage(p, ChatColor.RED, "Incorrect command usage. Correct usage: " + ChatColor.LIGHT_PURPLE + "/bs start <preset>", null);
            }
        } else {
            Util.blockShuffleMessage(p, ChatColor.RED, "You don't have permission to use this command!", null);
        }
    }
}
