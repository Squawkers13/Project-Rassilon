package com.smithb99.projectrassilon.api;

import com.smithb99.projectrassilon.ProjectRassilon;
import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;
import org.spongepowered.api.entity.player.Player;

public class Regenerator {
    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;

    Regenerator(ProjectRassilon par1, RDataHandler par2, RegenManager par3) {
        plugin = par1;
        rdh = par2;
        rm = par3;
    }

    public int regenerate(Player player) {
        if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
            return -1;
        }

        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            rdh.setPlayerRegenBlock(player.getUniqueId(), false);
            return -2;
        }

        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            return -3;
        }

        if (player.getLocation().getY() <= 0) {
            return -5;
        }

        rm.preRegen(player);
        return 0;
    }
}
