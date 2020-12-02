package dev.mylesmor.blockshuffle.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.mylesmor.blockshuffle.util.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Commands implements CommandExecutor {

    Map<String, BiConsumer<Player, String[]>> commands = new HashMap<>();

    public Commands() {
        commands.put("join", Join::join);
        commands.put("start", Start::start);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player)) {
            final Player p = (Player) sender;
            try {
                BiConsumer<Player, String[]> command = commands.get(args[0].toLowerCase());
                if (args.length > 1) {
                    String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                    command.accept(p, newArgs);
                } else {
                    command.accept(p, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Util.blockShuffleMessage(p, ChatColor.RED, "Invalid command!" + ChatColor.GRAY + " Type " + ChatColor.LIGHT_PURPLE + "/bs help " + ChatColor.GRAY + "for a list of commands.", null);
            }
        }
        return true;
    }
}

