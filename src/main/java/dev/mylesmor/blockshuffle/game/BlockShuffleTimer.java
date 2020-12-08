package dev.mylesmor.blockshuffle.game;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BlockShuffleTimer {

    private int timeRemainingTaskID;

    private int timeRemaining;
    private int speedUpTime;

    private BlockShuffleGame game;

    public BlockShuffleTimer(BlockShuffleGame game, int timeRemaining, int speedUpTime) {
        this.game = game;
        this.timeRemaining = timeRemaining;
        this.speedUpTime = speedUpTime;
    };

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void setSpeedUpTime(int speedUpTime) {
        this.speedUpTime = speedUpTime;
    }

    public int getSpeedUpTime() {
        return speedUpTime;
    }

    /**
     * Updates the timer for each round every second.
     */
    public void startTimer(int time) {
        timeRemainingTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockShuffle.plugin, new Runnable() {
            @Override
            public void run() {
                int finalTimeRemaining = (timeRemaining/speedUpTime)-1;
                if (speedUpTime == 4) {
                    finalTimeRemaining = timeRemaining/speedUpTime;
                }
                if (timeRemaining == 0) {
                    game.eliminate();
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

    public void cancelTimer() {
        Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
    }
}
