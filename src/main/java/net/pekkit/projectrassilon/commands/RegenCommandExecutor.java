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
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.pekkit.projectrassilon.RegenManager;

import static org.bukkit.Bukkit.getScheduler;

/**
 *
 * @author Squawkers13
 */
public class RegenCommandExecutor implements CommandExecutor {

    private ProjectRassilon plugin;
    private RDataHandler rdh;
    private final RegenManager rm;
    private MessageSender ms;

    /**
     *
     */
    public final List<String> subCmds = ImmutableList.of("?", "force", "block");

    /**
     *
     * @param instance
     * @param rdh
     * @param m
     */
    public RegenCommandExecutor(ProjectRassilon instance, RDataHandler rdh, RegenManager rm, MessageSender m) {
        this.plugin = instance;
        this.rdh = rdh;
        this.rm = rm;
        ms = m;
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
            ms.sendMsg(sender, MESSAGE.MUST_BE_PLAYER.toString());
            return true;
        }
        if (!sender.hasPermission("projectrassilon.regen.cmd")) {
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
            return true;
        }
        if (args.length == 0) {
            ms.sendMsg(sender, "&6---------- &cRegeneration Status: &e" + player.getName() + " &6----------");
            if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 0) {
                if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 1) {
                    ms.sendMsg(sender, "&cYou may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more times.");
                } else {
                    ms.sendMsg(sender, "&cYou may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more time.");
                }
            } else {
                ms.sendMsg(sender, "&cYou &ecannot &cregenerate.");
            }
            if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
                ms.sendMsg(sender, "&cYou are currently regenerating.");

            } else {
                ms.sendMsg(sender, "&cYou are currently &enot &cregenerating.");
            }
            if (this.plugin.getConfig().getBoolean("settings.regen-types.block", true)) {
                if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
                    ms.sendMsg(sender, "&cYou are currently blocking regeneration.");
                } else {
                    ms.sendMsg(sender, "&cYou are currently &enot &cblocking regeneration.");
                }
            }
            ms.sendMsg(sender, "&cType &e/" + cmd.getName() + " ? &cfor a list of commands.");
            return true;
        }
        if (!this.subCmds.contains(args[0].toLowerCase(Locale.ENGLISH))) {
            ms.sendMsg(sender, MESSAGE.INVALID_ARGS.toString());
            ms.sendMsg(sender, "&cType &e/" + cmd.getName() + " ? &cfor a list of commands.");
            return true;
        }
        if (args[0].equalsIgnoreCase("?")) {
            return helpMessage(player, cmd.getName());
        }
        if (args[0].equalsIgnoreCase("force")) {
            if (sender.hasPermission("projectrassilon.regen.force")) {
                return forceRegen(player);
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
        }
        if (args[0].equalsIgnoreCase("block")) {
            if (sender.hasPermission("projectrassilon.regen.block")) {
                return blockRegen(player, args, cmd.getName());
            }
            ms.sendMsg(sender, MESSAGE.NO_PERMS.toString());
        }
        return true;
    }

    /**
     *
     * @param player
     * @param label
     * @return
     */
    public boolean helpMessage(Player player, String label) {
        ms.sendMsg(player, "&6---------- &cRegeneration: &eHelp &6----------");
        ms.sendMsg(player, "&c/" + label + " &c- View your regeneration info.");
        if ((this.plugin.getConfig().getBoolean("settings.regen-types.force", true)) && (player.hasPermission("projectrassilon.regen.force"))) {
            ms.sendMsg(player, "&c/" + label + " &eforce &c- Force regeneration.");
        }
        if ((this.plugin.getConfig().getBoolean("settings.regen-types.block", true)) && (player.hasPermission("projectrassilon.regen.block"))) {
            ms.sendMsg(player, "&c/" + label + " &eblock &6<true|false> &c- Block your next regneration.");
        }
        return true;
    }

    private boolean forceRegen(Player player) {
        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
            ms.sendMsg(player, "&4You cannot regenerate!");
            return true;
        }
        if (this.rdh.getPlayerRegenStatus(player.getUniqueId())) {
            ms.sendMsg(player, "&4You're already regenerating!");
            return true;
        }
        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            ms.sendMsg(player, "&4You must unblock regeneration first!");
            return true;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            ms.sendMsg(player, "&4You cannot regenerate in creative mode!");
            return true;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            ms.sendMsg(player, "&4You cannot regenerate in the void!");
            return false;
        }
        ms.log(player.getName() + " forced regeneration");

        // --- END REGEN CHECKS ---
        rm.preRegen(player);
        return true;
    }

    private boolean blockRegen(Player player, String[] args, String label) {
        if (args.length == 1) {
            if (rdh.getPlayerRegenBlock(player.getUniqueId()) == false) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                ms.sendMsg(player, "&cYou are now blocking your next regeneration.");
                return true;
            }
            if (rdh.getPlayerRegenBlock(player.getUniqueId()) == true) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                ms.sendMsg(player, "&eYou are no longer blocking your next regeneration.");
                return true;
            }
        } else if (args.length == 2) {
            if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                ms.sendMsg(player, MESSAGE.INVALID_ARGS.toString());
                ms.sendMsg(player, MESSAGE.CORRECT_SYNTAX.toString() + " &c/" + label + " &eblock &6<true|false]>");
                ms.sendMsg(player, MESSAGE.TOGGLE_WARNING.toString());
                return true;
            }
            if (args[1].equalsIgnoreCase("true")) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                ms.sendMsg(player, "&cYou are now blocking your next regeneration.");
                return true;
            }
            if (args[1].equalsIgnoreCase("false")) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                ms.sendMsg(player, "&eYou are no longer blocking your next regeneration.");
                return true;
            }
        }
        return true;
    }
}
