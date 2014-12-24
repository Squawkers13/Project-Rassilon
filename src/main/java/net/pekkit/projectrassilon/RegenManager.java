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
package net.pekkit.projectrassilon;

import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.events.PostRegenEvent;
import net.pekkit.projectrassilon.events.PreRegenEvent;
import net.pekkit.projectrassilon.events.RegenEvent;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.tasks.TaskPostRegenDelay;
import net.pekkit.projectrassilon.tasks.TaskPostRegenEffects;
import net.pekkit.projectrassilon.tasks.TaskPreRegenEffects;
import net.pekkit.projectrassilon.tasks.TaskRegenDelay;
import net.pekkit.projectrassilon.tasks.TaskRegenEffects;
import net.pekkit.projectrassilon.tasks.TaskRegenEnd;
import net.pekkit.projectrassilon.util.Constants;
import net.pekkit.projectrassilon.util.RassilonUtils;
import net.pekkit.projectrassilon.util.RegenTask;
import static org.bukkit.Bukkit.getScheduler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Squawkers13
 */
public class RegenManager {

    private final ProjectRassilon plugin;
    private final RDataHandler rdh;

    public RegenManager(ProjectRassilon par1, RDataHandler par2) {
        plugin = par1;
        rdh = par2;
    }

    public void preRegen(Player player) {
        // --- BEGIN PRE-REGENERATION ---
        PreRegenEvent pre = new PreRegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(pre);

        if (pre.isCancelled()) {
            return; //Do not regenerate - event cancelled
        }

        rdh.setPlayerRegenStatus(player.getUniqueId(), true);
        player.setNoDamageTicks(100);

        BukkitTask preRegenEffects = new TaskPreRegenEffects(plugin, player.getUniqueId()).runTaskTimer(plugin, 20L, 20L);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS, preRegenEffects.getTaskId());
        // --- END PRE-REGENERATION ---

        BukkitTask regen = new TaskRegenDelay(rdh, this, player).runTaskLater(plugin, Constants.PRE_REGEN_LENGTH);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_DELAY, regen.getTaskId());
    }

    public void regen(Player player) {
        int preRegenEffects = rdh.getPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS);
        getScheduler().cancelTask(preRegenEffects);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS, 0);

        // --- BEGIN REGENERATION ---
        RegenEvent main = new RegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(main);

        BukkitTask regenEffects = new TaskRegenEffects(plugin, player.getUniqueId()).runTaskTimer(plugin, 3L, 3L);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_EFFECTS, regenEffects.getTaskId());

        player.setHealth(player.getMaxHealth());
        player.setNoDamageTicks(200);
        player.setFallDistance(0);
        player.setFireTicks(1);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (RassilonUtils.getCurrentVersion(plugin).getIndex() >= 2) { //Bountiful is enabled :)
            RassilonUtils.sendTitle(player, "&6You have regenerated", "", 3, 7, 3);
        } else {
            MessageSender.sendMsg(player, "&6You have regenerated");
        }
        MessageSender.log(player.getName() + " has regenerated");

        int regen = rdh.getPlayerRegenCount(player.getUniqueId()) - 1;

        rdh.setPlayerRegenCount(player.getUniqueId(), regen);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Constants.REGEN_LENGTH, 3, true), true);
        // --- END REGENERATION ---

        BukkitTask postRegen = new TaskPostRegenDelay(rdh, this, player).runTaskLater(plugin, Constants.REGEN_LENGTH);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.POST_REGEN_DELAY, postRegen.getTaskId());
    }

    public void postRegen(Player player) {
        int regenEffects = rdh.getPlayerTask(player.getUniqueId(), RegenTask.REGEN_EFFECTS);
        getScheduler().cancelTask(regenEffects);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_EFFECTS, 0);

        // --- BEGIN POST-REGENERATION ---
        PostRegenEvent post = new PostRegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(post);

        player.setHealth(player.getMaxHealth());
        player.getWorld().createExplosion(player.getLocation(), 0, false);

        player.setHealth(player.getMaxHealth());

        player.setFallDistance(0);
        player.setFireTicks(1);

        BukkitTask postRegenEffects = new TaskPostRegenEffects(plugin, player.getUniqueId()).runTaskTimer(plugin, 100L, 100L);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS, postRegenEffects.getTaskId());

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Constants.POST_REGEN_LENGTH, 1, true), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Constants.POST_REGEN_LENGTH, 1, true), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Constants.POST_REGEN_LENGTH / 2, 1, true), true);
        // --- END POST-REGENERATION ---

        BukkitTask regenEnd = new TaskRegenEnd(rdh, player).runTaskLater(plugin, Constants.POST_REGEN_LENGTH);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_END, regenEnd.getTaskId());
    }

}
