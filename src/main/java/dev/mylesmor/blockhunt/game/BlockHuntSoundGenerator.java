package dev.mylesmor.blockhunt.game;

import dev.mylesmor.blockhunt.BlockHunt;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BlockHuntSoundGenerator {

    int playSoundTask;
    float pitch;

    public BlockHuntSoundGenerator() { }

    public void startSound(Sound sound, float startPitch, float increment, float maxPitch, float volume, int speed) {
        pitch = startPitch;
        playSoundTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockHunt.plugin, () -> {
            if (pitch > maxPitch) {
                Bukkit.getScheduler().cancelTask(playSoundTask);
                return;
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), sound, volume, pitch);
                pitch += increment;
            }
        }, 0, speed);
    }


}
