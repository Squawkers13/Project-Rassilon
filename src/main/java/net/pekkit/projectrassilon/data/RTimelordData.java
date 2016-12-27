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

package net.pekkit.projectrassilon.data;

import net.pekkit.projectrassilon.api.TimelordData;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

/** This is a data object.
 * @author Squawkers13
 */
public class RTimelordData implements TimelordData {

    protected RDatabaseHandler rdh;
    protected OfflinePlayer player;

    protected int energy;
    protected int incarnation;
    protected boolean block;

    protected boolean status;

    protected HashMap<RegenTask, Integer> tasks;

    protected RTimelordData(RDatabaseHandler par1, OfflinePlayer par2) {
        rdh = par1;
        player = par2;

        energy = rdh.getPlayerRegenEnergy(player.getUniqueId());
        incarnation = rdh.getPlayerIncarnationCount(player.getUniqueId());
        block = rdh.getPlayerRegenBlock(player.getUniqueId());

        status = false;

        tasks = new HashMap<RegenTask, Integer>();
        for (RegenTask t: RegenTask.values()) {
            tasks.put(t, rdh.getPlayerTask(player.getUniqueId(), t));
        }
    }

    protected void writeToDB() {
        rdh.setPlayerRegenEnergy(player.getUniqueId(), energy);
        rdh.setPlayerIncarnationCount(player.getUniqueId(), incarnation);
        rdh.setPlayerRegenBlock(player.getUniqueId(), block);

        for (RegenTask t: RegenTask.values()) {
            rdh.setPlayerTask(player.getUniqueId(), t, tasks.get(t));
        }
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public int getRegenEnergy() {
        return energy;
    }

    public void setRegenEnergy(int i) {
        energy = i;
    }

    public int getIncarnation() {
        return incarnation;
    }

    public void setIncarnation(int i) {
        incarnation = i;
    }

    public boolean getRegenBlock() {
        return block;
    }

    public void setRegenBlock(boolean b) {
        block = b;
    }

    public boolean getRegenStatus() {
        return status;
    }

    public void setRegenStatus(boolean b) {
        status = b;
    }

    public int getRegenTask(RegenTask t) {
        return tasks.get(t);
    }

    public void setRegenTask(RegenTask t, int i) {
        tasks.put(t, i);
    }
}

