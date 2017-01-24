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

package net.pekkit.projectrassilon.data;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.util.RegenTask;

import java.util.UUID;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;

class RDatabaseHandler {

    private final ProjectRassilon plugin;
    private final DatabaseManager dm;

    protected RDatabaseHandler(ProjectRassilon par1) {
        plugin = par1;

        dm = new DatabaseManager(plugin);
        dm.initDB();
        dm.createTables();
    }

    protected int getPlayerRegenEnergy(UUID player) {
        int count;
        count = dm.query("SELECT * FROM Regen WHERE UUID='" + player.toString() + "';", "energy");

        if (count == -1) {
            return plugin.getConfig(REGEN).getInt("regen.costs.startingEnergy", 1500);
        }
        return count;
    }

    protected void setPlayerRegenEnergy(UUID player, int energy) {
        if (dm.checkNull(player.toString(), "Regen")) {
            // Time to create a new row
            dm.update("INSERT INTO Regen (UUID, energy, block) VALUES ('" + player.toString() + "','" + energy + "','0'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Regen SET energy='" + energy + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

    protected boolean getPlayerRegenBlock(UUID player) {
        int block;
        block = dm.query("SELECT * FROM Regen WHERE UUID='" + player.toString() + "';", "block");

        if (block == -1) {
            return false;
        }
        return block == 1;
    }

    protected void setPlayerRegenBlock(UUID player, boolean block) {
        int blockInt = 0;
        if (block) {
            blockInt = 1;
        }
        if (dm.checkNull(player.toString(), "Regen")) {
            // Time to create a new row
            dm.update("INSERT INTO Regen (UUID, count, block) VALUES ('" + player.toString() + "','" + plugin.getConfig(REGEN).getInt("regen.costs.startingEnergy") + "','" + blockInt + "'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Regen SET block='" + blockInt + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

    protected int getPlayerTask(UUID player, RegenTask taskType) {
        int task;
        task = dm.query("SELECT * FROM Tasks WHERE UUID='" + player.toString() + "';", taskType.getColumnName());

        if (task == -1) {
            return 0;
        }
        return task;
    }

    protected void setPlayerTask(UUID player, RegenTask taskType, int taskNum) {
        if (dm.checkNull(player.toString(), "Tasks")) {
            // Time to create a new row
            dm.update("INSERT INTO Tasks (UUID, " + taskType.getColumnName() + ") VALUES ('" + player.toString() + "','" + taskNum + "'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Tasks SET " + taskType.getColumnName() + "='" + taskNum + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

    protected int getPlayerIncarnationCount(UUID player) {
        int num;
        num = dm.query("SELECT * FROM Regen WHERE UUID='" + player.toString() + "';", "incarnation");

        if (num == -1) {
            return 1;
        }

        return num;
    }

    protected void setPlayerIncarnationCount(UUID player, int num) {
        if (dm.checkNull(player.toString(), "Regen")) {
            // Time to create a new row
            dm.update("INSERT INTO Regen (UUID, incarnation, block) VALUES ('" + player.toString() + "','" + num + "','0'); ");
        } else {
            // Row already exists
            dm.update("UPDATE Regen SET incarnation='" + num + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }
}
