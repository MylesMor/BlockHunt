package dev.mylesmor.blockshuffle.game;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.util.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class BlockShuffleGame {

    private Status status;
    private ArrayList<Material> blocks;
    private int position = 0;
    private Material currentBlock = null;

    private int timeEachRound;
    private int timeRemaining;
    private int timeRemainingTaskID;

    private int worldborder;
    private ArrayList<World> worlds;
    private World nether;
    private World end;
    private Location spawnLoc;
    private int spawnRadius;

    private ArrayList<Player> lastPlayers = new ArrayList<>();

    /**
     * Constructor for creating a new game of BlockShuffle.
     * @param time The time for each round.
     */
    public BlockShuffleGame(ArrayList<Material> blocks, int time, int worldborder, ArrayList<World> worlds, Location spawnLoc, int spawnRadius) {
        this.worlds = worlds;
        this.spawnLoc = spawnLoc;
        this.spawnRadius = spawnRadius;
        status = Status.LOBBY;
        this.worldborder = worldborder;
        this.timeEachRound = time;
        this.timeRemaining = time / 20;
        this.blocks = blocks;
    }

    public Material getCurrentBlock() {
        return currentBlock;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Starts a game of BlockShuffle.
     */
    public void start() {
        for (Player p : BlockShuffle.players.keySet()) {
            BlockShuffle.scores.put(p, 0);
        }
        setupWorldBorder();
        teleportPlayers();
        BlockShuffle.game.setStatus(Status.INGAME);
        chooseNextBlock();
    }

    /**
     * Teleports players to a random location inside the spawnRadius from spawnLoc.
     */
    public void teleportPlayers() {
        World spawnWorld = spawnLoc.getWorld();
        Random r = new Random();
        for (Player p : BlockShuffle.players.keySet()) {
            int x = r.nextInt(spawnRadius - (-spawnRadius)) + -spawnRadius + (int) spawnLoc.getX();
            int z = r.nextInt(spawnRadius - (-spawnRadius)) + -spawnRadius + (int) spawnLoc.getZ();
            p.teleport(spawnWorld.getHighestBlockAt(x, z).getLocation().add(0, 1, 0));
        }
    }

    /**
     * Sets up WorldBorder for game.
     */
    public void setupWorldBorder() {
        for (World w : worlds) {
            WorldBorder wb = w.getWorldBorder();
            wb.setCenter(spawnLoc);
            wb.setSize(worldborder);
        }
    }

    /**
     * Chooses the next block to find.
     */
    public void chooseNextBlock() {
        BlockShuffle.players.replaceAll( (k, v)->v=false);
        currentBlock = blocks.get(position);
        for (Player p : BlockShuffle.players.keySet()) {
            // Updates player scoreboard and sends the message/title
            BlockShuffle.board.setPlayerBoard(p);
            p.sendTitle(ChatColor.GRAY + "Choosing next block...", ChatColor.YELLOW + currentBlock.toString().replace("_", " "), 10, 70, 20);
            Util.blockShuffleMessage(p, ChatColor.GRAY, "Stand on " + ChatColor.YELLOW + ChatColor.BOLD + currentBlock.toString().replace("_", " ") + ChatColor.GRAY + " and type /check", null);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!BlockShuffle.players.containsKey(p)) {
                BlockShuffle.board.setPlayerBoard(p);
                Util.blockShuffleMessage(p, ChatColor.GRAY, "The players now need to find: " + ChatColor.YELLOW + ChatColor.BOLD + currentBlock.toString().replace("_", " "), null);
            }
        }
        timeRemaining = timeEachRound / 20;
        calculateTime();
        scheduleElimination();
        position++;
    }

    /**
     * Updates the timer for each round every second.
     */
    private void calculateTime() {
        timeRemainingTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                if (timeRemaining == 0) {
                    return;
                } else if (timeRemaining == 61 || timeRemaining == 31) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, 3);
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(timeRemaining-1) + ChatColor.GRAY + " seconds remaining!", null);
                    }
                } else if (timeRemaining <= 11) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, 3);
                        if (timeRemaining-1 != 1) {
                            Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(timeRemaining-1) + ChatColor.GRAY + " seconds remaining!", null);
                        } else {
                            Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(timeRemaining-1) + ChatColor.GRAY + " second remaining!", null);
                        }
                    }
                }
                timeRemaining--;
                BlockShuffle.board.updateBoard();
            }
        }, 0, 20);
    }

    /**
     * Checks the block the player is standing on.
     * @param p The player running the command.
     */
    public void verifyBlock(Player p) {
        Material matToFind = currentBlock;
        Material m = p.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        Material halfBlock = p.getPlayer().getLocation().getBlock().getType();
        if (m.toString().equals(matToFind.toString()) || halfBlock.toString().equals(halfBlock.toString())) {
            p.sendTitle(ChatColor.GREEN + "You've found the block!", "", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 3);
            Util.blockShuffleMessage(p, ChatColor.GREEN, "Well done! Now continue mining and gathering materials until your next block!", null);
            BlockShuffle.players.replace(p, true);
            int score = BlockShuffle.scores.get(p);
            BlockShuffle.scores.replace(p, 1000 + timeRemaining + score);
            BlockShuffle.board.setFoundBoard(p);
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 10, 3);
            Util.blockShuffleMessage(p, ChatColor.RED, "That is not the correct block! You are standing on " + ChatColor.GOLD + m.toString().replace("_", " ") + ChatColor.RED + ". You need to stand on: " + ChatColor.GREEN + matToFind.toString().replace("_", " ") + ChatColor.RED + ".", null);
        }
    }

    /**
     * Schedules the end of each round, determining eliminations and whether the game is over.
     */
    public void scheduleElimination() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BlockShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
                // Determines whether all the remaining players have failed at this block
                if (!checkIfAllOut()) {
                    lastPlayers.clear();
                    // Eliminates players who have failed
                    checkElimination();
                    // Determine if one player remaining
                    if (BlockShuffle.players.size() == 1) {
                        for (Map.Entry<Player, Boolean> entry : BlockShuffle.players.entrySet()) {
                            lastPlayers.add(entry.getKey());
                        }
                        endGame();
                    } else {
                        // Continue the game
                        chooseNextBlock();
                        BlockShuffle.board.updateBoard();
                    }
                } else {
                    endGame();
                }
            }
        }, this.timeEachRound);
    }

    /**
     * Ends the game, declaring the winners.
     */
    private void endGame() {
        // Find player with max score
        Map.Entry<Player, Integer> maxEntry = null;
        for (Map.Entry<Player, Integer> entry : BlockShuffle.scores.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        for (Map.Entry<Player, Integer> entry : BlockShuffle.scores.entrySet()) {
            if (!entry.equals(maxEntry)) {
                Player player = entry.getKey();
                player.sendTitle(ChatColor.RED + "Eliminated!", "", 10, 70, 20);
                player.setGameMode(GameMode.SPECTATOR);
                player.getWorld().strikeLightningEffect(player.getLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 3);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.GOLD + player.getName() + ChatColor.RED + " has been eliminated! " + ChatColor.YELLOW + (BlockShuffle.players.size()-1) + ChatColor.GRAY + " player(s) remain.", null);
                }
                BlockShuffle.board.updateBoard();
            }
        }
        Player winner = maxEntry.getKey();
        winner.sendTitle(ChatColor.GREEN + "Winner!", "", 10, 70, 20);
        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 3);
        winner.setGameMode(GameMode.SPECTATOR);
        for (Player p : Bukkit.getOnlinePlayers()) {
            Util.blockShuffleMessage(p, ChatColor.GRAY, "The game is now over! " + ChatColor.GREEN + "Winner: %NAME%", winner.getName());
        }
        saveScores();
        resetGame();
    }

    /**
     * Writes the scores at the end of a game to scores.csv in the BlockShuffle plugin folder.
     */
    public void saveScores() {
        try {
            File file = new File(BlockShuffle.plugin.getDataFolder() + "/scores.csv");
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
    public ArrayList<String> convertScores() {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : BlockShuffle.scores.entrySet()) {
            String line = entry.getKey().getName() + "," + entry.getValue() + "\n";
            lines.add(line);
        }
        return lines;
    }

    /**
     * Resets the game, ready for another round.
     */
    public void resetGame() {
        BlockShuffle.game.setStatus(Status.LOBBY);
        BlockShuffle.players.clear();
        //TODO
    }

    /**
     * Checks all players to verify whether should be eliminated and eliminates them.
     */
    private void checkElimination() {
        Iterator<Map.Entry<Player, Boolean>> iter = BlockShuffle.players.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Player, Boolean> entry = iter.next();
            Player player = entry.getKey();
            Bukkit.getLogger().info("Eliminating player: " + player.getName() + " " + entry.getValue());
            if (!entry.getValue()) {
                player.sendTitle(ChatColor.RED + "Eliminated!", "", 10, 70, 20);
                player.setGameMode(GameMode.SPECTATOR);
                player.getWorld().strikeLightningEffect(player.getLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 3);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.GOLD + player.getName() + ChatColor.RED + " has been eliminated! " + ChatColor.YELLOW + (BlockShuffle.players.size()-1) + ChatColor.GRAY + " player(s) remain.", null);
                }
                iter.remove();
                BlockShuffle.board.updateBoard();
            }
        }
    }

    /**
     * Determines whether all of the last players have failed on the same block.
     * @return True if all players out, otherwise false.
     */
    private boolean checkIfAllOut() {
        boolean allOut = true;
        for (Map.Entry<Player, Boolean> entry : BlockShuffle.players.entrySet()) {
            lastPlayers.add(entry.getKey());
            if (entry.getValue()) {
                allOut = false;
                break;
            }
        }
        return allOut;
    }


}

