package dev.mylesmor.blockshuffle.game;

import com.comphenix.net.sf.cglib.core.CollectionUtils;
import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.util.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.*;

public class BlockShuffleGame {

    private Status status;
    private ArrayList<Material> blocks;
    private int round = 0;
    private int blockNumber = 0;
    private Material currentBlock = null;

    private int timeEachRound;
    private int timeRemaining;
    private int timeRemainingTaskID;
    private int allCompleteTask;
    private int timerIncreasePitchTask;

    private int worldborder;
    private ArrayList<World> worlds;
    private Location spawnLoc;
    private int spawnRadius;

    private int maxNumberRounds = 10;
    private boolean elimination = false;
    private boolean pvp;
    private String difficulty;
    private boolean daylightCycle;
    private String timeOfDay;
    private boolean hunger;

    private int speedUpTime = 1;

    private float pitch = 1;

    private ArrayList<Player> lastPlayers = new ArrayList<>();

    /**
     * Constructor for creating a new game of BlockShuffle.
     *
     * @param time The time for each round.
     */
    public BlockShuffleGame(ArrayList<Material> blocks, int time, int worldborder, ArrayList<World> worlds,
                            Location spawnLoc, int spawnRadius, int maxNumberRounds, boolean elimination,
                            boolean pvp, String difficulty, boolean daylightCycle, String timeOfDay,
                            boolean hunger) {
        this.pvp = pvp;
        this.difficulty = difficulty;
        this.daylightCycle = daylightCycle;
        this.timeOfDay = timeOfDay;
        this.hunger = hunger;
        this.elimination = elimination;
        this.maxNumberRounds = maxNumberRounds;
        this.worlds = worlds;
        this.spawnLoc = spawnLoc;
        this.spawnRadius = spawnRadius;
        status = Status.LOBBY;
        this.worldborder = worldborder;
        this.timeEachRound = time;
        this.timeRemaining = time / 20;
        this.blocks = blocks;
        this.round = this.blockNumber;
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

    public double getSpeedUpTime() {
        return speedUpTime;
    }

    public boolean getElimination() {
        return elimination;
    }

    public int getRound() {
        return round;
    }

    public int getMaxNumberRounds() {
        return maxNumberRounds;
    }

    public boolean getPvP() {
        return pvp;
    }

    public boolean getHunger() {
        return hunger;
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
        chooseNextBlock(false);
    }

    /**
     * Teleports players to a random location inside the spawnRadius from spawnLoc.
     */
    public void teleportPlayers() {
        World spawnWorld = spawnLoc.getWorld();
        Random r = new Random();
        for (Player p : BlockShuffle.players.keySet()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.getInventory().clear();
            p.setFoodLevel(20);
            p.setSaturation(20);
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
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylightCycle);
            switch (timeOfDay) {
                case "day":
                    w.setTime(1000L);
                    break;
                case "noon":
                    w.setTime(6000L);
                    break;
                case "sunset":
                    w.setTime(12000L);
                    break;
                case "night":
                    w.setTime(13000L);
                    break;
                case "midnight":
                    w.setTime(18000L);
                    break;
            }
            if (difficulty.equalsIgnoreCase("peaceful")) {
                w.setDifficulty(Difficulty.PEACEFUL);
                w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                w.setDifficulty(Difficulty.EASY);
            } else {
                w.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
            }
            WorldBorder wb = w.getWorldBorder();
            wb.setCenter(spawnLoc);
            wb.setSize(worldborder);
        }
    }

    /**
     * Chooses the next block to find.
     */
    public void chooseNextBlock(boolean skipped) {
        speedUpTime = 1;
        blockNumber++;
        if (round > maxNumberRounds - 1) {
            endGame();
            return;
        }
        if (!skipped) {
            round++;
        }
        BlockShuffle.players.replaceAll((k, v) -> v = false);
        currentBlock = blocks.get(blockNumber);
        for (Player p : BlockShuffle.players.keySet()) {
            // Updates player scoreboard and sends the message/title
            p.sendTitle(ChatColor.GRAY + "Choosing next block...", ChatColor.YELLOW + currentBlock.toString().replace("_", " "), 10, 70, 20);
            Util.blockShuffleMessage(p, ChatColor.GRAY, "Stand on " + ChatColor.YELLOW + ChatColor.BOLD + currentBlock.toString().replace("_", " ") + ChatColor.GRAY + " and type" + ChatColor.LIGHT_PURPLE + " /check", null);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!BlockShuffle.players.containsKey(p)) {
                //TODO Add spectator scoreboard
                Util.blockShuffleMessage(p, ChatColor.GRAY, "The players now need to find: " + ChatColor.YELLOW + ChatColor.BOLD + currentBlock.toString().replace("_", " "), null);
            }
        }
        timeRemaining = timeEachRound / 20;
        calculateTime(20);
        checkForAllComplete();
    }

    /**
     * Updates the timer for each round every second.
     */
    private void calculateTime(int time) {
        timeRemainingTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                int finalTimeRemaining = (timeRemaining/speedUpTime)-1;
                if (speedUpTime == 4) {
                    finalTimeRemaining = timeRemaining/speedUpTime;
                }
                if (timeRemaining == 0) {
                    eliminate();
                    return;
                } else if (timeRemaining == (60*speedUpTime)+1 || timeRemaining == (30*speedUpTime)+1) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, 3);
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining) + ChatColor.GRAY + " seconds remaining!", null);
                    }
                } else if ( timeRemaining <= (10*speedUpTime)+1 && timeRemaining % speedUpTime == 1) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, 3F);
                        if (timeRemaining - 1 != speedUpTime+1) {
                            //TODO:
                            Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining) + ChatColor.GRAY + " seconds remaining!", null);
                        } else {
                            Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining) + ChatColor.GRAY + " second remaining!", null);
                        }
                    }
                }
                timeRemaining--;
            }
        }, 0, time);
    }

    public void checkForAllComplete() {
        allCompleteTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                boolean allComplete = true;
                for (boolean value : BlockShuffle.players.values()) {
                    if (!value) {
                        allComplete = false;
                    }
                }
                if (allComplete) {
                    speedUpTime = 4;
                    Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
                    //TODO: Put in another class
                    timerIncreasePitchTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockShuffle.plugin, new Runnable() {
                            @Override
                            public void run() {
                                if (pitch > 2) {
                                    Bukkit.getScheduler().cancelTask(timerIncreasePitchTask);
                                    return;
                                }
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3, pitch);
                                pitch += 0.1;
                            }
                        }
                    }, 0, 2);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        Util.blockShuffleMessage(p, ChatColor.GRAY, "All players have found the block. Timer speed increased" + ChatColor.DARK_RED + ChatColor.BOLD + " x4.", null);
                    }
                    calculateTime(5);
                    Bukkit.getScheduler().cancelTask(allCompleteTask);
                }
            }
        }, 0, 2);
    }

    /**
     * Checks the block the player is standing on.
     *
     * @param p The player running the command.
     */
    public void verifyBlock(Player p) {
        Material matToFind = currentBlock;
        Material m = p.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        Material halfBlock = p.getPlayer().getLocation().getBlock().getType();
        if (m.toString().equals(matToFind.toString()) || halfBlock.toString().equals(matToFind.toString())) {
            p.sendTitle(ChatColor.GREEN + "You've found the block!", "", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
            //Util.blockShuffleMessage(p, ChatColor.GREEN, "Well done! Now continue gathering materials until the next block!", null);
            BlockShuffle.players.replace(p, true);
            int score = BlockShuffle.scores.get(p);
            Util.blockShuffleMessage(p, ChatColor.GRAY, "Score " + ChatColor.LIGHT_PURPLE + "+" + timeRemaining, null);
            BlockShuffle.scores.replace(p, timeRemaining + score);
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 10, 3);
            Util.blockShuffleMessage(p, ChatColor.RED, "That is not the correct block! " + ChatColor.GRAY
                    + "You are standing on " + ChatColor.RED + m.toString().replace("_", " ")
                    + ChatColor.GRAY + ". You need to stand on: " + ChatColor.YELLOW + ChatColor.BOLD
                    + matToFind.toString().replace("_", " ") + ChatColor.GRAY + ".", null);
        }
    }

    /**
     * Called at the end of the round, determining eliminations (if enabled) and whether the game is over.
     */
    public void eliminate() {
        Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
        // Determines whether all the remaining players have failed at this block
        if (elimination) {
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
                    chooseNextBlock(false);
                }
            } else {
                endGame();
            }
        } else {
            chooseNextBlock(false);
        }
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
                if (elimination) {
                    player.sendTitle(ChatColor.RED + "Eliminated!", "", 10, 70, 20);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                } else {
                    player.sendTitle(ChatColor.RED + "Game over!", "", 10, 70, 20);
                    player.setGameMode(GameMode.SPECTATOR);
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (elimination) {
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.GOLD + player.getName() + ChatColor.RED + " has been eliminated! " + ChatColor.YELLOW + (BlockShuffle.players.size()-1) + ChatColor.GRAY + " player(s) remain.", null);
                    }
                }
            }
        }
        Player winner = maxEntry.getKey();
        winner.sendTitle(ChatColor.GREEN + "Winner!", "", 10, 70, 20);
        Bukkit.getScheduler().cancelTask(allCompleteTask);
        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 2f);
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

    public void skip() {
        Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
        Bukkit.getScheduler().cancelTask(allCompleteTask);
        chooseNextBlock(true);
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

