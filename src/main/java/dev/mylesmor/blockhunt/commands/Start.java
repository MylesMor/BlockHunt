package dev.mylesmor.blockhunt.commands;

import dev.mylesmor.blockhunt.data.Status;
import dev.mylesmor.blockhunt.util.*;
import dev.mylesmor.blockhunt.BlockHunt;
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
                    BlockHunt.game = BlockHunt.games.get(args[0].toLowerCase());
                    if (BlockHunt.game != null) {
                        if (BlockHunt.game.getStatus() == Status.LOBBY) {
                            if (BlockHunt.players.size() >= 1) {
                                for (Player pl : Bukkit.getOnlinePlayers()) {
                                    if (!BlockHunt.players.containsKey(pl)) {
                                        pl.setGameMode(GameMode.SPECTATOR);
                                    }
                                }
                                BlockHunt.game.start();
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
