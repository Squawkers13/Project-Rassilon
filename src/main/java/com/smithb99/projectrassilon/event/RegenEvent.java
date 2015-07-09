package com.smithb99.projectrassilon.event;

import com.sun.media.jfxmedia.events.PlayerEvent;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.event.callback.CallbackList;

public class RegenEvent extends PlayerEvent {
    private Player player;

    public RegenEvent(Player player) {
        this.player = player;
    }

    public Player getEntity() {
        return player;
    }

    public Game getGame() {
        return
    }

    public CallbackList getCallbacks() {
        return new CallbackList();
    }
}
