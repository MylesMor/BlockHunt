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
        file = new File(plugin.getDataFolder() + "/config.yml");
        if (!file.exists()) {
            InputStream config = plugin.getResource("config.yml");
            try {
                Files.copy(config, file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                        getElimination(prefix), getPvP(prefix), getDifficulty(prefix), getDaylightCycle(prefix),
                            getTimeOfDay(prefix), getHunger(prefix));
            games.put(preset.toLowerCase(), game);
        }
        return games;
    }

    private void saveConfig() {
        plugin.saveConfig();
    }

    public int getTime(String preset) {
        return config.getInt(preset + ".round_settings.round_time", 180) * 20;
    }

    public boolean getRandomBlockOrder(String preset) {
        return config.getBoolean(preset + ".round_settings.random_order", true);
    }

    public int getMaxNumberRounds(String preset) {
        return config.getInt(preset + ".round_settings.max_number_of_rounds", 10);
    }

    public int getWorldBorder(String preset) {
        return config.getInt(preset + ".worlds.worldborder_diameter", 1000);
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
        return config.getBoolean(preset + ".round_settings.random_blocks", false);
    }

    public ArrayList<World> getWorlds(String preset) {
        ArrayList<World> worlds = new ArrayList<>();
        worlds.add(Bukkit.getWorld(config.getString(preset + ".worlds.world_name", "world")));
        worlds.add(Bukkit.getWorld(config.getString(preset + ".worlds.nether_world_name", "world_nether")));
        worlds.add(Bukkit.getWorld(config.getString(preset + ".worlds.end_world_name", "world_the_end")));
        for (World w : worlds) {
            if (w == null) {
                Bukkit.getLogger().warning("[BLOCKSHUFFLE] Invalid world in " + preset + ".");
            }
        }
        return worlds;
    }

    public Location getSpawnLocation(String preset) {
        World world = Bukkit.getWorld(config.getString(preset + ".worlds.default_game_world_name", "world"));
        double x = config.getDouble(preset + ".worlds.spawn_location.x", 0);
        double y = config.getDouble(preset + ".worlds.spawn_location.y", 68);
        double z = config.getDouble(preset + ".worlds.spawn_location.z", 0);
        return new Location(world, x, y, z);
    }

    public int getSpawnRadius(String preset) {
        return config.getInt(preset + ".worlds.spawn_radius", 10);
    }

    public boolean getElimination(String preset) {
        return config.getBoolean(preset + ".round_settings.elimination", false);
    }

    public boolean getPvP(String preset) {
        if (!config.isBoolean(preset+".round_settings.disable_pvp")) {
            Bukkit.getLogger().warning("[BLOCKSHUFFLE] Invalid option in disable_pvp in " + preset + ".");
        }
        return !config.getBoolean(preset + ".disable_pvp", true);
    }

    public String getDifficulty(String preset) {
        String difficulty = config.getString(preset + ".round_settings.difficulty", "normal");
        if (!difficulty.equalsIgnoreCase("peaceful") && !difficulty.equalsIgnoreCase("easy")
                && !difficulty.equalsIgnoreCase("normal") && !difficulty.equalsIgnoreCase("hard")) {
            Bukkit.getLogger().warning("[BLOCKSHUFFLE] Invalid option in difficulty in " + preset + ".");
        }
        return difficulty;

    }

    public boolean getDaylightCycle(String preset) {
        if (!config.isBoolean(preset+".round_settings.daylight_cycle")) {
            Bukkit.getLogger().warning("[BLOCKSHUFFLE] Invalid option in daylight_cycle in " + preset + ".");
        }
        return config.getBoolean(preset + ".daylight_cycle", true);
    }

    public String getTimeOfDay(String preset) {
        String time = config.getString(preset + ".round_settings.time_of_day", "day");
        if (time.equalsIgnoreCase("day") || time.equalsIgnoreCase("night")
                || time.equalsIgnoreCase("midnight") || time.equalsIgnoreCase("noon")
                    || time.equalsIgnoreCase("sunset")) {
            return time;
        } else {
            Bukkit.getLogger().warning("[BLOCKSHUFFLE] " + time + " is not recognised as a valid time of day in " + preset + ".");
            return "day";
        }

    }
    public boolean getHunger(String preset) {
        if (!config.isBoolean(preset+".round_settings.disable_hunger")) {
            Bukkit.getLogger().warning("[BLOCKSHUFFLE] Invalid option in disable_hunger in " + preset + ".");
        }
        return !config.getBoolean(preset + ".disable_hunger", false);
    }
}
