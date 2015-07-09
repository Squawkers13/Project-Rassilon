package com.smithb99.projectrassilon.api;

import com.smithb99.projectrassilon.data.RDataHandler;
import org.spongepowered.api.entity.player.Player;

public class TimelordDataManager {
    private final RDataHandler rdh;

    public TimelordDataManager(RDataHandler r) {
        rdh = r;
    }

    public boolean getTimelordStatus(Player player) {
        return player.hasPermission("projectrassilon.regen.timelord");
    }

    public int getRegenCount(Player player) {
        return rdh.getPlayerRegenCount(player.getUniqueId());
    }

    public boolean getRegenBlock(Player player) {
        return rdh.getPlayerRegenBlock(player.getUniqueId());
    }

    public boolean getRegenStatus(Player player) {
        return rdh.getPlayerRegenStatus(player.getUniqueId());
    }

    public int getIncarnationCount(Player player) {
        return rdh.getPlayerIncarnationCount(player.getUniqueId());
    }
}
