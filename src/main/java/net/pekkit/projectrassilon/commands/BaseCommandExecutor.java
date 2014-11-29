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
package net.pekkit.projectrassilon.commands;

import com.google.common.collect.ImmutableList;
import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.locale.MESSAGE;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import net.pekkit.projectrassilon.RegenManager;
import static org.bukkit.Bukkit.getScheduler;

/**
 *
 * @author Squawkers13
 */
public class BaseCommandExecutor implements CommandExecutor {

    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;
    private final MessageSender ms;

    private final List<String> subCmds;

    private UUID uid;

    public BaseCommandExecutor(ProjectRassilon instance, RDataHandler rdh, RegenManager rm, MessageSender m) {
        subCmds = ImmutableList.of("?", "view", "set", "force", "block", "reload");
        plugin = instance;

        this.rdh = rdh;
        this.rm = rm;

        ms = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            ms.sendMsg(sender, "&cProject Rassilon &e" + plugin.getDescription().getVersion() + "&c, created by &eSquawkers13");
            ms.sendMsg(sender, "&cType &e/" + cmd.getName() + " ? &cfor a list of commands.");
            return true;
        }
        if (!this.subCmds.contains(args[0].toLowerCase(Locale.ENGLISH))) {
            ms.sendMsg(sender, MESSAGE.INVALID_SUB_CMD.toString());
            ms.sendMsg(sender, "&cType &e/" + cmd.getName() + " ? &cfor a list of commands.");
            return true;
        }
        if (args[0].equalsIgnoreCase("?")) {
            return helpMessage(sender, cmd.getName());
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("projectrassilon.pr.reload")) {
                return reloadConfigs(sender);
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("view")) {
            if (sender.hasPermission("projectrassilon.pr.view")) {
                return viewPlayerStats(sender, args, cmd.getName());
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (sender.hasPermission("projectrassilon.pr.set")) {
                return setCount(sender, args, cmd.getName());
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("force")) {
            if (sender.hasPermission("projectrassilon.pr.force")) {
                return force(sender, args, cmd.getName());
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("block")) {
            if (sender.hasPermission("projectrassilon.pr.block")) {
                return setBlock(sender, args, cmd.getName());
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
            return true;
        }
        return true;
    }

    public boolean reloadConfigs(CommandSender sender) {
        plugin.reloadConfig();

        if (plugin.getConfig() == null) {
            plugin.saveResource("config.yml", true);
        }
        if (plugin.getConfig().getDouble("settings.config-version", -1) != 1) {
            ms.sendMsg(sender, "&cIncompatible config detected! Renaming to config-OLD.yml");
            ms.sendMsg(sender, "&cA new config has been created, please transfer your settings.");
            ms.sendMsg(sender, "&cWhen you have finished, type &6/pr reload&c to load your settings.");
            try {
                plugin.getConfig().save(new File(plugin.getDataFolder(), "config-OLD.yml"));
            } catch (IOException ex) {
                ms.logStackTrace(ex);
            }
            plugin.saveResource("config.yml", true);
        } else {
            ms.sendMsg(sender, "&cConfigs successfully reloaded.");
        }

        return true;
    }

    private boolean helpMessage(CommandSender sender, String label) {
        ms.sendMsg(sender, "&6---------- &cProject Rassilon: &eHelp &6----------");
        if (sender.hasPermission("projectrassilon.pr.view")) {
            ms.sendMsg(sender, "&c/" + label + " &eview &6[player] &c- View a player's regneration info.");
        }
        if (sender.hasPermission("projectrassilon.pr.set")) {
            ms.sendMsg(sender, "&c/" + label + " &eset &6[player] [amount] &c- Set a player's regen count.");
        }
        if (sender.hasPermission("projectrassilon.pr.force")) {
            ms.sendMsg(sender, "&c/" + label + " &eforce &6[player] &c- Force a player to regenerate.");
        }
        if (sender.hasPermission("projectrassilon.pr.block")) {
            ms.sendMsg(sender, "&c/" + label + " &eblock &6[player] <true|false> &c- Set a player's regen block status.");
        }
        if (sender.hasPermission("projectrassilon.pr.reload")) {
            ms.sendMsg(sender, "&c/" + label + " &ereload &c- Reload the configuration.");
        }
        return true;
    }

    private boolean viewPlayerStats(CommandSender sender, String[] args, String label) {
        if (args.length < 2) {
            ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
            ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eview &6[player]");
            return true;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            ms.sendMsg(sender, MESSAGE.PLAYER_NEVER_JOINED.toString());
            return true;
        }

        ms.sendMsg(sender, "&6---------- &cRegeneration Status: &e" + player.getName() + " &6----------");
        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 0) {
            if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 1) {
                ms.sendMsg(sender, "&cThey may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more times.");
            } else {
                ms.sendMsg(sender, "&cThey may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more time.");
            }
        } else {
            ms.sendMsg(sender, "&cThey &ecannot &cregenerate.");
        }
        if (this.plugin.getConfig().getBoolean("settings.features.block", true)) {
            if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
                ms.sendMsg(sender, "&cThey are currently blocking regeneration.");
            } else {
                ms.sendMsg(sender, "&cThey are currently &enot &cblocking regeneration.");
            }
        }
        return true;
    }

    private boolean setCount(CommandSender sender, String[] args, String label) {
        if (args.length < 3) {
            ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
            ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eset &6[player] [amount]");
            ms.sendMsg(sender, "&4The amount must be positive!");
            return true;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            ms.sendMsg(sender, MESSAGE.PLAYER_NEVER_JOINED.toString());
            return true;
        }

        try {
            int count = parseInt(args[2]);
            if (count < 0) {
                ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
                ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eset &6[player] [amount]");
                ms.sendMsg(sender, "&4The amount must be positive!");
                return true;
            }

            ms.sendMsg(sender, "&cSuccessfully set &e" + args[1] + "'s &cregen count to &e" + count + "&c.");
            rdh.setPlayerRegenCount(player.getUniqueId(), count);
            return true;

        } catch (NumberFormatException ex) {
            ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
            ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eset &6[player] [amount]");
            ms.sendMsg(sender, "&4The amount must be positive!");
            return true;
        }
    }

    private boolean force(CommandSender sender, String[] args, String label) {
        if (args.length < 2) {
            ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
            ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eforce &6[player]");
            return true;
        }

        OfflinePlayer op = plugin.getServer().getOfflinePlayer(args[1]);
        Player player = op.getPlayer();

        if (player == null) {
            ms.sendMsg(sender, MESSAGE.PLAYER_NOT_ONLINE.toString());
            return true;
        }
        if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
            ms.sendMsg(sender, "&4That player cannot regenerate!");
            return true;
        }
        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            ms.sendMsg(sender, "&4That player is already regenerating!");
            return true;
        }
        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            ms.sendMsg(sender, "&4That player is blocking regeneration!");
            return true;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            ms.sendMsg(player, "&4You cannot regenerate in the void!");
            return false;
        }

        ms.sendMsg(sender, "&cSucessfully forced &e" + player.getName() + " &cto regenerate.");
        
        ms.log(sender.getName() + " forced " + player.getName() + " to regenerate");

        rm.preRegen(player);
        return true;
    }

    private boolean setBlock(CommandSender sender, String[] args, String label) {
        if (args.length < 2) {
            ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
            ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eblock &6[player] <true|false>");
            ms.sendMsg(sender, MESSAGE.TOGGLE_WARNING.toString());
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            ms.sendMsg(sender, MESSAGE.PLAYER_NEVER_JOINED.toString());
            return true;
        }

        if (args.length == 2) {
            if (rdh.getPlayerRegenBlock(player.getUniqueId()) == false) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                ms.sendMsg(sender, "&cSucessfully set &e" + args[1] + "'s &cblock to &e" + true + "&c.");
                return true;
            }
            if (rdh.getPlayerRegenBlock(player.getUniqueId()) == true) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                ms.sendMsg(sender, "&cSucessfully set &e" + args[1] + "'s &cblock to &e" + false + "&c.");
                return true;
            }
            return true;
        }

        if (args.length >= 3) {
            String block = (args[2]);
            if (!Boolean.parseBoolean(block) && !block.equalsIgnoreCase("false")) {
                ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
                ms.sendMsg(sender, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eblock &6[player] <true|false>");
                ms.sendMsg(sender, MESSAGE.TOGGLE_WARNING.toString());
                return true;
            }
            rdh.setPlayerRegenBlock(player.getUniqueId(), Boolean.parseBoolean(block));
            ms.sendMsg(sender, "&cSucessfully set &e" + args[1] + "'s &cblock to &e" + block + "&c.");
            return true;
        }
        return true;
    }
}
