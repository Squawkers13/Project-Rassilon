package com.smithb99.projectrassilon.listeners;

import com.smithb99.projectrassilon.ProjectRassilon;
import com.smithb99.projectrassilon.data.RDataHandler;
import com.smithb99.projectrassilon.util.RegenTask;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.EventHandler;
import org.spongepowered.api.event.entity.player.PlayerDeathEvent;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Texts;

public class PlayerDeathEventHandler implements EventHandler<PlayerDeathEvent> {
    private ProjectRassilon plugin;
    private RDataHandler rdh;

    @Override
    public void handle(PlayerDeathEvent event) throws Exception {
        Player player = event.getEntity();

        rdh.setPlayerRegenCount(player.getUniqueId(), plugin.getConfigHandler().getInt("settings.regen.count"));
        rdh.setPlayerIncarnationCount(player.getUniqueId(), 1);

        for (RegenTask e : RegenTask.values()) {
            Task task = rdh.getPlayerTask(player.getUniqueId(), e);
            final boolean remove;
            if (plugin.getGame().getScheduler().getScheduledTasks().remove(task)) {
                remove = true;
            } else {
                remove = false;
            }
            rdh.setPlayerTask(player.getUniqueId(), e, 0);
        }

        if (rdh.getPlayerRegenStatus(player.getUniqueId())) {
            event.setNewMessage(Texts.of(player.getName() + " was killed while regenerating"));
        }
    }
}
