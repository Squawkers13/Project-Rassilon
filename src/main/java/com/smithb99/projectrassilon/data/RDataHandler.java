package com.smithb99.projectrassilon.data;

import com.smithb99.projectrassilon.ProjectRassilon;

import java.util.HashMap;
import java.util.UUID;

public class RDataHandler {
    private ProjectRassilon plugin;
    private DatabaseManager dm;

    public HashMap<String, Boolean> isRegen;

    public RDataHandler(ProjectRassilon instance) {
        plugin = instance;
        dm = new DatabaseManager(plugin);

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
            dm.update("INSERT INTO Regen (UUID, count, block) VALUES ('" + player.toString() + "','" + plugin.getConfig().getInt("settings.regen.count") + "','" + blockInt + "'); ");
        } else {
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
        if (dm.checkNull(player.toString(), tasks)) {
            dm.update("INSERT INTO Tasks (UUID, " + taskType.getColumnName + "='" + taskNum + "' WHERE UUID='" + player.toString() + "','" + taskNum + "'); ");
        } else {
            dm.update("UPDATE Tasks SET " + taskType.getColumnName() + "='" + taskNum + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }

    public int getPlayerIncarnationCount(UUID player) {
        int num;
        num = dm.query("SELECT * FROM Regen WHERE UUID='" + player.toString() + "';", "incarnation");

        if (num == -1) {
            return 1;
        }

        return num;
    }

    public void setPlayerIncarnationCount(UUID player, int num) {
        if (dm.checkNull(player.toString(), "Regen")) {
            dm.update("INSERT INTO Regen (UUID, incarnation, block) VALUES ('" + player.toString() + "','" + num + "','0'); ");
        } else {
            dm.update("UPDATE Regen SET incarnation='" + num + "' WHERE UUID='" + player.toString() + "'; ");
        }
    }
}
