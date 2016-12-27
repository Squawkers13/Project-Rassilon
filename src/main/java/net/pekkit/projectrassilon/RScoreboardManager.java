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

package net.pekkit.projectrassilon;

import net.pekkit.projectrassilon.api.TimelordData;
import net.pekkit.projectrassilon.data.TimelordDataHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;

public class RScoreboardManager {

    private ProjectRassilon plugin;
    private TimelordDataHandler tdh;

    ScoreboardManager manager;

    public RScoreboardManager(ProjectRassilon par1, TimelordDataHandler par2) {
        plugin = par1;
        tdh = par2;

        manager = plugin.getServer().getScoreboardManager();
    }

    public void setScoreboardForPlayer(final Player player, SidebarType regenSidebar, TimelordData timelordData) {
        Scoreboard scoreboard = manager.getNewScoreboard(); //Every call of this clears the scoreboard! :)

        switch (regenSidebar) {
            case REGEN_STATUS:
                setRegenStatusSidebar(scoreboard, tdh.getTimelordData(player), false);
                break;
            case REGEN_STATUS_OTHER:
                setRegenStatusSidebar(scoreboard, timelordData, true);
                break;
            case REGEN_COSTS:
                setRegenCostsSidebar(scoreboard);
                break;
            default:
                break;
        }

        if (regenSidebar != SidebarType.NONE) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        setScoreboardForPlayer(player, SidebarType.NONE);
                    }
                }
            }, 250L);
        }

        player.setScoreboard(scoreboard);
    }

    public void setScoreboardForPlayer(Player player, SidebarType regenSidebar) {
        setScoreboardForPlayer(player, regenSidebar, tdh.getTimelordData(player));
    }

    public enum SidebarType {
        NONE,
        REGEN_STATUS,
        REGEN_STATUS_OTHER,
        REGEN_COSTS
    }

    private Scoreboard setRegenStatusSidebar(Scoreboard scoreboard, TimelordData data, boolean showName) {
        Objective objective = scoreboard.registerNewObjective("PR_RegenStatus", "RegenStatus");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if(showName) {
            objective.setDisplayName(ChatColor.GOLD + "Status: " + data.getOfflinePlayer().getName());
        } else {
            objective.setDisplayName(ChatColor.GOLD + "Regeneration Status");
        }

        ChatColor colorFalse = ChatColor.RED;
        ChatColor colorTrue = ChatColor.GREEN;


        Score incarnation = objective.getScore(ChatColor.YELLOW + "Current Incarnation");
        incarnation.setScore(data.getIncarnation());
        Score energy = objective.getScore(ChatColor.YELLOW + "Available Energy");
        energy.setScore(data.getRegenEnergy());
        Score block = objective.getScore(((data.getRegenBlock()) ? colorTrue : colorFalse) + "Regeneration Blocked?");
        block.setScore((data.getRegenBlock()) ? 1 : 0);
        Score status = objective.getScore(((data.getRegenStatus()) ? colorTrue : colorFalse) + "Currently Regenerating?");
        status.setScore((data.getRegenStatus()) ? 1 : 0);

        return scoreboard;
    }

    private Scoreboard setRegenCostsSidebar(Scoreboard scoreboard) {
        Objective objective = scoreboard.registerNewObjective("PR_RegenCosts", "RegenCosts");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + "Energy Costs");

        Score startingEnergy = objective.getScore(ChatColor.YELLOW + "Starting Energy");
        startingEnergy.setScore(plugin.getConfig(REGEN).getInt("regen.costs.startingEnergy", 1500));

        Score regenCost = objective.getScore(ChatColor.YELLOW + "Cost to Regenerate");
        regenCost.setScore(plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120));

        Score healPerHP = objective.getScore(ChatColor.YELLOW + "Heal Cost Per HP");
        healPerHP.setScore(plugin.getConfig(REGEN).getInt("regen.costs.healCostPerHP", 5));

        Score maxHealCost = objective.getScore(ChatColor.YELLOW + "Maximum Heal Cost");
        maxHealCost.setScore(plugin.getConfig(REGEN).getInt("regen.costs.maximumHealCost", 100));

        return scoreboard;
    }
}
