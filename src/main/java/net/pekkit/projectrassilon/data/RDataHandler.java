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
package net.pekkit.projectrassilon.data;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.RegenTask;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Squawkers13
 */
public class RDataHandler {

    private ProjectRassilon plugin;
    private DatabaseManager dm;

    public HashMap<String, Boolean> isRegen;

    public RDataHandler(ProjectRassilon instance, MessageSender ms) {
        plugin = instance;
        dm = new DatabaseManager(plugin, ms);

        isRegen = new HashMap<String, Boolean>();

        dm.initDB();
        dm.createTables();

    }

    public int getPlayerRegenCount(UUID player) {
        int count;
        count = dm.query("SELECT * FROM Regen WHERE UUID='" + player.toString() + "';", "count");

        if (count == -1) {
            return plugin.getConfig().getInt("settings.regen.count");
        }
        return count;
    }

    public void setPlayerRegenCount(UUID player, int count) {
        if (dm.checkNull(player.toString(), "Regen")) {
            // Time to create a new row
            dm.update("INSERT INTO Regen (UUID, count, block) VALUES ('" + player.toString() + "','" + count + "','0'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Regen SET count='" + count + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

    public boolean getPlayerRegenBlock(UUID player) {
        int block;
        block = dm.query("SELECT * FROM Regen WHERE UUID='" + player.toString() + "';", "block");

        if (block == -1) {
            return false;
        }
        return block == 1;
    }

    public void setPlayerRegenBlock(UUID player, boolean block) {
        int blockInt = 0;
        if (block) {
            blockInt = 1;
        }
        if (dm.checkNull(player.toString(), "Regen")) {
            // Time to create a new row
            dm.update("INSERT INTO Regen (UUID, count, block) VALUES ('" + player.toString() + "','" + plugin.getConfig().getInt("settings.regen.count") + "','" + blockInt + "'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Regen SET block='" + blockInt + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

    public boolean getPlayerRegenStatus(UUID player) {
        if (isRegen.containsKey(player.toString())) {
            return isRegen.get(player.toString());
        }
        return false;
    }

    public void setPlayerRegenStatus(UUID player, Boolean value) {
        isRegen.put(player.toString(), value);
    }

    public int getPlayerTask(UUID player, RegenTask taskType) {
        int task;
        task = dm.query("SELECT * FROM Tasks WHERE UUID='" + player.toString() + "';", taskType.getColumnName());

        if (task == -1) {
            return 0;
        }
        return task;
    }

    public void setPlayerTask(UUID player, RegenTask taskType, int taskNum) {
        if (dm.checkNull(player.toString(), "Tasks")) {
            // Time to create a new row
            dm.update("INSERT INTO Tasks (UUID, " + taskType.getColumnName() + ") VALUES ('" + player.toString() + "','" + taskNum + "'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Tasks SET " + taskType.getColumnName() + "='" + taskNum + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

}
