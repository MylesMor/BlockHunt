package dev.mylesmor.blockhunt.listeners;

import dev.mylesmor.blockhunt.BlockHunt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (BlockHunt.players.containsKey(e.getPlayer())) {
            //TODO
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (BlockHunt.players.containsKey(e.getPlayer())) {
            BlockHunt.board.setScoreboard(e.getPlayer());
        }
        //TODO
    }


}
