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
package net.pekkit.projectrassilon.tasks;

import net.pekkit.projectrassilon.ProjectRassilon;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 *
 * @author Squawkers13
 */
public class TaskPostRegenEffects extends BukkitRunnable {

    private ProjectRassilon plugin;
    private UUID uuid;

    /**
     *
     * @param pr
     * @param u
     */
    public TaskPostRegenEffects(ProjectRassilon pr, UUID u) {
        plugin = pr;
        uuid = u;
    }

    @Override
    public void run() {
        int height = 1;
        double interval = 0.5D;
        for (double i = -height; i < height; i += interval) {
            try {
                Player p = Bukkit.getServer().getPlayer(uuid);

                p.getWorld().playEffect(p.getLocation().add(0.0D, i, 0.0D), Effect.MOBSPAWNER_FLAMES, 0);
            } catch (NullPointerException e) {
                //Player not online- don't render the effect
            }
        }
    }
}
