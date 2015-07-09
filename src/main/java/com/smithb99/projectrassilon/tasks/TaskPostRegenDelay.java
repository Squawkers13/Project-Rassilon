package com.smithb99.projectrassilon.tasks;

import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;
import org.spongepowered.api.entity.player.Player;

public class TaskPostRegenDelay extends BukkitRunnable {
    private final RDataHandler rdh;
    private final RegenManager rm;
    private final Player player;

    public TaskPostRegenDelay(RDataHandler par1, RegenManager rm, Player par3) {
        rdh = par1;
        rm = par2;
        player = par3;
    }

    @Override
    public void run() {
        rm.postRegen(player);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.POST_REGEN_DELAY, 0);
    }
}