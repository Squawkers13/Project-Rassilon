package com.smithb99.projectrassilon;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.potion.PotionEffectTypes;

public class RegenManager {
    private final ProjectRassilon plugin;
    private final RDataHandler rdh;

    public RegenManager(ProjectRassilon par1, RDataHandler par2) {
        plugin = par1;
        rdh = par2;
    }

    public void preRegen(Player player) {
        PreRegenEvent pre = new PreRegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(pre);

        if (pre.isCancelled()) {
            return;
        }

        rdh.setPlayerRegenStatus(player.getUniqueId(), true);
        player.setNoDamageTicks(100);

        BukkitTask preRegenEffects = new BukkitTask(plugin, player.getUniqueId()).runTaskTimer(plugin, 20L, 20L);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS, preRegenEffects.getTaskID());

        BukkitTask regen = new TaskRegenDelay(rdh, this, player).runTaskLater(plugin, Constants.PRE_REGEN_LENGTH);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_DELAY, regen.getTaskId());
    }

    public void regen(Player player) {
        int preRegenEffects = rdh.getPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS);
        getScheduler().cancelTask(preRegenEffects);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.PRE_REGEN_EFFECTS, 0);

        RegenEvent main = new RegenEvent(player);
        plugin.getServer.getPluginManager().callEvent(main);

        BukkitTask regenEffects = new TaskRegenEffects(plugin, player.getUniqueId().runTaskTimer(plugin, 3L, 3L));
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_EFFECTS, regenEffects.getTaskId());

        player.setHealth(player.getMaxHealth());
        player.setNoDamageTicks(200);
        player.setFallDistance(0);
        player.setFireTicks(1);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (RassilonUtils.getCurrentVersion(plugin).getIndex() >= 2) {
            RassilonUtils.sendTitle(player, "&6You have regenerated", "", 3, 7, 3);
        } else {
            MessageSender.log("&6You have regenerated");
        }

        MessageSender.log(player.getName() + " has regenerated");

        int regen = rdh.getPlayerRegenCount(player.getUniqueId(), regen);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Constants.REGEN_LENGTH, 3, true), true);

        BukkitTask postRegen = new TaskPostRegenDelay(rdh, this, player).runTaskLater(plugin, Constants.REGEN_LENGTH);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.POST_REGEN_DELAY, postRegen.getTaskId());
    }

    public void postRegen(Player player) {
        int regenEffects = rdh.getPlayerTask(player.getUniqueId(), RegenTask.REGEN_EFFECTS);
        getScheduler().cancelTask(regenEffects);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_EFFECTS, 0);

        PostRegenEvent post = new PostRegenEvent(player);
        plugin.getServer().getPluginManager().callEvent(post);

        player.setHealth(player.getMaxHealth());
        player.getWorld().createExplosion(player.getLocation(), 0, false);

        player.setHealth(player.getMaxHealth());

        player.setFallDistance(0);
        player.setFireTicks(1);

        rdh.setPlayerIncarnationCounts(player.getUniqueId(), rdh.getPlayerIncarnationCount(player.getUniqueId()) + 1);

        BukkitTask postRegenEffects = new TaskPostRegenEffects(plugin, player.getUniqueId().runTaskTimer(plugin, 100L, 100L));
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.POST_REGEN_EFFECTS, postRegenEffects.getTaskId());

        player.addPotionEffect(new PotionEffect(PotionEffectTypes.HASTE, Constants.POST_REGEN_LENGTH, 2, true), true);
        player.addPotionEffect(new PotionEffect(PotionEffectTypes.REGENERATION, Constants.POST_REGEN_LENGTH, 3, true), true);
        player.addPotionEffect(new PotionEffect(PotionEffectTypes.HEALTH_BOOST, Constants.POST_REGEN_LENGTH / 2, 4, true), true);
        player.addPotionEffect(new PotionEffect(PotionEffectTypes.WEAKNESS, Constants.POST_REGEN_LENGTH / 2, 1, true), true);

        BukkitTask regenEnd = new TaskRegenEnd(rdh, player).runTaskLater(plugin, Constants.POST_REGEN_LENGTH);
        rdh.setPlayerTask(player.getUniqueId(), RegenTask.REGEN_END, regenEnd.getTaskId());
    }
}
