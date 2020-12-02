package dev.mylesmor.blockshuffle.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class ConfigManager {

    Plugin plugin = null;
    FileConfiguration config = null;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        setupConfig();
        getConfig();
    }

    public void getConfig() {
        this.config = plugin.getConfig();
    }

    private void setupConfig() {
        plugin.saveDefaultConfig();
        saveConfig();
    }

    private void saveConfig() {
        plugin.saveConfig();
    }

    public int getTime() {
        return config.getInt("round_time") * 20;
    }

    public boolean getRandomBlockOrder() {
        return config.getBoolean("random_order");
    }

    public int getNumberOfBlocks() {
        return config.getInt("number_of_blocks");
    }

    public ArrayList<String> getBlocks() {
        return (ArrayList<String>) config.getStringList("blocks");
    }

    public ArrayList<String> getDisabledBlocks() {
        return (ArrayList<String>) config.getStringList("disabled_blocks");
    }

    public boolean getRandomBlocks() {
        return config.getBoolean("random_blocks");
    }


}
