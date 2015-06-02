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

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.locale.MessageSender;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.pekkit.projectrassilon.RegenManager;

/**
 *
 * @author Squawkers13
 */
public class RegenCommandExecutor implements CommandExecutor {

    private final ProjectRassilon plugin;
    private RDataHandler rdh;
    private final RegenManager rm;

    /**
     *
     * @param instance
     * @param rdh
     */
    public RegenCommandExecutor(ProjectRassilon instance, RDataHandler rdh, RegenManager rm) {
        this.plugin = instance;
        this.rdh = rdh;
        this.rm = rm;
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
            MessageSender.sendMsg(sender, "&cYou must be a Time Lord to do that!"); // Player = Time Lord to console
            return true;
        }
        if (!sender.hasPermission("projectrassilon.regen.timelord")) {
            MessageSender.sendMsg(sender, "&cYou must be a Time Lord do that!");
            return true;
        }
        if (args.length == 0) {
            statusCommand(player);
        } else if (args[0].equalsIgnoreCase("?")) {
            helpCommand(player);
        } else if (args[0].equalsIgnoreCase("force") || args[0].equalsIgnoreCase("f")) {
            forceCommand(player);
        } else if (args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("b")) {
            blockCommand(player, args);
        } else { //invalid args
            MessageSender.sendMsg(sender, "&cI'm not sure what you mean: &e" + args[0]);
            MessageSender.sendMsg(sender, "&cType &e/regen ?&c for help.");
        }
        return true;
    }

    public void statusCommand(Player player) {
        MessageSender.sendMsg(player, "&6---------- &cRegeneration Status: &e" + player.getName() + " &6----------");
        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 0) {
            if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 1) {
                MessageSender.sendMsg(player, "&cYou may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more times.");
            } else {
                MessageSender.sendMsg(player, "&cYou may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more time.");
            }
        } else {
            MessageSender.sendMsg(player, "&cYou &ecannot &cregenerate.");
        }
        MessageSender.sendMsg(player, "&cYou are on your &e" + this.rdh.getPlayerIncarnationCount(player.getUniqueId()) + " &cincarnation.");
        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            MessageSender.sendMsg(player, "&cYou are currently regenerating.");

        } else {
            MessageSender.sendMsg(player, "&cYou are currently &enot &cregenerating.");
        }
        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            MessageSender.sendMsg(player, "&cYou are currently blocking regeneration.");
        } else {
            MessageSender.sendMsg(player, "&cYou are currently &enot &cblocking regeneration.");
        }
        MessageSender.sendMsg(player, "&cType &e/regen ? &cfor help.");
    }

    /**
     *
     * @param player
     * @param label
     */
    public void helpCommand(Player player) {
        MessageSender.sendMsg(player, "&6---------- &cRegeneration: &eHelp &6----------");
        MessageSender.sendMsg(player, "&c/regen &c- View your regeneration stats.");
        if (player.hasPermission("projectrassilon.regen.force")) {
            MessageSender.sendMsg(player, "&c/regen &ef,force &c- Force regeneration.");
        }
        if (player.hasPermission("projectrassilon.regen.block")) {
            MessageSender.sendMsg(player, "&c/regen &eb,block &6<true|false> &c- Block your next regneration.");
        }
    }

    private void forceCommand(Player player) {
        if (!player.hasPermission("projectrassilon.regen.force")) {
            MessageSender.sendMsg(player, "&cYou don't have permission to do that!");
            return;
        }
        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
            MessageSender.sendMsg(player, "&cYou cannot regenerate!");
            return;
        }
        if (this.rdh.getPlayerRegenStatus(player.getUniqueId())) {
            MessageSender.sendMsg(player, "&cYou're already regenerating!");
            return;
        }
        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            MessageSender.sendMsg(player, "&cYou must unblock regeneration first!");
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            MessageSender.sendMsg(player, "&cYou cannot regenerate in creative mode!");
            return;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            MessageSender.sendMsg(player, "&cYou cannot regenerate in the void!");
            return;
        }
        MessageSender.log(player.getName() + " forced regeneration");

        // --- END REGEN CHECKS ---
        rm.preRegen(player);
    }

    private void blockCommand(Player player, String[] args) {
        if (!player.hasPermission("projectrassilon.regen.block")) {
            MessageSender.sendMsg(player, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length == 1) {
            if (rdh.getPlayerRegenBlock(player.getUniqueId()) == false) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                MessageSender.sendMsg(player, "&cYou are now blocking your next regeneration.");
            } else if (rdh.getPlayerRegenBlock(player.getUniqueId()) == true) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                MessageSender.sendMsg(player, "&eYou are no longer blocking your next regeneration.");
            }
        } else if (args.length == 2) {
            if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                MessageSender.sendMsg(player, "&cBlock your next regeneration.");
                MessageSender.sendMsg(player, "&c/regen &eblock &6<true|false>");
            } else if (args[1].equalsIgnoreCase("true")) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                MessageSender.sendMsg(player, "&cYou are now blocking your next regeneration.");
            } else if (args[1].equalsIgnoreCase("false")) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                MessageSender.sendMsg(player, "&eYou are no longer blocking your next regeneration.");
            }
        }
    }
}
