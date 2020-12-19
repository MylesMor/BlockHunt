package dev.mylesmor.blockhunt.game;

import dev.mylesmor.blockhunt.BlockHunt;
import dev.mylesmor.blockhunt.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class BlockHuntTimer {

    private int timeRemainingTaskID;

    private int timeRemaining;
    private int speedUpTime;

    private final int roundTime;

    private final BlockHuntGame game;

    public BlockHuntTimer(BlockHuntGame game, int timeRemaining, int speedUpTime) {
        this.game = game;
        this.timeRemaining = timeRemaining;
        this.speedUpTime = speedUpTime;
        this.roundTime = timeRemaining;
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
        BlockHunt.bossBar.setVisible(true);
        BlockHunt.bossBar.setProgress(1);
        timeRemainingTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockHunt.plugin, () -> {
            int finalTimeRemaining = (timeRemaining/speedUpTime)-1;
            BlockHunt.bossBar.setProgress((double) (timeRemaining) / roundTime);
            BlockHunt.bossBar.setTitle(getTimeRemainingString());
            if (timeRemaining == 0) {
                game.eliminate();
                return;
            } else if (timeRemaining == (60*speedUpTime) || timeRemaining == (30*speedUpTime)) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 3, 1);
                    if (finalTimeRemaining != 0) {
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining+1) + ChatColor.GRAY + " seconds remaining!", null);
                    } else {
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining+1) + ChatColor.GRAY + " second remaining!", null);
                    }
                }
            } else if ( timeRemaining <= (10*speedUpTime) && timeRemaining % speedUpTime == 0) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 3, 1);
                    if (finalTimeRemaining != 0) {
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining+1) + ChatColor.GRAY + " seconds remaining!", null);
                    } else {
                        Util.blockShuffleMessage(p, ChatColor.RED, ChatColor.BOLD + Integer.toString(finalTimeRemaining+1) + ChatColor.GRAY + " second remaining!", null);
                    }
                }
            }
            timeRemaining--;
        }, 0, time);
    }

    public String getTimeRemainingString() {
        int minute = (int) TimeUnit.SECONDS.toMinutes(timeRemaining);
        int second = timeRemaining - (minute * 60);
        String timeString;
        if (minute >= 3 && !(minute == 3 && second == 0)) {
            timeString = String.format(ChatColor.GRAY + "Time remaining: " + ChatColor.GREEN + ChatColor.BOLD + "%02d:%02d", minute, second);
            BlockHunt.bossBar.setColor(BarColor.GREEN);
        } else if (minute > 0 && !(minute == 1 && second == 0)) {
            timeString = String.format(ChatColor.GRAY + "Time remaining: " + ChatColor.YELLOW + ChatColor.BOLD + "%02d:%02d", minute, second);
            BlockHunt.bossBar.setColor(BarColor.YELLOW);
        } else {
            timeString = String.format(ChatColor.GRAY + "Time remaining: " + ChatColor.RED + ChatColor.BOLD + "%02d:%02d", minute, second);
            BlockHunt.bossBar.setColor(BarColor.RED);
        }
        if (speedUpTime != 1) {
            timeString += "" + ChatColor.DARK_RED + ChatColor.BOLD + " x" + speedUpTime;
        }
        return timeString;
    }

    public void cancelTimer() {
        Bukkit.getScheduler().cancelTask(timeRemainingTaskID);
    }
}
