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
package net.pekkit.projectrassilon.commands;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.RScoreboardManager;
import net.pekkit.projectrassilon.RegenManager;
import net.pekkit.projectrassilon.api.TimelordData;
import net.pekkit.projectrassilon.data.RTimelordData;
import net.pekkit.projectrassilon.data.TimelordDataHandler;
import net.pekkit.projectrassilon.locale.MessageSender;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;

/**
 *
 * @author Squawkers13
 */
public class RegenCommandExecutor implements CommandExecutor {

    private final ProjectRassilon plugin;
    private TimelordDataHandler tdh;
    private final RegenManager rm;
    private RScoreboardManager rsm;

    /**
     *
     * @param instance
     * @param tdh
     */
    public RegenCommandExecutor(ProjectRassilon instance, TimelordDataHandler tdh, RegenManager rm, RScoreboardManager rsm) {
        this.plugin = instance;
        this.tdh = tdh;
        this.rm = rm;
        this.rsm = rsm;
    }

    /**
     *
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player;
        if ((sender instanceof Player)) {
            player = (Player) sender;
        } else {
            MessageSender.sendPrefixMsg(sender, "&cYou must be a Time Lord to do that!"); // Player = Time Lord to console
            return true;
        }
        if (!sender.hasPermission("projectrassilon.regen.timelord")) {
            MessageSender.sendPrefixMsg(sender, "&cYou must be a Time Lord do that!");
            return true;
        }
        if (args.length == 0) {
            showRegenStatus(player);
        } else if (args[0].equalsIgnoreCase("info")) {
            showRegenInfo(player);
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            showCommandList(player);
        } else if (args[0].equalsIgnoreCase("costs")) {
            showRegenCosts(player);
        } else if (args[0].equalsIgnoreCase("force")) {
            forceRegen(player);
        } else if (args[0].equalsIgnoreCase("block")) {
            modifyRegenBlock(player, args);
        } else { //invalid args
            MessageSender.sendPrefixMsg(sender, "&cI'm not sure what you mean by &e" + args[0]);
            MessageSender.sendPrefixMsg(sender, "Type &e/regen ?&c for more options.");
        }
        return true;
    }

    public void showRegenStatus(Player player) {
        rsm.setScoreboardForPlayer(player, RScoreboardManager.SidebarType.REGEN_STATUS);
        MessageSender.sendPrefixMsg(player, "&cType &e/regen info&c for an explanation of this.");
    }

    public void showRegenInfo(Player player) {
        MessageSender.sendPrefixMsg(player, "&4This command will be implemented soon!"); //TODO explanation
        MessageSender.sendPrefixMsg(player, "&cType &e/regen ?&c for a list of commands.");
    }

    public void showRegenCosts(Player player) {
        rsm.setScoreboardForPlayer(player, RScoreboardManager.SidebarType.REGEN_COSTS);
        MessageSender.sendPrefixMsg(player, "&cType &e/regen info&c for an explanation of this.");
    }

    /**
     *
     * @param player
     */
    public void showCommandList(Player player) {
        MessageSender.sendMsg(player, "&6---------- &cRegeneration: &eCommands &6----------");
        MessageSender.sendMsg(player, "&c/regen &c- View your regeneration stats.");
        MessageSender.sendMsg(player, "&c/regen &einfo &c- View information about how regeneration works.");
        MessageSender.sendMsg(player, "&c/regen &ecosts &c- View the costs of regeneration.");
        MessageSender.sendMsg(player, "&6--------------------------------------------------");
        MessageSender.sendMsg(player, "&c/regen &eforce &c- Force yourself to regenerate.");
        MessageSender.sendMsg(player, "&c/regen &eblock &6<true|false> &c- Block or unblock regeneration.");
        MessageSender.sendMsg(player, "&6--------------------------------------------------");

    }

    private void forceRegen(Player player) {
        TimelordData data = tdh.getTimelordData(player);

        if (!player.hasPermission("projectrassilon.regen.force")) {
            MessageSender.sendPrefixMsg(player, "&cYou don't have permission to do that!");
            return;
        }
        if (data.getRegenEnergy() < plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120)) { //Not enough regeneration energy
            MessageSender.sendPrefixMsg(player, "&cYou don't have enough regeneration energy!");
            return;
        }
        if (data.getRegenStatus()) {
            MessageSender.sendPrefixMsg(player, "&cYou're already regenerating!");
            return;
        }
        if (data.getRegenBlock()) {
            MessageSender.sendPrefixMsg(player, "&cYou must unblock regeneration first!");
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            MessageSender.sendPrefixMsg(player, "&cYou cannot regenerate in creative mode!");
            return;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            MessageSender.sendPrefixMsg(player, "&cYou cannot regenerate in the void!");
            return;
        }
        MessageSender.log(player.getName() + " forced regeneration");

        // --- END REGEN CHECKS ---
        MessageSender.sendPrefixMsg(player, "&eYou used " + plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120) + " regeneration energy.");
        rm.preRegen(player);
    }

    private void modifyRegenBlock(Player player, String[] args) {
        RTimelordData data = tdh.getTimelordData(player);

        if (!player.hasPermission("projectrassilon.regen.block")) {
            MessageSender.sendPrefixMsg(player, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length == 1) { //Toggle command
            if (!data.getRegenBlock()) { //Not blocking
                data.setRegenBlock(true);
                MessageSender.sendPrefixMsg(player, "&cYou are now blocking your next regeneration.");
            } else if (data.getRegenBlock()) { //Blocking
                data.setRegenBlock(false);
                MessageSender.sendPrefixMsg(player, "&eYou are no longer blocking your next regeneration.");
            }
        } else if (args.length == 2) { //Additional parameters specified
            if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                MessageSender.sendPrefixMsg(player, "&cBlock your next regeneration.");
                MessageSender.sendPrefixMsg(player, "&c/regen &eblock &6<true|false>");
            } else if (args[1].equalsIgnoreCase("true")) {
                data.setRegenBlock(true);
                MessageSender.sendPrefixMsg(player, "&cYou are now blocking your next regeneration.");
            } else if (args[1].equalsIgnoreCase("false")) {
                data.setRegenBlock(false);
                MessageSender.sendPrefixMsg(player, "&eYou are no longer blocking your next regeneration.");
            }
        }
    }
}
