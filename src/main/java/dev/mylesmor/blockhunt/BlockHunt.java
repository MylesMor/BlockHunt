package dev.mylesmor.blockhunt;

import dev.mylesmor.blockhunt.commands.BlockHuntTabCompleter;
import dev.mylesmor.blockhunt.commands.Check;
import dev.mylesmor.blockhunt.config.ConfigManager;
import dev.mylesmor.blockhunt.game.*;
import dev.mylesmor.blockhunt.commands.Commands;
import dev.mylesmor.blockhunt.listeners.GameListener;
import dev.mylesmor.blockhunt.listeners.JoinLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class BlockHunt extends JavaPlugin implements Listener {

    public static HashMap<Player, Boolean> players = new HashMap<>();
    public static HashMap<Player, Integer> scores = new HashMap<>();

    public static HashMap<String, BlockHuntGame> games = new HashMap<>();
    public static BlockHuntGame game = null;
    public static BlockHuntBoard board = null;
    public static BossBar bossBar = null;

    public static Plugin plugin;
    public static ConfigManager config;

    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("blockhunt").setExecutor(new Commands());
        this.getCommand("blockhunt").setExecutor(new Commands());
        this.getCommand("blockhunt").setTabCompleter(new BlockHuntTabCompleter());
        this.getCommand("check").setExecutor(new Check());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        board = new BlockHuntBoard();
        bossBar = Bukkit.createBossBar(ChatColor.YELLOW + "TIME REMAINING", BarColor.GREEN, BarStyle.SEGMENTED_20);
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.removePlayer(p);
        }
        board.destroyBoards();
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        config = new ConfigManager(this);
        games = config.getGames();
    }
}
