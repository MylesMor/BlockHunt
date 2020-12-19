package dev.mylesmor.blockhunt.listeners;

import dev.mylesmor.blockhunt.BlockHunt;
import dev.mylesmor.blockhunt.data.Status;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerPvP(EntityDamageByEntityEvent e) {
        if (BlockHunt.game != null) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player reciever = (Player) e.getEntity();
                if (BlockHunt.game.getStatus() == Status.LOBBY) {
                    if (BlockHunt.players.containsKey(damager) && BlockHunt.players.containsKey(reciever)) {
                        e.setCancelled(true);
                    }
                } else {
                    if (!BlockHunt.game.getPvP()) {
                        if (BlockHunt.players.containsKey(damager) && BlockHunt.players.containsKey(reciever)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e) {
        if (BlockHunt.game != null) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (BlockHunt.game.getStatus() == Status.LOBBY) {
                    if (BlockHunt.players.containsKey(p)) {
                        e.setCancelled(true);
                    }
                } else {
                    if (!BlockHunt.game.getHunger()) {
                        p.setFoodLevel(20);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
