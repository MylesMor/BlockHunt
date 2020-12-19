package dev.mylesmor.blockhunt.commands;

import dev.mylesmor.blockhunt.BlockHunt;
import dev.mylesmor.blockhunt.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Check implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player)) {
            final Player p = (Player) sender;
            if (BlockHunt.players.containsKey(p)) {
                if (!BlockHunt.players.get(p)) {
                    BlockHunt.game.verifyBlock(p);
                } else {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10, 0.1F);
                    Util.blockShuffleMessage(p, ChatColor.RED, "You've already found your block for this round!", null);
                }
            }
        }
        return true;
    }
}
