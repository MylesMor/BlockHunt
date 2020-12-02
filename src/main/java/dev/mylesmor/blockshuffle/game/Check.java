package dev.mylesmor.blockshuffle.game;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Check implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player)) {
            final Player p = (Player) sender;
            if (BlockShuffle.players.containsKey(p)) {
                verifyBlock(p);
            }
        }
        return true;
    }

    /**
     * Checks the block the player is standing on.
     * @param p The player running the command.
     */
    public static void verifyBlock(Player p) {
        Material matToFind = BlockShuffle.game.getCurrentBlock();
        Material m = p.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        if (m.toString().equals(matToFind.toString())) {
            p.sendTitle(ChatColor.GREEN + "You've found the block!", "", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 3);
            Util.blockShuffleMessage(p, ChatColor.GREEN, "Well done! Now continue mining and gathering materials until your next block!", null);
            BlockShuffle.players.replace(p, true);
            BlockShuffle.board.setFoundBoard(p);
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 10, 3);
            Util.blockShuffleMessage(p, ChatColor.RED, "That is not the correct block! You are standing on " + ChatColor.GOLD + m.toString().replace("_", " ") + ChatColor.RED + ". You need to stand on: " + ChatColor.GREEN + matToFind.toString().replace("_", " ") + ChatColor.RED + ".", null);
        }
    }
}
