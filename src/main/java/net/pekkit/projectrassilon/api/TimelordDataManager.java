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
package net.pekkit.projectrassilon.api;

import net.pekkit.projectrassilon.data.RDataHandler;
import org.bukkit.entity.Player;

/**
 *
 * @author Squawkers13
 */
public class TimelordDataManager {

    private final RDataHandler rdh;

    /**
     *
     * @param r
     */
    public TimelordDataManager(RDataHandler r) {
        rdh = r;
    }

    /**
     * Fetches the Timelord status of a player. This actually just fetches
     * whether they have the permission to regenerate on death or not.
     *
     * @param player The player to fetch the Timelord status of.
     *
     * @return whether the player is a Timelord or not.
     */
    public boolean getTimelordStatus(Player player) {
        return player.hasPermission("projectrassilon.regen.timelord");
    }

    /**
     * Fetches the regeneration count of a player.
     *
     * @param player The player to fetch the regeneration count of.
     *
     * @return The player's regeneration count.
     */
    public int getRegenCount(Player player) {
        return rdh.getPlayerRegenCount(player.getUniqueId());
    }

    /**
     * Fetches the regeneration block of a player.
     *
     * @param player The player to fetch the regeneration block of.
     *
     * @return The player's regeneration block.
     */
    public boolean getRegenBlock(Player player) {
        return rdh.getPlayerRegenBlock(player.getUniqueId());
    }

    /**
     * Fetches the regeneration status of a player.
     *
     * @param player The player to fetch the regeneration block of.
     *
     * @return Whether the player is regenerating or not.
     */
    public boolean getRegenStatus(Player player) {
        return rdh.getPlayerRegenStatus(player.getUniqueId());
    }
}
