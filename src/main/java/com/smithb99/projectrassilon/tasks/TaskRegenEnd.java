package com.smithb99.projectrassilon.tasks;

import com.smithb99.projectrassilon.data.RDataHandler;
import org.spongepowered.api.entity.player.Player;

public class TaskRegenEnd extends BukkitRunnable {
    private final RDataHandler rdh;
    private final Player player;

    public TaskRegenEnd(RDataHandler par1, Player par2) {
        rdh = par1;
        player = par2;
    }

    @Override
    public void run() {
        int postRegenEffects = rdh.getPlayerTask(player.getUniqueId(), RegenTask.POST_REGEN_EFFECTS);
        getScheduler().cancelTask(postRegenEffects);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.POST_REGEN_EFFECTS, 0);

        rdh.setPlayerRegenStatus(player.getUniqueId(), false);

        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_END, 0);
    }
}
