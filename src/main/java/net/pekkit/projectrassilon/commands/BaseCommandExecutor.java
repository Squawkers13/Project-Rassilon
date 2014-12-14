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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static java.lang.Integer.parseInt;
import net.pekkit.projectrassilon.RegenManager;
import net.pekkit.projectrassilon.util.Constants;

/**
 *
 * @author Squawkers13
 */
public class BaseCommandExecutor implements CommandExecutor {

    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;

    public BaseCommandExecutor(ProjectRassilon instance, RDataHandler rdh, RegenManager rm) {
        plugin = instance;

        this.rdh = rdh;
        this.rm = rm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            MessageSender.sendMsg(sender, "&cProject Rassilon &e" + plugin.getDescription().getVersion() + "&c, created by &eSquawkers13");
            MessageSender.sendMsg(sender, "&cType &e/pr ? &cfor help.");
        } else if (args[0].equalsIgnoreCase("?")) {
            helpMessage(sender);
        } else if (args[0].equalsIgnoreCase("reload") || (args[0].equalsIgnoreCase("r"))) {
            reloadConfigs(sender);
        } else if (args[0].equalsIgnoreCase("view") || (args[0].equalsIgnoreCase("v"))) {
            viewPlayerStats(sender, args);
        } else if (args[0].equalsIgnoreCase("set") || (args[0].equalsIgnoreCase("s"))) {
            setCount(sender, args);
        } else if (args[0].equalsIgnoreCase("force") || (args[0].equalsIgnoreCase("f"))) {
            force(sender, args);
        } else if (args[0].equalsIgnoreCase("block") || (args[0].equalsIgnoreCase("b"))) {
            setBlock(sender, args);
        } else { //Invalid args
            MessageSender.sendMsg(sender, "&cI'm not sure what you mean: &e" + args[0]);
            MessageSender.sendMsg(sender, "&cType &e/pr ?&c for help.");
        }
        return true;
    }

    public void reloadConfigs(CommandSender sender) {
        if (!sender.hasPermission("projectrassilon.pr.reload")) {
            MessageSender.sendMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        plugin.reloadConfig();

        if (plugin.getConfig() == null) {
            plugin.saveResource("config.yml", true);
        }
        if (plugin.getConfig().getDouble("settings.config-version", -1.0D) != Constants.CONFIG_VERSION) {
            String old = plugin.getConfig().getString("settings.config-version", "OLD");
            MessageSender.sendMsg(sender, "&cIncompatible config detected! Renaming to config-" + old + ".yml");
            MessageSender.sendMsg(sender, "&cA new config has been created, please transfer your settings.");
            MessageSender.sendMsg(sender, "&cWhen you have finished, type &6/pr reload&c to load your settings.");
            try {
                plugin.getConfig().save(new File(plugin.getDataFolder(), "config-" + old + ".yml"));
            } catch (IOException ex) {
                MessageSender.logStackTrace(ex);
            }
            plugin.saveResource("config.yml", true);
        } else {
            MessageSender.sendMsg(sender, "&cConfig successfully reloaded.");
        }
    }

    private void helpMessage(CommandSender sender) {
        MessageSender.sendMsg(sender, "&6---------- &cProject Rassilon: &eHelp &6----------");
        if (sender.hasPermission("projectrassilon.pr.view")) {
            MessageSender.sendMsg(sender, "&c/pr &ev,view &6[player] &c- View a player's regneration info.");
        }
        if (sender.hasPermission("projectrassilon.pr.set")) {
            MessageSender.sendMsg(sender, "&c/pr &es,set &6[player] [amount] &c- Set a player's regen count.");
        }
        if (sender.hasPermission("projectrassilon.pr.force")) {
            MessageSender.sendMsg(sender, "&c/pr &ef,force &6[player] &c- Force a player to regenerate.");
        }
        if (sender.hasPermission("projectrassilon.pr.block")) {
            MessageSender.sendMsg(sender, "&c/pr &eb,block &6[player] <true|false> &c- Set a player's regen block status.");
        }
        if (sender.hasPermission("projectrassilon.pr.reload")) {
            MessageSender.sendMsg(sender, "&c/pr &er,reload &c- Reload the configuration.");
        }
    }

    private void viewPlayerStats(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.view")) {
            MessageSender.sendMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendMsg(sender, "&cView another player's regeneration info.");
            MessageSender.sendMsg(sender, "&c/pr &eview &6[player]");
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            MessageSender.sendMsg(sender, "&cThat player has never played before!");
            return;
        }

        MessageSender.sendMsg(sender, "&6---------- &cRegeneration Status: &e" + player.getName() + " &6----------");
        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 0) {
            if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 1) {
                MessageSender.sendMsg(sender, "&cThey may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more times.");
            } else {
                MessageSender.sendMsg(sender, "&cThey may regenerate &e" + this.rdh.getPlayerRegenCount(player.getUniqueId()) + "&c more time.");
            }
        } else {
            MessageSender.sendMsg(sender, "&cThey &ecannot &cregenerate.");
        }
        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            MessageSender.sendMsg(sender, "&cThey are currently blocking regeneration.");
        } else {
            MessageSender.sendMsg(sender, "&cThey are currently &enot &cblocking regeneration.");
        }

    }

    private void setCount(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.set")) {
            MessageSender.sendMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 3) {
            MessageSender.sendMsg(sender, "&cSet a player's regeneration count.");
            MessageSender.sendMsg(sender, "&c/pr &eset &6[player] [amount]");
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            MessageSender.sendMsg(sender, "&cThat player has never played on this server before!");
            return;
        }

        int count;

        try {
            count = parseInt(args[2]);
        } catch (NumberFormatException ex) {
            MessageSender.sendMsg(sender, "&cThe amount must be a whole number!");
            return;
        }
        if (count < 0) {
            MessageSender.sendMsg(sender, "&cThe amount must be positive!");
            return;
        }

        MessageSender.sendMsg(sender, "&cSuccessfully set &e" + args[1] + "'s &cregen count to &e" + count + "&c.");
        rdh.setPlayerRegenCount(player.getUniqueId(), count);
    }

    private void force(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.force")) {
            MessageSender.sendMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendMsg(sender, "&cForce a player to regenerate.");
            MessageSender.sendMsg(sender, "&c/pr &eforce &6[player]");
            return;
        }

        OfflinePlayer op = plugin.getServer().getOfflinePlayer(args[1]);
        Player player = op.getPlayer();

        if (player == null) {
            MessageSender.sendMsg(sender, "&cThat player is not online!");
            return;
        }
        if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
            MessageSender.sendMsg(sender, "&cThat player cannot regenerate!");
            return;
        }
        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            MessageSender.sendMsg(sender, "&cThat player is already regenerating!");
            return;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            MessageSender.sendMsg(sender, "&cThat player is in the void!");
            return;
        }

        MessageSender.sendMsg(sender, "&cSucessfully forced &e" + player.getName() + " &cto regenerate.");

        MessageSender.log(sender.getName() + " forced " + player.getName() + " to regenerate");

        rm.preRegen(player);
    }

    private void setBlock(CommandSender sender, String[] args) {
        if (!sender.hasPermission("projectrassilon.pr.block")) {
            MessageSender.sendMsg(sender, "&cYou don't have permission to do that!");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendMsg(sender, "&cSet a player's regeneration block.");
            MessageSender.sendMsg(sender, "&c/pr &eblock &6[player] <true|false>");
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            MessageSender.sendMsg(sender, "&cThat player has never played on this server before!");
            return;
        }

        if (args.length == 2) {
            if (rdh.getPlayerRegenBlock(player.getUniqueId()) == false) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                MessageSender.sendMsg(sender, "&cSucessfully set &e" + args[1] + "'s &cblock to &e" + true + "&c.");
            } else if (rdh.getPlayerRegenBlock(player.getUniqueId()) == true) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                MessageSender.sendMsg(sender, "&cSucessfully set &e" + args[1] + "'s &cblock to &e" + false + "&c.");
            }
        } else if (args.length >= 3) {
            String block = (args[2]);
            if (!Boolean.parseBoolean(block) && !block.equalsIgnoreCase("false")) {
                MessageSender.sendMsg(sender, "&cThe value must be '&etrue&c' or '&efalse&c'!");
            } else {
                rdh.setPlayerRegenBlock(player.getUniqueId(), Boolean.parseBoolean(block));
                MessageSender.sendMsg(sender, "&cSucessfully set &e" + args[1] + "'s &cblock to &e" + block + "&c.");
            }
        }
    }
}
