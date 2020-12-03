package dev.mylesmor.blockshuffle.listeners;

import dev.mylesmor.blockshuffle.BlockShuffle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (BlockShuffle.players.containsKey(e.getPlayer())) {
            //TODO
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //TODO
    }


}
