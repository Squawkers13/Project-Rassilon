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
import net.pekkit.projectrassilon.data.RTimelordData;
import net.pekkit.projectrassilon.data.TimelordDataHandler;
import net.pekkit.projectrassilon.locale.MessageSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;

/**
 *
 * @author Squawkers13
 */
public class BaseCommandExecutor implements CommandExecutor {

    private final ProjectRassilon plugin;
    private final TimelordDataHandler tdh;
    private final RegenManager rm;
    private RScoreboardManager rsm;

    public BaseCommandExecutor(ProjectRassilon instance, TimelordDataHandler tdh, RegenManager rm, RScoreboardManager rsm) {
        plugin = instance;

        this.tdh = tdh;
        this.rm = rm;
        this.rsm = rsm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            MessageSender.sendMsg(sender, "&6Project Rassilon &e" + plugin.getDescription().getVersion() + "&c, created by &eDoctor Squawk");
            if (plugin.getDescription().getVersion().contains("SNAPSHOT")) {
                MessageSender.sendMsg(sender, "&4Snapshot build! Proceed with caution!");
            } else if (plugin.getDescription().getVersion().contains("PREVIEW")) {
                MessageSender.sendMsg(sender, "&cThis is a preview build- use at your own risk!");
            }
            MessageSender.sendMsg(sender, "&cType &e/pr ? &cfor more options.");
        } else if (args[0].equalsIgnoreCase("?")) {
            helpMessage(sender);
        } else if (args[0].equalsIgnoreCase("reload")) {
            reloadConfigs(sender);
        } else if (args[0].equalsIgnoreCase("view")) {
            viewPlayerStats(sender, args);
        } else if (args[0].equalsIgnoreCase("set")) {
            setCount(sender, args);
        } else if (args[0].equalsIgnoreCase("force")) {
            force(sender, args);
        } else if (args[0].equalsIgnoreCase("block")) {
            setBlock(sender, args);
        } else { //Invalid args
            MessageSender.sendPrefixMsg(sender, "&cI'm not sure what you mean by &e" + args[0]);
            MessageSender.sendPrefixMsg(sender, "&cType &e/pr ?&c for more options.");
        }
        return true;
    }

    public void reloadConfigs(CommandSender sender) {
        if (!sender.hasPermission("projectrassilon.pr.reload")) {
            MessageSender.sendPrefixMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        plugin.reloadConfigs();

        MessageSender.sendPrefixMsg(sender, "&cConfiguration files successfully reloaded.");
        /*if (plugin.getConfig().getDouble("core.configVersion", -1.0D) != Constants.CONFIG_VERSION) { TODO reimplement when configs change
            String old = plugin.getConfig().getString("core.configVersion", "OLD");
            MessageSender.sendMsg(sender, "&cIncompatible config detected! Renaming to config-" + old + ".yml");
            MessageSender.sendMsg(sender, "&cA new config has been created, please transfer your settings.");
            MessageSender.sendMsg(sender, "&cWhen you have finished, type &6/pr reload&c to load your settings.");
            try {
                plugin.getConfig().save(new File(plugin.getDataFolder(), "config-" + old + ".yml"));
            } catch (IOException ex) {
                MessageSender.logStackTrace(ex);
            }
            plugin.saveResource("core.yml", true);
        } else {
            MessageSender.sendMsg(sender, "&cConfig successfully reloaded.");
        }*/
    }

    private void helpMessage(CommandSender sender) {
        MessageSender.sendMsg(sender, "&6---------- &cProject Rassilon: &eCommands &6----------");
        if (sender.hasPermission("projectrassilon.pr.view")) {
            MessageSender.sendMsg(sender, "&c/pr &eview &6[player] &c- View a Time Lord's regeneration stats.");
        }
        if (sender.hasPermission("projectrassilon.pr.set")) {
            MessageSender.sendMsg(sender, "&c/pr &eset &6[player] [amount] &c- Set a Time Lord's regeneration energy.");
        }
        if (sender.hasPermission("projectrassilon.pr.force")) {
            MessageSender.sendMsg(sender, "&c/pr &eforce &6[player] &c- Force a Time Lord to regenerate.");
        }
        if (sender.hasPermission("projectrassilon.pr.block")) {
            MessageSender.sendMsg(sender, "&c/pr &eblock &6[player] <true|false> &c- Set a Time Lord's regen block status.");
        }
        if (sender.hasPermission("projectrassilon.pr.reload")) {
            MessageSender.sendMsg(sender, "&c/pr &ereload &c- Reload the plugin's configuration.");
            MessageSender.sendMsg(sender, "&6--------------------------------------------------");
        }
    }

    private void viewPlayerStats(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.view")) {
            MessageSender.sendPrefixMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendPrefixMsg(sender, "&cView another Time Lord's regeneration stats.");
            MessageSender.sendPrefixMsg(sender, "&c/pr &eview &6[player]");
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            MessageSender.sendPrefixMsg(sender, "&cWe have no record of that Time Lord!");
            return;
        }

        if (sender instanceof Player) {
            rsm.setScoreboardForPlayer((Player) sender, RScoreboardManager.SidebarType.REGEN_STATUS_OTHER,
                    tdh.getTimelordData(player));
        } else {
            MessageSender.sendPrefixMsg(sender, "&cYou must be a player to run this command!");
        }

    }

    private void setCount(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.set")) {
            MessageSender.sendPrefixMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 3) {
            MessageSender.sendPrefixMsg(sender, "&cSet a Time Lord's regeneration energy.");
            MessageSender.sendPrefixMsg(sender, "&c/pr &eset &6[player] [amount]");
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            MessageSender.sendPrefixMsg(sender, "&cWe have no record of that player!");
            return;
        }

        int count;

        try {
            count = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            MessageSender.sendPrefixMsg(sender, "&cThe amount must be a whole number!");
            return;
        }
        if (count < 0) {
            MessageSender.sendPrefixMsg(sender, "&cThe amount must be positive!");
            return;
        }

        RTimelordData data = tdh.getTimelordData(player);

        MessageSender.sendPrefixMsg(sender, "&cSuccessfully set &e" + args[1] + "'s &cregeneration energy to &e" + count + "&c.");
        data.setRegenEnergy(count);
    }

    private void force(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.force")) {
            MessageSender.sendPrefixMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendPrefixMsg(sender, "&cForce a Time Lord to regenerate.");
            MessageSender.sendPrefixMsg(sender, "&c/pr &eforce &6[player]");
            return;
        }

        OfflinePlayer op = plugin.getServer().getOfflinePlayer(args[1]);
        Player player = op.getPlayer();

        if (player == null) {
            MessageSender.sendPrefixMsg(sender, "&cThat player is not online!");
            return;
        }

        RTimelordData data = tdh.getTimelordData(player);

        if (data.getRegenEnergy() < plugin.getConfig(REGEN).getInt("regen.costs.regenCost", 120)) {
            MessageSender.sendPrefixMsg(sender, "&cThat player cannot regenerate!");
            return;
        }
        if (data.getRegenStatus()) {
            MessageSender.sendPrefixMsg(sender, "&cThat player is already regenerating!");
            return;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            MessageSender.sendPrefixMsg(sender, "&cThat player is in the void!");
            return;
        }

        MessageSender.sendPrefixMsg(sender, "&cSuccessfully forced &e" + player.getName() + " &cto regenerate.");

        MessageSender.log(sender.getName() + " forced " + player.getName() + " to regenerate");

        rm.preRegen(player);
    }

    private void setBlock(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.block")) {
            MessageSender.sendPrefixMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendPrefixMsg(sender, "&cSet a Time Lord's regeneration block status.");
            MessageSender.sendPrefixMsg(sender, "&c/pr &eblock &6[player] <true|false>");
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            MessageSender.sendPrefixMsg(sender, "&cWe have no record of that player!");
            return;
        }

        RTimelordData data = tdh.getTimelordData(player);

        if (args.length == 2) {
            if (!data.getRegenBlock()) {
                data.setRegenBlock(true);
                MessageSender.sendPrefixMsg(sender, "&cSuccessfully set &e" + args[1] + "'s &cblock to &e" + true + "&c.");
            } else if (data.getRegenBlock()) {
                data.setRegenBlock(false);
                MessageSender.sendPrefixMsg(sender, "&cSuccessfully set &e" + args[1] + "'s &cblock to &e" + false + "&c.");
            }
        } else if (args.length >= 3) {
            String block = (args[2]);
            if (!Boolean.parseBoolean(block) && !block.equalsIgnoreCase("false")) {
                MessageSender.sendPrefixMsg(sender, "&cThe value must be '&etrue&c' or '&efalse&c'!");
            } else {
                data.setRegenBlock(Boolean.parseBoolean(block));
                MessageSender.sendPrefixMsg(sender, "&cSuccessfully set &e" + args[1] + "'s &cblock to &e" + block + "&c.");
            }
        }
    }
}
