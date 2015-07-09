package com.smithb99.projectrassilon.tasks;

import com.google.inject.Inject;
import com.smithb99.projectrassilon.ProjectRassilon;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.player.Player;

import java.util.UUID;

public class TaskPreRegenEffects extends BukkitRunnable {
    private ProjectRassilon plugin;
    private UUID uuid;

    @Inject
    private Game game;

    private GameRegistry registry = game.getRegistry();

    public TaskPreRegenEffects(ProjectRassilon instance, UUID uuid) {
        plugin = instance;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        int height = 1;
        double interval = 0.05D;

        for (double i = -height; i < height; i += interval) {
            try {
                Player p = Bukkit.getServer().getPlayer(uuid);
                ParticleType effectType = ParticleTypes.FLAME;

                p.spawnParticles(registry.getParticleEffectBuilder(effectType).build(), p.getLocation().getPosition());
            } catch (NullPointerException e) {

            }
        }
    }
}
