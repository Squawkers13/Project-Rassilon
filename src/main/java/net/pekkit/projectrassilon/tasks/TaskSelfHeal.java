/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Doctor Squawk <Squawkers13@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
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

import net.pekkit.projectrassilon.data.TimelordDataHandler;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class TaskSelfHeal extends BukkitRunnable {

    private TimelordDataHandler tdh;

    private Player player;
    private int amount;

    public TaskSelfHeal(TimelordDataHandler t, Player p, int a) {
        tdh = t;

        player = p;
        amount = a;
    }

    @Override
    public void run() {
        if (amount <= 0) { //We're done!
            cancel();
            tdh.getTimelordData(player).setRegenTask(RegenTask.SELF_HEAL, 0);
            return;
        }

        if (player.getHealth() == player.getMaxHealth()) { //At max? Stop healing
            cancel();
            tdh.getTimelordData(player).setRegenTask(RegenTask.SELF_HEAL, 0);
            return;
        }

        if (player.isDead()) {
            cancel();
            tdh.getTimelordData(player).setRegenTask(RegenTask.SELF_HEAL, 0);
            return;
        }

        if (tdh.getTimelordData(player).getRegenStatus()) { //Stop healing if real regeneration kicks in- we don't want these stacking
            cancel();
            tdh.getTimelordData(player).setRegenTask(RegenTask.SELF_HEAL, 0);
            return;
        }

        player.setHealth(player.getHealth() + 1D); //TODO test this value
        amount--; //TODO test this value

        int height = 1;
        double interval = 1D;
        for (double i = -height; i < height; i += interval) {
            player.getWorld().playEffect(player.getLocation().add(0.0D, i, 0.0D), Effect.MOBSPAWNER_FLAMES, 0);
        }
    }
}
