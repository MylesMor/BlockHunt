package dev.mylesmor.blockhunt.data;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RandomBlocks extends Blocks {

    private ArrayList<Material> blockList = null;

    public RandomBlocks(HashMap<String, ArrayList<Material>> groups, ArrayList<String> blocks, boolean randomOrder, ArrayList<String> disallowedBlocks) {
        super(groups, blocks, randomOrder);
        ArrayList<Material> allBlocks = new ArrayList<>();
        ArrayList<Material> disallowedMats = new ArrayList<>();
        for (String mat : disallowedBlocks) {
            if (groups.containsKey(mat)) {
                disallowedMats.addAll(groups.get(mat));
            }
        }
        for (Material material : Material.values()) {
            if (material.isBlock() && !disallowedBlocks.contains(material.name()) && !disallowedMats.contains(material)) {
                allBlocks.add(material);
            }
        }
        Collections.shuffle(allBlocks);
        blockList = allBlocks;
    }

    public ArrayList<Material> getBlocks() {
        return blockList;
    }
}
