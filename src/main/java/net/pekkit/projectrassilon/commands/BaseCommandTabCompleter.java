/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Doctor Squawk
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;
import static org.bukkit.util.StringUtil.copyPartialMatches;

/**
 *
 * @author Squawkers13
 */
public class BaseCommandTabCompleter implements TabCompleter {

    private List<String> ROOT_SUBS = of("?", "view", "set", "force", "block", "reload");
    private ProjectRassilon plugin;

    /**
     *
     * @param instance
     */
    public BaseCommandTabCompleter(ProjectRassilon instance) {
        this.plugin = instance;
    }

    /**
     *
     * @param cs
     * @param cmd
     * @param alias
     * @param args
     * @return
     */
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        // Remember that we can return null to default to online player name matching  
        if (args.length <= 1) {
            List<String> part = partial(args[0], ROOT_SUBS);
            return (part.size() > 0) ? part : null;
        } else if (args.length == 2) {
            String sub = args[0];
            if (sub.equalsIgnoreCase("view") || sub.equalsIgnoreCase("set") || sub.equalsIgnoreCase("force") || sub.equalsIgnoreCase("block")) {
                return null;
            } else {
                return of();
            }
        } else if (args.length == 3) {
            String sub = args[0];
            if (sub.equalsIgnoreCase("block")) {
                return of("true", "false");
            } else if (sub.equalsIgnoreCase("set")) {
                return of("0", plugin.getConfig(REGEN).getString("regen.costs.regenCost", "120"),
                        plugin.getConfig(REGEN).getString("regen.costs.startingEnergy", "1500"));
            } else {
                return of();
            }
        }
        return of();
    }

    private List<String> partial(String token, Collection<String> from) {
        return copyPartialMatches(token, from, new ArrayList<String>(from.size()));
    }
}
