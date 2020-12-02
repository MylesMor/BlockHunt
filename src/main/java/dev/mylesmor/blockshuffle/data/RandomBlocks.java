package dev.mylesmor.blockshuffle.data;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;

public class RandomBlocks extends Blocks {

    private ArrayList<Material> blockList = null;

    public RandomBlocks(ArrayList<String> blocks, boolean randomOrder, ArrayList<String> disallowedBlocks, int numberOfBlocks) {
        super(blocks, randomOrder);
        ArrayList<Material> allBlocks = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isBlock() && !disallowedBlocks.contains(material.name())) {
                allBlocks.add(material);
            }
        }
        Collections.shuffle(allBlocks);
        blockList = (ArrayList<Material>) allBlocks.subList(0, numberOfBlocks);
    }

    public ArrayList<Material> getBlocks() {
        return blockList;
    }
}
