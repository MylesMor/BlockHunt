package dev.mylesmor.blockshuffle.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

    public int getWorldBorder() {
        return config.getInt("worldborder_diameter");
    }

    public ArrayList<String> getBlocks() {
        return (ArrayList<String>) config.getStringList("blocks");
    }

    public ArrayList<String> getDisabledBlocks() {
        ArrayList<String> disabledBlocks = (ArrayList<String>) config.getStringList("disabled_blocks");
        disabledBlocks.replaceAll(String::toUpperCase);
        return disabledBlocks;
    }

    public boolean getRandomBlocks() {
        return config.getBoolean("random_blocks");
    }

    public ArrayList<World> getWorlds() {
        ArrayList<World> worlds = new ArrayList<>();
        worlds.add(Bukkit.getWorld(config.getString("world_name")));
        worlds.add(Bukkit.getWorld(config.getString("nether_world_name")));
        worlds.add(Bukkit.getWorld(config.getString("end_world_name")));
        return worlds;
    }

    public Location getSpawnLocation() {
        World world = Bukkit.getWorld(config.getString("default_game_world_name"));
        double x = config.getDouble("spawn_location.x");
        double y = config.getDouble("spawn_location.y");
        double z = config.getDouble("spawn_location.z");
        return new Location(world, x, y, z);
    }

    public int getSpawnRadius() {
        return config.getInt("spawn_radius");
    }


}
