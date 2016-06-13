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
package net.pekkit.projectrassilon.data;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.locale.MessageSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Front end for data manipulation
 *
 * @author Squawkers13
 */
public class TimelordDataHandler {

    private ProjectRassilon plugin;
    private RDatabaseHandler rdh;

    private HashMap<UUID, RTimelordData> regenData;

    public TimelordDataHandler(ProjectRassilon instance) {
        plugin = instance;

        rdh = new RDatabaseHandler(plugin);

        regenData = new HashMap<UUID, RTimelordData>();
    }

    // TODO Only save instances to memory if the player is a Time Lord!
    public RTimelordData getTimelordData(OfflinePlayer player) {
        if (regenData.containsKey(player.getUniqueId())) {
            return regenData.get(player.getUniqueId());
        } else {
            MessageSender.log("Creating new data entry for " + player.getName() + "...");
            RTimelordData data = new RTimelordData(rdh, player);
            regenData.put(player.getUniqueId(), data);
            return data;
        }
    }

    public void removeTimelordData(Player player) {
        MessageSender.log("Saving and removing old data entry for " + player.getName() + "...");
        regenData.get(player.getUniqueId()).writeToDB();
        regenData.remove(player.getUniqueId());

    }

    public void writeAllToDB() {
        MessageSender.log("Saving stray data entries...");
        for (RTimelordData d : regenData.values()) {
            d.writeToDB();
        }
    }

}
