package com.smithb99.projectrassilon.commands;

import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;
import com.smithb99.projectrassilon.locale.MessageSender;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import javax.annotation.Nonnull;

public class RegenCommandExecutor implements CommandExecutor {
    private RDataHandler rdh;
    private final RegenManager rm;

    public RegenCommandExecutor(RDataHandler rdh, RegenManager rm) {
        this.rdh = rdh;
        this.rm = rm;
    }

    @Nonnull
    public CommandResult execute(@Nonnull CommandSource source, @Nonnull CommandContext args) {
        Player player;

        if (source instanceof Player) {
            player = (Player) source;
        } else {
            source.sendMessage(Texts.of("You must be a Time Lord to do that."));
            return CommandResult.builder().successCount(0).build();
        }

        if (!source.hasPermission("projectrassilon.regen.timelord")) {
            source.sendMessage(Texts.of("You must be a Time Lord to do that."));
        }

        //TODO parse args
        if (args.getAll("").size() == 0) {
            statusCommand(player);
        } else if (args.getOne("").equals("?")) {
            helpCommand(player);
        } else if (args.getOne("").equals("force") || args.getOne("").equals("f")) {
            forceCommand(source, player);
        } else if (args.getOne("").equals("block") || args.getOne("").equals("b")) {
            blockCommand(player, args);
        } else {
            source.sendMessage(Texts.of("Invalid argument " + args.getOne("") + ". Type /regen ? for help."));
        }

        return CommandResult.builder().successCount(0).build();
    }

    public void statusCommand(Player player) {
        player.sendMessage(Texts.of("---------- Regeneration Status: " + player.getName() + " ----------"));

        if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 0) {
            if (this.rdh.getPlayerRegenCount(player.getUniqueId()) != 1) {
                player.sendMessage(Texts.of("You may regenerate " + this.rdh.getPlayerRegenCount(player.getUniqueId()) + " more times."));
            } else {
                player.sendMessage(Texts.of("You may regenerate " + this.rdh.getPlayerRegenCount(player.getUniqueId()) + " more time."));
            }
        } else {
            player.sendMessage(Texts.of("You can no longer regenerate."));
        }

        player.sendMessage(Texts.of("You are on your " + this.rdh.getPlayerIncarnationCount(player.getUniqueId()) + getIncarnationSuffix(this.rdh.getPlayerIncarnationCount(player.getUniqueId())) + " incarnation."));

        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            player.sendMessage(Texts.of("You are currently regenerating."));
        } else {
            player.sendMessage(Texts.of("You are currently not regenerating."));
        }

        if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
            player.sendMessage(Texts.of("You are currently blocking regeneration."));
        } else {
            player.sendMessage(Texts.of("You are currently not blocking regeneration."));
        }

        player.sendMessage(Texts.of("Type /regen ? for help."));
    }

    public void helpCommand(Player player) {
        player.sendMessage(Texts.of("---------- Regeneration Help ----------"));
        player.sendMessage(Texts.of("/regen - View your regeneration status"));

        if (player.hasPermission("projectrassilon.regen.force")) {
            player.sendMessage(Texts.of("/regen <force | f > - Force regeneration"));
        }

        if (player.hasPermission("projectrassilon.regen.block")) {
            player.sendMessage(Texts.of("/regen <block | b> <true | false> - Block your next regeneration"));
        }
    }

    private void forceCommand(CommandSource source, Player player) {
        if (!player.hasPermission("projectrassilon.regen.force")) {
            player.sendMessage(Texts.of("You do not have permission to perform this command."));
            return;
        }

        if (this.rdh.getPlayerIncarnationCount(player.getUniqueId()) <= 0) {
            player.sendMessage(Texts.of("You cannot regenerate."));
            return;
        }

        if (this.rdh.getPlayerRegenStatus(player.getUniqueId())) {
            player.sendMessage(Texts.of("You are already regenerating."));
            return;
        }

        if (this.rdh.getPlayerRegenBlock(player.getUniqueId())) {
            player.sendMessage(Texts.of("you are blocking regeneration."));
            return;
        }

        if (player.getGameModeData().getGameMode().equals(GameModes.CREATIVE)) {
            player.sendMessage(Texts.of("You cannot regenerate in Creative mode."));
            return;
        }

        if (player.getLocation().getY() <= 0) {
            player.sendMessage(Texts.of("You cannot regenerate in the Void."));
            return;
        }

        MessageSender.log(source.getName() + " forced " + player.getName() + " to regenerate.");

        rm.preRegen(player);
    }

    private void blockCommand(Player player, CommandContext args) {
        if (!player.hasPermission("projectrassilon.regen.block")) {
            player.sendMessage(Texts.of("You do not have permission to perform this command."));
            return;
        }

        if (args.getAll("").size() == 1) {
            if (!rdh.getPlayerRegenBlock(player.getUniqueId())) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                player.sendMessage(Texts.of("You are now blocking regeneration."));
            } else if (rdh.getPlayerRegenBlock(player.getUniqueId())) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                player.sendMessage(Texts.of("You are no longer blocking regeneration."));
            }
        } else if (args.getAll("").size() == 2) {
            if (!args.getOne("").equals("true") && !args.getOne("").equals("false")) {
                player.sendMessage(Texts.of("Block your next regeneration:"));
                player.sendMessage(Texts.of("/regen block <true | false>"));
            } else if (args.getOne("").equals("true")) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), true);
                player.sendMessage(Texts.of("You are now blocking regeneration."));
            } else if (args.getOne("").equals("false")) {
                rdh.setPlayerRegenBlock(player.getUniqueId(), false);
                player.sendMessage(Texts.of("You are no longer blocking regeneration."));
            }
        }
    }

    private String getIncarnationSuffix(int i) {
        switch (i) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
