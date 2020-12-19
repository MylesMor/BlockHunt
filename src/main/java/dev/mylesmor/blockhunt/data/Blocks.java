package dev.mylesmor.blockhunt.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Blocks {

    private ArrayList<Material> blocks = new ArrayList<>();

    public Blocks(HashMap<String, ArrayList<Material>> groups, ArrayList<String> blocks, boolean randomOrder) {
        for (String block : blocks) {
            if (groups.containsKey(block)) {
                this.blocks.addAll(groups.get(block));
            } else {
                Material m = Material.getMaterial(block.toUpperCase());
                if (m != null && !this.blocks.contains(m) && m.isBlock()) {
                        this.blocks.add(m);
                } else if (m == null) {
                    Bukkit.getLogger().warning("[BLOCKSHUFFLE] Material " + block + " is invalid! Skipping...");
                }
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
