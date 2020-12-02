package dev.mylesmor.blockshuffle.data;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class Blocks {

    public static ArrayList<Material> getBlocks() {
        ArrayList<Material> blocks = new ArrayList<Material>();
        blocks.add(Material.GRASS_BLOCK);
        blocks.add(Material.STONE);
        blocks.add(Material.COBBLESTONE);
        return blocks;
    }
}
