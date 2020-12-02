package dev.mylesmor.blockshuffle;

import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.game.Check;
import dev.mylesmor.blockshuffle.game.*;
import dev.mylesmor.blockshuffle.commands.Commands;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class BlockShuffle extends JavaPlugin {

    public static HashMap<Player, Boolean> players = new HashMap<>();
    public static HashMap<Player, Integer> lostPlayers = new HashMap<>();

    public static BlockShuffleGame game = null;
    public static BlockShuffleBoard board = null;

    public static Status status = Status.LOBBY;

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("blockshuffle").setExecutor(new Commands());
        this.getCommand("check").setExecutor(new Check());
        game = new BlockShuffleGame(620);
    }

    @Override
    public void onDisable() {

    }
}
