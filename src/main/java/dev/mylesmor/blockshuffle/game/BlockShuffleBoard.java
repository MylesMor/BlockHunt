package dev.mylesmor.blockshuffle.game;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.data.Status;
import dev.mylesmor.blockshuffle.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BlockShuffleBoard {

    Scoreboard board = null;
    Scoreboard foundBoard = null;
    HashMap<String, String> scores = new HashMap<>();

    public BlockShuffleBoard() {
        createScoreboard();
    }

    public void createScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard newScoreboard = manager.getNewScoreboard();
        Scoreboard newFoundScoreboard = manager.getNewScoreboard();

        Objective o;
        Objective o1;

        o = newScoreboard.registerNewObjective("BlockShuffle", "", Util.prefix);
        o1 = newFoundScoreboard.registerNewObjective("BlockShuffle", "", Util.prefix);

        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(Util.prefix);

        o1.setDisplaySlot(DisplaySlot.SIDEBAR);
        o1.setDisplayName(Util.prefix);


        String playersString = ChatColor.GRAY + "Players: " + ChatColor.YELLOW + BlockShuffle.players.size();
        String timeString = ChatColor.GRAY + "Time remaining: ";
        String blockString = ChatColor.GRAY + "Block: " + ChatColor.YELLOW + ChatColor.GOLD;
        String foundString = "" + ChatColor.GREEN + ChatColor.BOLD + "FOUND!";

        Score players = o.getScore(playersString);
        Score time = o.getScore(timeString);
        Score block = o.getScore(blockString);
        Score blank = o.getScore("");
        Score blank1 = o.getScore(" ");
        Score blank2 = o.getScore("  ");
        Score blank3 = o.getScore("   ");

        Score playersFound = o1.getScore(playersString);
        Score timeFound = o1.getScore(timeString);
        Score blockFound = o1.getScore(blockString);
        Score found = o1.getScore(foundString);
        Score blankFound = o1.getScore("");
        Score blank1Found = o1.getScore(" ");
        Score blank2Found = o1.getScore("  ");
        Score blank3Found = o1.getScore("   ");

        time.setScore(1);
        players.setScore(2);
        blank.setScore(3);
        blank1.setScore(4);
        block.setScore(5);
        blank2.setScore(6);
        blank3.setScore(7);

        timeFound.setScore(1);
        playersFound.setScore(2);
        blankFound.setScore(3);
        blank1Found.setScore(4);
        found.setScore(5);
        blockFound.setScore(6);
        blank2Found.setScore(7);
        blank3Found.setScore(8);

        scores.put("players", playersString);
        scores.put("time", timeString);
        scores.put("block", blockString);
        scores.put("found", foundString);
        scores.put("playersFound", playersString);
        scores.put("timeFound", timeString);
        scores.put("blockFound", blockString);


        this.board = newScoreboard;
        this.foundBoard = newFoundScoreboard;
    }

    public void updateBoard() {
        Objective o = board.getObjective("BlockShuffle");
        Objective o1 = foundBoard.getObjective("BlockShuffle");

        board.resetScores(scores.get("players"));
        board.resetScores(scores.get("time"));
        board.resetScores(scores.get("block"));
        foundBoard.resetScores(scores.get("timeFound"));
        foundBoard.resetScores(scores.get("blockFound"));
        foundBoard.resetScores(scores.get("playersFound"));
        String playersString = ChatColor.GRAY + "Players remaining: " + ChatColor.YELLOW + BlockShuffle.players.size();
        Score players = o.getScore(playersString);
        Score playersFound = o1.getScore(playersString);
        playersFound.setScore(2);
        players.setScore(2);
        scores.replace("players",playersString);
        scores.replace("playersFound", playersString);


        if (BlockShuffle.status == Status.INGAME) {
            String blockString = ChatColor.GRAY + "Block: " + ChatColor.YELLOW + ChatColor.GOLD + BlockShuffle.game.getCurrentBlock();
            Score block = o.getScore(blockString);
            Score blockFound = o1.getScore(blockString);

            scores.replace("block", blockString);
            scores.replace("blockFound", blockString);

            Score time;
            Score timeFound;
            int minute = (int) TimeUnit.SECONDS.toMinutes(BlockShuffle.game.getTimeRemaining());
            int second = BlockShuffle.game.getTimeRemaining() - (minute*60);
            if (minute > 3) {
                String timeString = String.format(ChatColor.GRAY + "Time remaining: " + ChatColor.GREEN + "%02d:%02d", minute, second);
                time = o.getScore(timeString);
                timeFound = o1.getScore(timeString);
                scores.replace("time", timeString);
                scores.put("timeFound", timeString);
            } else if (minute > 0 && minute < 3) {
                String timeString = String.format(ChatColor.GRAY + "Time remaining: " + ChatColor.YELLOW + "%02d:%02d", minute, second);
                time = o.getScore(timeString);
                timeFound = o1.getScore(timeString);
                scores.replace("time", timeString);
                scores.put("timeFound", timeString);

            } else {
                String timeString = String.format(ChatColor.GRAY + "Time remaining: " + ChatColor.RED + "%02d:%02d", minute, second);
                time = o.getScore(timeString);
                timeFound = o1.getScore(timeString);
                scores.replace("time", timeString);
                scores.put("timeFound", timeString);
            }
            time.setScore(1);
            timeFound.setScore(1);
            block.setScore(5);
            blockFound.setScore(6);
        }
    }

    public void setPlayerBoard(Player p) {
        p.setScoreboard(board);
    }

    public void setFoundBoard(Player p) {
        p.setScoreboard(foundBoard);
    }


}
