/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Doctor Squawk
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
import net.pekkit.projectrassilon.RScoreboardManager;
import net.pekkit.projectrassilon.RegenManager;
import net.pekkit.projectrassilon.data.RTimelordData;
import net.pekkit.projectrassilon.data.TimelordDataHandler;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Scoreboard;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.CORE;
import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;
import static org.bukkit.Bukkit.getScheduler;

@SuppressWarnings("unused")
/**
 *
 * @author Squawkers13
 */
public class PlayerListener implements Listener {

    private ProjectRassilon plugin;
    private TimelordDataHandler tdh;
    private RegenManager rm;
    private RScoreboardManager rsm;

    public PlayerListener(ProjectRassilon instance, TimelordDataHandler tdh, RegenManager rm, RScoreboardManager rsm) {
        this.plugin = instance;
        this.tdh = tdh;
        this.rm = rm;
        this.rsm = rsm;
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

                    RTimelordData p = tdh.getTimelordData(player);

                    // --- BEGIN REGEN CHECKS ---   
                    if (p.getRegenEnergy() < plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120)) { //Not enough regeneration energy
                        return;
                    }
                    if (p.getRegenBlock()) { //Blocking regeneration
                        p.setRegenBlock(false);
                        return;
                    }
                    if (p.getRegenStatus()) { //Already regenerating
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
                    MessageSender.sendPrefixMsg(player, "& You used &e" + plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120) + " &cregeneration energy.");
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
        RTimelordData p = tdh.getTimelordData(event.getEntity());

        p.setRegenEnergy(plugin.getConfig(REGEN).getInt("regen.costs.startingEnergy"));
        
        p.setIncarnation(1);

        for (RegenTask e : RegenTask.values()) {
            int task = p.getRegenTask(e);
            getScheduler().cancelTask(task);
            p.setRegenTask(e, 0);
        }

        if (p.getRegenStatus()) {
            event.setDeathMessage(event.getEntity().getName() + " was killed while regenerating");
        }

    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        RTimelordData p = tdh.getTimelordData(event.getPlayer());

        p.setRegenStatus(false);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        RTimelordData p = tdh.getTimelordData(event.getPlayer());

        if (p.getRegenStatus()) {
            if (RassilonUtils.getCurrentVersion(plugin).getIndex() >= 2) { //Bountiful is enabled :)
                RassilonUtils.getNMSHelper().sendActionBar(event.getPlayer(), "&6You are currently regenerating.");
            } else {
                MessageSender.sendMsg(event.getPlayer(), "&6You are currently regenerating.");
            }
        }

        // Set new max health if applicable
        if (event.getPlayer().hasPermission("projectrassilon.regen.twohearts") && plugin.getConfig(CORE).getBoolean("core.regen.modifyTimelordHP", true)) {
            event.getPlayer().setMaxHealth(plugin.getConfig(CORE).getDouble("core.regen.timelordHP", 40.0));
        }

        rsm.setScoreboardForPlayer(event.getPlayer(), RScoreboardManager.SidebarType.NONE);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (tdh.getTimelordData(event.getPlayer()).getRegenStatus()) {
            MessageSender.log("Keeping data entry for " + event.getPlayer().getName() + " in memory as they are still regenerating!");
            return;
        }
        tdh.removeTimelordData(event.getPlayer());
    }
}
