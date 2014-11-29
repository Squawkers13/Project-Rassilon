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

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.RegenManager;
import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.RegenTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getScheduler;

/**
 * The Regenerator is used to make players regenerate.
 * @author Squawkers13
 */
public class Regenerator {

    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;

    Regenerator(ProjectRassilon par1, RDataHandler par2, RegenManager par3) {
        plugin = par1;
        rdh = par2;
        rm = par3;
    }

    /**
     * Make a player regenerate.
     * @param player The player who you want to regenerate
     * @return an integer specifying if the operation was successful, and if not, why
     */
    public int regenerate(Player player) {
        // --- BEGIN REGEN CHECKS ---   
        if (rdh.getPlayerRegenCount(player.getUniqueId()) <= 0) { //Not enough regeneration energy
            return -1;
        }
        if (rdh.getPlayerRegenBlock(player.getUniqueId())) { //Blocking regeneration
            rdh.setPlayerRegenBlock(player.getUniqueId(), false);
            return -2;
        }
        if (rdh.getPlayerRegenStatus(player.getUniqueId())) { //Already regenerating
            return -3;
        }
        if (player.getLocation().getY() <= 0) { //In the void
            return -5;
        }
        // --- END REGEN CHECKS ---

        rm.preRegen(player);
        return 0;

    }

}
