package dev.mylesmor.blockshuffle;

import dev.mylesmor.blockshuffle.commands.Check;
import dev.mylesmor.blockshuffle.config.ConfigManager;
import dev.mylesmor.blockshuffle.data.Blocks;
import dev.mylesmor.blockshuffle.data.RandomBlocks;
import dev.mylesmor.blockshuffle.game.*;
import dev.mylesmor.blockshuffle.commands.Commands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class BlockShuffle extends JavaPlugin {

    public static HashMap<Player, Boolean> players = new HashMap<>();
    public static HashMap<Player, Integer> scores = new HashMap<>();

    public static BlockShuffleGame game = null;
    public static BlockShuffleBoard board = null;

    public static Plugin plugin;
    public static ConfigManager config;

    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("blockshuffle").setExecutor(new Commands());
        this.getCommand("check").setExecutor(new Check());
        config = new ConfigManager(this);
        Blocks blocks;
        if (config.getRandomBlocks()) {
            blocks = new RandomBlocks(config.getBlocks(), config.getRandomBlockOrder(), config.getDisabledBlocks(), config.getNumberOfBlocks());
        } else {
            blocks = new Blocks(config.getBlocks(), config.getRandomBlockOrder());
        }
        game = new BlockShuffleGame(blocks.getBlocks(), config.getTime());
    }

    @Override
    public void onDisable() {

    }
}
