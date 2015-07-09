package com.smithb99.projectrassilon.commands;

import com.smithb99.projectrassilon.ProjectRassilon;
import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;
import com.smithb99.projectrassilon.locale.MessageSender;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.io.File;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class BaseCommandExecutor implements CommandExecutor {
    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;

    public BaseCommandExecutor(ProjectRassilon instance, RDataHandler rdh, RegenManager rm) {
        plugin = instance;

        this.rdh = rdh;
        this.rm = rm;
    }

    public CommandResult execute(CommandSource commandSource, CommandContext args) throws CommandException {
        //TODO parse args
        return CommandResult.success();
    }

    public void reloadConfigs(CommandSource source) {
        if (!source.hasPermission("projectrassilon.pr.reload")) {
            MessageSender.sendMsg(source, "You do not have permission to perform this command.");
            return;
        }

        plugin.reloadConfig();

        if (plugin.getConfig() == null) {
            plugin.saveResource("config.yml", true);
        }

        if (plugin.getConfig().getDouble("settings.config-version", -1.0D) != Constraints.CONFIG_VERSION) {
            String old = plugin.getConfig().getString("settings.config-version", "OLD");
            MessageSender.sendMsg(source, "Incompatible config detected");
            MessageSender.sendMsg(source, "A new config has been created, please transfer your settings");
            MessageSender.sendMsg(source, "When you have finished, type /pr reload to load your settings");;

            try {
                plugin.getConfig().save(new File(plugin.getDataFolder(), "config-" + old + ".yml"));
            } catch (IOException e) {
                MessageSender.logStackTrace(e);
            }

            plugin.saveResource("config.yml", true);
        } else {
            MessageSender.sendMsg(source, "Config successfully reloaded.");
        }
    }

    private void viewPlayerStats(CommandSource source, CommandContext args) {
        if (!source.hasPermission("projectrassilon.pr.view")) {
            source.sendMessage(Texts.of("You do not have permission to perform this command."));
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args.getOne("")); //TODO get offline player

        if (!player.hasPlayedBefore()) {
            source.sendMessage(Texts.of("That player has never played before."));
            return;
        }

        source.sendMessage(Texts.of("---------- Regeneration Status: " + player.getName() + " ----------"));

        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 0) {
            if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 1) {
                source.sendMessage(Texts.of("This player may regenerate " + this.rdh.getPlayerRegenCount(player.getUniqueId())) + " more times.");
            } else {
                source.sendMessage(Texts.of("This player may regenerate " + this.rdh.getPlayerRegenCount(player.getUniqueId())) + " more time.");
            }
        } else {
            source.sendMessage(Texts.of("This player can no longer regenerate."));
        }

        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            source.sendMessage(Texts.of("This player is currently blocking regeneration."));
        } else {
            source.sendMessage(Texts.of("This player is not blocking regeneration."));
        }
    }

    private void setCount(CommandSource source, CommandContext args) {
        if (!source.hasPermission("projectrassilon.pr.set")) {
            source.sendMessage(Texts.of("You do not have permission to perform this command."));
            return;
        }

        if (!player.hasPlayedBefore()) {
            source.sendMessage(Texts.of("That player has never played before."));
            return;
        }

        int count;

        try {
            count = parseInt(args.getOne("")); //TODO figure this out
        } catch (NumberFormatException e) {
            source.sendMessage(Texts.of("The amount must be a whole number."));
            return;
        }

        if (count < 0) {
            source.sendMessage(Texts.of("The amount must be positive."));
        }

        source.sendMessage(Texts.of("Successfully set " + args.getOne("") + "'s regeneration count to " + count + "."));
        rdh.setPlayerRegenCount(player.getUniqueId(), count);
    }

    private void force(CommandSource source, CommandContext args) {
        if (source.hasPermission("projectrassilon.pr.force")) {
            source.sendMessage(Texts.of("You do not have permission to perform this command."));
            return;
        }

        OfflinePlayer op = plugin.getServer().getOfflinePlayer(args.getOne("")); //TODO parse args
        Player player = op.getPlayer();

        if (player == null) {
            source.sendMessage(Texts.of("Player is not online."));
            return;
        }

        if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) {
            source.sendMessage(Texts.of("That player can no longer regenerate."));
            return;
        }

        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            source.sendMessage(Texts.of("That player is already regenerating."));
            return;
        }

        if (player.getLocation().getY() <= 0) {
            source.sendMessage(Texts.of("That player is in the void."));
            return;
        }

        source.sendMessage(Texts.of("Successfully forced " + player.getName() + " to regenerate."));
        player.sendMessage(Texts.of(source.getName() + " is forcing you to regenerate."));
        MessageSender.log(source.getName() + " has forced " player.getName() + " to regenerate.");

        rm.preRegen(player);
    }

    private void setBlock(CommandSource source, CommandContext args) {
        if (!source.hasPermission("projectrassilon.pr.block")) {
            source.sendMessage(Texts.of("You do not have permission to perform this command."));
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(args.getOne("")); //TODO parse args

        if (!player.hasPlayedBefore()) {
            source.sendMessage(Texts.of("That player has never played before."));
            return;
        }

        //TODO get value from command and set block
    }
}
