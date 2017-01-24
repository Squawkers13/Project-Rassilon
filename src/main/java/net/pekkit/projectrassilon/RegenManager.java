/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Doctor Squawk
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

package net.pekkit.projectrassilon;

import net.pekkit.projectrassilon.data.RTimelordData;
import net.pekkit.projectrassilon.data.TimelordDataHandler;
import net.pekkit.projectrassilon.events.PostRegenEvent;
import net.pekkit.projectrassilon.events.PreRegenEvent;
import net.pekkit.projectrassilon.events.RegenEvent;
import net.pekkit.projectrassilon.events.SelfHealEvent;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.tasks.*;
import net.pekkit.projectrassilon.util.RassilonUtils;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;
import static org.bukkit.Bukkit.getScheduler;

/**
 *
 * @author Squawkers13
 */
public class RegenManager {

    private final ProjectRassilon plugin;
    private final TimelordDataHandler tdh;

    public RegenManager(ProjectRassilon par1, TimelordDataHandler par2) {
        plugin = par1;
        tdh = par2;
    }

    public void preRegen(Player player) {
        // --- BEGIN PRE-REGENERATION ---
        PreRegenEvent pre = new PreRegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(pre);

        if (pre.isCancelled()) {
            return; //Do not regenerate - event cancelled
        }

        RTimelordData p = tdh.getTimelordData(player);

        p.setRegenStatus(true);
        player.setNoDamageTicks(100);
        player.setInvulnerable(true);

        for (String effect : plugin.getConfig(REGEN).getStringList("regen.effects.preRegenEffects")) {
            String[] substrings = effect.split(":");

            PotionEffectType effectType = PotionEffectType.getByName(substrings[0]);
            int effectAmplifier = Integer.parseInt(substrings[1]);

            player.addPotionEffect(new PotionEffect(effectType, plugin.getConfig(REGEN).getInt("regen.durations.preRegenLength", 100), effectAmplifier, true), true);
        }

        BukkitTask preRegenEffects = new TaskPreRegenEffects(plugin, player.getUniqueId()).runTaskTimer(plugin, 20L, 20L);
        p.setRegenTask(RegenTask.PRE_REGEN_EFFECTS, preRegenEffects.getTaskId());
        // --- END PRE-REGENERATION ---

        BukkitTask regen = new TaskRegenDelay(tdh, this, player).runTaskLater(plugin, plugin.getConfig(REGEN).getInt("regen.durations.preRegenLength", 100));
        p.setRegenTask(RegenTask.REGEN_DELAY, regen.getTaskId());
    }

    public void regen(Player player) {
        RTimelordData p = tdh.getTimelordData(player);

        int preRegenEffects = p.getRegenTask(RegenTask.PRE_REGEN_EFFECTS);
        getScheduler().cancelTask(preRegenEffects);
        p.setRegenTask(RegenTask.PRE_REGEN_EFFECTS, 0);

        // --- BEGIN REGENERATION ---
        RegenEvent main = new RegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(main);

        BukkitTask regenEffects = new TaskRegenEffects(plugin, player.getUniqueId()).runTaskTimer(plugin, 3L, 3L);
        p.setRegenTask(RegenTask.REGEN_EFFECTS, regenEffects.getTaskId());

        //Play the "regeneration" sound to all players nearby ;)
        player.getLocation().getWorld().playSound(player.getLocation(), "regeneration", 2.0f, 1.0f);

        player.setHealth(player.getMaxHealth());
        player.setNoDamageTicks(150);
        player.setFallDistance(0);
        player.setFireTicks(1);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (RassilonUtils.getCurrentVersion(plugin).getIndex() >= 2) { //Bountiful is enabled :)
            RassilonUtils.getNMSHelper().sendTitle(player, "&6You have regenerated",
                    RassilonUtils.getRegenerationQuote(), 3, 7, 3);
        } else {
            MessageSender.sendMsg(player, "&6You have regenerated");
        }
        MessageSender.log(player.getName() + " has regenerated");

        int regen = p.getRegenEnergy() - plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120);

        p.setRegenEnergy(regen);

        for (String effect : plugin.getConfig(REGEN).getStringList("regen.effects.regenEffects")) {
            String[] substrings = effect.split(":");

            PotionEffectType effectType = PotionEffectType.getByName(substrings[0]);
            int effectAmplifier = Integer.parseInt(substrings[1]);

            player.addPotionEffect(new PotionEffect(effectType, plugin.getConfig(REGEN).getInt("regen.durations.regenLength", 150), effectAmplifier, true), true);
        }
        // --- END REGENERATION ---

        BukkitTask postRegen = new TaskPostRegenDelay(tdh, this, player).runTaskLater(plugin, plugin.getConfig(REGEN).getInt("regen.durations.regenLength", 150));
        p.setRegenTask(RegenTask.POST_REGEN_DELAY, postRegen.getTaskId());
    }

    public void postRegen(Player player) {
        RTimelordData p = tdh.getTimelordData(player);

        int regenEffects = p.getRegenTask(RegenTask.REGEN_EFFECTS);
        getScheduler().cancelTask(regenEffects);
        p.setRegenTask(RegenTask.REGEN_EFFECTS, 0);

        // --- BEGIN POST-REGENERATION ---
        PostRegenEvent post = new PostRegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(post);

        player.setHealth(player.getMaxHealth());
        player.getWorld().createExplosion(player.getLocation(), 0.0f, false);

        player.setHealth(player.getMaxHealth());

        player.setInvulnerable(false);
        player.setFallDistance(0);
        player.setFireTicks(1);
        
        p.setIncarnation(p.getIncarnation() + 1);

        BukkitTask postRegenEffects = new TaskPostRegenEffects(plugin, player.getUniqueId()).runTaskTimer(plugin, 100L, 100L);
        p.setRegenTask(RegenTask.POST_REGEN_EFFECTS, postRegenEffects.getTaskId());

        for (String effect : plugin.getConfig(REGEN).getStringList("regen.effects.postRegenEffects")) {
            String[] substrings = effect.split(":");

            PotionEffectType effectType = PotionEffectType.getByName(substrings[0]);
            int effectAmplifier = Integer.parseInt(substrings[1]);

            player.addPotionEffect(new PotionEffect(effectType, plugin.getConfig(REGEN).getInt("regen.durations.postRegenLength", 6000), effectAmplifier, true), true);
        }
        // --- END POST-REGENERATION ---

        BukkitTask regenEnd = new TaskRegenEnd(tdh, player).runTaskLater(plugin, plugin.getConfig(REGEN).getInt("regen.durations.postRegenLength", 6000));
        p.setRegenTask(RegenTask.REGEN_END, regenEnd.getTaskId());
    }

    public void beginSelfHeal(Player player, int amount, int cost) {
        // --- BEGIN SELF HEALING ---
        SelfHealEvent event = new SelfHealEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return; //Do not regenerate - event cancelled
       }

        RTimelordData p = tdh.getTimelordData(player);

        p.setRegenEnergy(p.getRegenEnergy() - cost); //deduct cost

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, amount * 20, 1, true), true);

        //Play the "regeneration" sound to all players nearby, but at a higher pitch and lower volume
        player.getLocation().getWorld().playSound(player.getLocation(), "regeneration", 1.0f, 1.3f);

        BukkitTask selfHeal = new TaskSelfHeal(tdh, player, amount).runTaskTimer(plugin, 20L, 20L); //TODO these values
        p.setRegenTask(RegenTask.SELF_HEAL, selfHeal.getTaskId());
        // --- END SELF HEALING ---
    }

}
