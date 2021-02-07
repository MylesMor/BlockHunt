package dev.mylesmor.blockhunt.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Util {

    public static final String prefix = "" + ChatColor.AQUA + ChatColor.BOLD + "BLOCK" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "SHUFFLE " ;

    public static void blockShuffleMessage(Player p, ChatColor color, String message, String name) {
        if (message.contains("%NAME%")) {
             message = message.replace("%NAME%", ChatColor.GOLD + name + color);
        }
        p.sendMessage(prefix + color + message);
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }
}
