package com.smithb99.projectrassilon.event;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.entity.player.PlayerEvent;
import org.spongepowered.api.util.event.callback.CallbackList;

public class PostRegenEvent extends PlayerEvent {
    private Player player;

    public PostRegenEvent(Player player) {
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
