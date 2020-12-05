package dev.mylesmor.blockshuffle.listeners;

import dev.mylesmor.blockshuffle.BlockShuffle;
import dev.mylesmor.blockshuffle.data.Status;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerPvP(EntityDamageByEntityEvent e) {
        if (BlockShuffle.game != null) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player reciever = (Player) e.getEntity();
                if (BlockShuffle.game.getStatus() == Status.LOBBY) {
                    if (BlockShuffle.players.containsKey(damager) && BlockShuffle.players.containsKey(reciever)) {
                        e.setCancelled(true);
                    }
                } else {
                    if (!BlockShuffle.game.getPvP()) {
                        if (BlockShuffle.players.containsKey(damager) && BlockShuffle.players.containsKey(reciever)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e) {
        if (BlockShuffle.game != null) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (BlockShuffle.game.getStatus() == Status.LOBBY) {
                    if (BlockShuffle.players.containsKey(p)) {
                        e.setCancelled(true);
                    }
                } else {
                    if (!BlockShuffle.game.getHunger()) {
                        p.setFoodLevel(20);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
