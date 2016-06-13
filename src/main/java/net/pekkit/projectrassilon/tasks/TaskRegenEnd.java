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

import net.pekkit.projectrassilon.data.RTimelordData;
import net.pekkit.projectrassilon.data.TimelordDataHandler;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.getScheduler;

/**
 *
 * @author Squawkers13
 */
public class TaskRegenEnd extends BukkitRunnable {
    private final TimelordDataHandler tdh;
    private final Player player;

    /**
     *
     * @param par1
     * @param par2
     */
    public TaskRegenEnd(TimelordDataHandler par1, Player par2) {
        tdh = par1;
        player = par2;

    }

    @Override
    public void run() {
        RTimelordData p = tdh.getTimelordData(player);

        int postRegenEffects = p.getRegenTask(RegenTask.POST_REGEN_EFFECTS);
        getScheduler().cancelTask(postRegenEffects);
        p.setRegenTask(RegenTask.POST_REGEN_EFFECTS, 0);

        p.setRegenStatus(false);

        p.setRegenTask(RegenTask.REGEN_END, 0);
    }

}
