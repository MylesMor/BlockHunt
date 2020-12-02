package dev.mylesmor.blockshuffle.game;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.util.*;
import dev.mylesmor.blockshuffle.data.Blocks;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BlockShuffleGame {

    //TODO STATUS

    private ArrayList<Material> blocks;
    private int position = 0;
    private Material currentBlock = null;

    private int timeEachRound;

    private int timeRemaining;
    private int timeRemainingTaskID;

    public BlockShuffleGame(int time) {
        this.timeEachRound = time;
        this.timeRemaining = time / 20;
        blocks = Blocks.getBlocks();
    }

    public void start() {
        BlockShuffle.status = Status.INGAME;
        chooseNextBlock();
    }

    public Material getCurrentBlock() {
        return currentBlock;
    }

    public void chooseNextBlock() {
        BlockShuffle.players.replaceAll( (k, v)->v=false);
        currentBlock = blocks.get(position);
        for (Player p : BlockShuffle.players.keySet()) {
            BlockShuffle.board.setPlayerBoard(p);
            p.sendTitle(ChatColor.GRAY + "Choosing next block...", ChatColor.YELLOW + currentBlock.toString().replace("_", " "), 10, 70, 20);
            Util.blockShuffleMessage(p, ChatColor.GRAY, "Stand on " + ChatColor.YELLOW + ChatColor.BOLD + currentBlock.toString().replace("_", " ") + ChatColor.GRAY + " and type /check", null);
        }
        for (Player p : BlockShuffle.lostPlayers.keySet()) {
            BlockShuffle.board.setPlayerBoard(p);
            Util.blockShuffleMessage(p, ChatColor.GRAY, "The players now need to find: " + ChatColor.YELLOW + ChatColor.BOLD + currentBlock.toString().replace("_", " "), null);
        }
        timeRemaining = timeEachRound / 20;
        calculateTime();
        scheduleElimination(timeEachRound);
        position++;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

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

    public void scheduleElimination(int time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BlockShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
                //TODO: Change this back to false
                boolean allOut = false;
                ArrayList<Player> lastPlayers = new ArrayList<>();
                for (Map.Entry<Player, Boolean> entry : BlockShuffle.players.entrySet()) {
                    lastPlayers.add(entry.getKey());
                    if (entry.getValue()) {
                        allOut = false;
                        break;
                    }
                }
                if (!allOut) {
                    lastPlayers.clear();
                    Iterator<Map.Entry<Player, Boolean>> iter = BlockShuffle.players.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Player, Boolean> entry = iter.next();
                        Player player = entry.getKey();
                        if (!entry.getValue()) {
                            player.sendTitle(ChatColor.RED + "Eliminated!", "", 10, 70, 20);
                            player.setGameMode(GameMode.SPECTATOR);
                            player.getWorld().strikeLightningEffect(player.getLocation());
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 3);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.GOLD + p.getName() + ChatColor.RED + " has been eliminated! " + ChatColor.YELLOW + (BlockShuffle.players.size()-1) + ChatColor.GRAY + " player(s) remain.", null);
                            }
                                iter.remove();
                            BlockShuffle.lostPlayers.put(player, 0);
                            //TODO: Update scoreboard
                        }
                    }
                    //TODO CHANGE THIS BACK TO <= 1
                    if (BlockShuffle.players.size() == 10) {
                        for (Map.Entry<Player, Boolean> entry : BlockShuffle.players.entrySet()) {
                            lastPlayers.add(entry.getKey());
                        }
                        endGame(lastPlayers);
                    } else {
                        chooseNextBlock();
                        BlockShuffle.board.updateBoard();
                    }
                } else {
                    endGame(lastPlayers);
                }
            }
        }, time);
    }

    public void endGame(ArrayList<Player> players) {
        StringBuilder playerString = new StringBuilder();
        for (Player p : players) {
            p.sendTitle(ChatColor.GREEN + "Winner!", "", 10, 70, 20);
            playerString.append(ChatColor.GOLD).append(p.getName()).append(ChatColor.GRAY).append(",");
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Util.blockShuffleMessage(p, ChatColor.GRAY, "The game is now over! " + ChatColor.GREEN + "Winner(s): " + playerString.toString().substring(0, playerString.toString().length()-1), null);
        }
        resetGame();

    }

    public void resetGame() {
        BlockShuffle.status = Status.LOBBY;
        //TODO
    }
}

