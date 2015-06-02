/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Squawkers13 <Squawkers13@pekkit.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pekkit.projectrassilon.listeners;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.RegenManager;
import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.RassilonUtils;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import static org.bukkit.Bukkit.getScheduler;

/**
 *
 * @author Squawkers13
 */
public class PlayerListener implements Listener {

    private ProjectRassilon plugin;
    private RDataHandler rdh;
    private RegenManager rm;

    public PlayerListener(ProjectRassilon instance, RDataHandler rdh, RegenManager rm) {
        this.plugin = instance;
        this.rdh = rdh;
        this.rm = rm;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player)) { //Is  it a player being damaged?
            Player player = (Player) event.getEntity();

            if (player.getHealth() - event.getDamage() <= 0.0D) { //Are they dying?

                if (player.hasPermission("projectrassilon.regen.timelord")) { //Do they have permission?

                    // --- BEGIN REGEN CHECKS ---   
                    if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) { //Not enough regeneration energy
                        return;
                    }
                    if (rdh.getPlayerRegenBlock(player.getUniqueId())) { //Blocking regeneration
                        rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                        return;
                    }
                    if (rdh.getPlayerRegenStatus(player.getUniqueId())) { //Already regenerating
                        return;
                    }
                    if (event.isCancelled()) { //Damage event already cancelled
                        return;
                    }
                    if (player.getLocation().getY() <= 0) { //In the void
                        MessageSender.sendMsg(player, "&4You cannot regenerate in the void!");
                        return;
                    }

                    event.setCancelled(true);
                    // --- END REGEN CHECKS ---
                    rm.preRegen(player);

                }
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        rdh.setPlayerRegenCount(player.getUniqueId(), plugin.getConfig().getInt("settings.regen.count"));
        
        rdh.setPlayerIncarnationCount(player.getUniqueId(), 1);

        for (RegenTask e : RegenTask.values()) {
            int task = rdh.getPlayerTask(player.getUniqueId(), e);
            getScheduler().cancelTask(task);
            rdh.setPlayerTask(player.getUniqueId(), e, 0);
        }

        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            event.setDeathMessage(player.getName() + " was killed while regenerating");
        }

    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        rdh.setPlayerRegenStatus(player.getUniqueId(), false);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (rdh.getPlayerRegenStatus(event.getPlayer().getUniqueId())) {
            if (RassilonUtils.getCurrentVersion(plugin).getIndex() >= 2) { //Bountiful is enabled :)
                RassilonUtils.sendActionBar(event.getPlayer(), "&6You are currently regenerating.");
            } else {
                MessageSender.sendMsg(event.getPlayer(), "&6You are currently regenerating.");
            }
        }
    }
}
