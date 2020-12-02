package dev.mylesmor.blockshuffle.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;

public class Blocks {

    private ArrayList<Material> blocks = new ArrayList<>();

    public Blocks(ArrayList<String> blocks, boolean randomOrder) {
        for (String block : blocks) {
            Material m = Material.getMaterial(block);
            if (m != null) {
                this.blocks.add(m);
            } else {
                Bukkit.getLogger().warning("[BLOCKSHUFFLE] Material " + block + " is invalid! Skipping...");
            }
        }
        if (randomOrder) {
            Collections.shuffle(this.blocks);
        }
    }

    public ArrayList<Material> getBlocks() {
        return blocks;
    }
}
