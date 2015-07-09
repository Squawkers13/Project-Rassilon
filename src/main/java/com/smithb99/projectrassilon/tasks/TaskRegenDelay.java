package com.smithb99.projectrassilon.tasks;

import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;
import org.spongepowered.api.entity.player.Player;

public class TaskRegenDelay extends BukkitRunnable {
    private final RDataHandler rdh;
    private final RegenManager rm;
    private final Player player;

    public TaskRegenDelay(RDataHandler par1, RegenManager par2, Player par3) {
        rdh = par1;
        rm = par2;
        player = par3;
    }

    @Override
    public void run() {
        rm.regen(player);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_DELAY, 0);
    }
}
