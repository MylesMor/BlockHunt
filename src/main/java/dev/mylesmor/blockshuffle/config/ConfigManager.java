package dev.mylesmor.blockshuffle.config;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.data.Blocks;
import dev.mylesmor.blockshuffle.data.RandomBlocks;
import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.game.BlockShuffleGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {

    Plugin plugin = null;
    FileConfiguration config = null;
    HashMap<String, ArrayList<Material>> groups = new HashMap<>();

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        setupConfig();
        getConfig();
    }

    public void getConfig() {
        this.config = plugin.getConfig();
    }

    private void setupConfig() {
        File file = new File(plugin.getDataFolder() + "/groups.yml");
        if (!file.exists()) {
            InputStream groups = plugin.getResource("groups.yml");
            try {
                Files.copy(groups, file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        plugin.saveDefaultConfig();
        saveConfig();
        getGroups();
    }

    private void getGroups() {
        File file = new File(plugin.getDataFolder() + "/groups.yml");
        YamlConfiguration groupsConfig = new YamlConfiguration();
        try {
            groupsConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        for (String key : groupsConfig.getKeys(false)) {
            ArrayList<String> blocks = (ArrayList<String>) groupsConfig.getStringList(key);
            ArrayList<Material> group = new ArrayList<>();
            for (String block : blocks) {
                Material m = Material.getMaterial(block);
                if (m != null) {
                    group.add(m);
                } else {
                    Bukkit.getLogger().warning("Cannot find material: " + block);
                }
            }
            groups.put("groups." + key, group);
        }
    }

    public HashMap<String, BlockShuffleGame> getGames() {
        HashMap<String, BlockShuffleGame> games = new HashMap<>();
        for (String preset : config.getConfigurationSection("presets").getKeys(false)) {
            String prefix = "presets." + preset;
            Blocks blocks;
            if (getRandomBlocks(prefix)) {
                blocks = new RandomBlocks(groups, getBlocks(prefix), getRandomBlockOrder(prefix), getDisabledBlocks(prefix));
            } else {
                blocks = new Blocks(groups, getBlocks(prefix), getRandomBlockOrder(prefix));
            }
            BlockShuffleGame game = new BlockShuffleGame(blocks.getBlocks(), getTime(prefix), getWorldBorder(prefix),
                    getWorlds(prefix), getSpawnLocation(prefix), getSpawnRadius(prefix), getMaxNumberRounds(prefix),
                        getElimination(preset));
            games.put(preset.toLowerCase(), game);
        }
        return games;
    }

    private void saveConfig() {
        plugin.saveConfig();
    }

    public int getTime(String preset) {
        return config.getInt(preset + ".round_time") * 20;
    }

    public boolean getRandomBlockOrder(String preset) {
        return config.getBoolean(preset + ".random_order");
    }

    public int getMaxNumberRounds(String preset) {
        return config.getInt(preset + ".max_number_of_rounds");
    }

    public int getWorldBorder(String preset) {
        return config.getInt(preset + ".worldborder_diameter");
    }

    public ArrayList<String> getBlocks(String preset) {
        return (ArrayList<String>) config.getStringList(preset + ".blocks");
    }

    public ArrayList<String> getDisabledBlocks(String preset) {
        ArrayList<String> disabledBlocks = (ArrayList<String>) config.getStringList(preset + ".disabled_blocks");
        disabledBlocks.replaceAll(String::toUpperCase);
        return disabledBlocks;
    }

    public boolean getRandomBlocks(String preset) {
        return config.getBoolean(preset + ".random_blocks");
    }

    public ArrayList<World> getWorlds(String preset) {
        ArrayList<World> worlds = new ArrayList<>();
        worlds.add(Bukkit.getWorld(config.getString(preset + ".world_name")));
        worlds.add(Bukkit.getWorld(config.getString(preset + ".nether_world_name")));
        worlds.add(Bukkit.getWorld(config.getString(preset + ".end_world_name")));
        return worlds;
    }

    public Location getSpawnLocation(String preset) {
        World world = Bukkit.getWorld(config.getString(preset + ".default_game_world_name"));
        double x = config.getDouble(preset + ".spawn_location.x");
        double y = config.getDouble(preset + ".spawn_location.y");
        double z = config.getDouble(preset + ".spawn_location.z");
        return new Location(world, x, y, z);
    }

    public int getSpawnRadius(String preset) {
        return config.getInt(preset + ".spawn_radius");
    }

    public boolean getElimination(String preset) {
        return config.getBoolean(preset + ".elimination");
    }


}
