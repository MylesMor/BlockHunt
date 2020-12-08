package dev.mylesmor.blockshuffle;

import dev.mylesmor.blockshuffle.commands.BlockShuffleTabCompleter;
import dev.mylesmor.blockshuffle.commands.Check;
import dev.mylesmor.blockshuffle.config.ConfigManager;
import dev.mylesmor.blockshuffle.game.*;
import dev.mylesmor.blockshuffle.commands.Commands;
import dev.mylesmor.blockshuffle.listeners.GameListener;
import dev.mylesmor.blockshuffle.listeners.JoinLeaveListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class BlockShuffle extends JavaPlugin implements Listener {

    public static HashMap<Player, Boolean> players = new HashMap<>();
    public static HashMap<Player, Integer> scores = new HashMap<>();

    public static HashMap<String, BlockShuffleGame> games = new HashMap<>();
    public static BlockShuffleGame game = null;
    public static BlockShuffleBoard board = null;

    public static Plugin plugin;
    public static ConfigManager config;

    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("blockshuffle").setExecutor(new Commands());
        this.getCommand("blockshuffle").setExecutor(new Commands());
        this.getCommand("blockshuffle").setTabCompleter(new BlockShuffleTabCompleter());
        this.getCommand("check").setExecutor(new Check());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        board = new BlockShuffleBoard();
    }

    @Override
    public void onDisable() {
        board.destroyBoards();
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        config = new ConfigManager(this);
        games = config.getGames();
    }
}
