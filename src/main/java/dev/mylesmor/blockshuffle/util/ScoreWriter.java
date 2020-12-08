package dev.mylesmor.blockshuffle.util;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.game.BlockShuffleGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class ScoreWriter {

    BlockShuffleGame game;

    public ScoreWriter(BlockShuffleGame game) {
        this.game = game;
    }


    /**
     * Writes the scores at the end of a game to scores.csv in the BlockShuffle plugin folder.
     */
    public void saveScores() {
        try {
            File file = new File(BlockShuffle.plugin.getDataFolder() + "/scores" + Instant.now().getEpochSecond() + ".csv");
            if (file.createNewFile()) {
                Bukkit.getLogger().info("[BLOCKSHUFFLE] File created: " + file.getName());
            }
        } catch (IOException e) {
            Bukkit.getLogger().warning("[BLOCKSHUFFLE] An error occurred whilst creating scores.csv!");
            e.printStackTrace();
        }
        File file = new File(BlockShuffle.plugin.getDataFolder() + "/scores.csv");
        if (file.exists()) {
            ArrayList<String> lines = convertScores();
            try {
                new PrintWriter(BlockShuffle.plugin.getDataFolder() + "/scores.csv").close();
                FileWriter fw = new FileWriter(file);
                fw.append("Name");
                fw.append(",");
                fw.append("Score");
                fw.append("\n");
                for (String line : lines) {
                    fw.append(line);
                }
                fw.flush();
                Bukkit.getLogger().info("[BLOCKSHUFFLE] Successfully written scores to scores.csv!");
                fw.close();
            } catch (IOException e) {
                Bukkit.getLogger().warning("[BLOCKSHUFFLE] Failed to write scores to scores.csv!");
            }
        }
    }

    /**
     * Converts the scores hashmap into a format for saving to a file.
     * @return An ArrayList of ArrayList<String>, which each entry containing a line to write to the file.
     */
    private ArrayList<String> convertScores() {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : BlockShuffle.scores.entrySet()) {
            String line = entry.getKey().getName() + "," + entry.getValue() + "\n";
            lines.add(line);
        }
        return lines;
    }

    private void countScoreFiles() {
        //TODO
    }


}
