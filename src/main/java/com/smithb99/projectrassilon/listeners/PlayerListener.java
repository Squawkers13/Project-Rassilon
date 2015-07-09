package com.smithb99.projectrassilon.listeners;

import com.smithb99.projectrassilon.ProjectRassilon;
import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;
import com.smithb99.projectrassilon.util.RassilonUtils;
import com.smithb99.projectrassilon.util.RegenTask;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.EventHandler;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerDeathEvent;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerRespawnEvent;
import org.spongepowered.api.text.Texts;

public class PlayerListener implements Listener {
    private ProjectRassilon plugin;
    private RDataHandler rdh;
    private RegenManager rm;

    public PlayerListener(ProjectRassilon instance, RDataHandler rdh, RegenManager rm) {
        this.plugin = instance;
        this.rdh = rdh;
        this.rm = rm;
    }

    @Subscribe
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.getHealthData().getHealth() - event.getDamage() <= 0.0D) {
                if (player.hasPermission("projectrassilon.regen.timelord")) {
                    if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
                        return;
                    }

                    if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
                        rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                        return;
                    }

                    if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
                        return;
                    }

                    if (event.isCancelled()) {
                        return;
                    }

                    if (player.getLocation().getY() <= 0) {
                        player.sendMessage(Texts.of("You cannot regenerate in the Void."));
                        return;
                    }

                    event.setCancelled(true);

                    rm.preRegen(player);
                }
            }
        }
    }

    @Subscribe
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

    }

    @Subscribe
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getEntity();

        rdh.setPlayerRegenStatus(player.getUniqueId(), false);
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (rdh.getPlayerRegenStatus(event.getEntity().getUniqueId())) {
            if (RassilonUtils.getCurrentVersion(plugin).getIndex() >= 2) {
                RassilonUtils.sendActionBar(event.getEntity(), "You are currently regenerating.");
            } else {
                event.getEntity().sendMessage(Texts.of("You are currently regenerating."));
            }
        }
    }
}
