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

import com.google.common.collect.ImmutableList;
import net.pekkit.projectrassilon.ProjectRassilon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.bukkit.util.StringUtil.copyPartialMatches;

/**
 * @author Squawkers13
 */
public class RegenCommandTabCompleter implements TabCompleter {

    private final ProjectRassilon plugin;
    List<String> ROOT_SUBS = ImmutableList.of("help", "info", "costs", "force", "block", "heal");

    /**
     * @param instance
     */
    public RegenCommandTabCompleter(ProjectRassilon instance) {
        plugin = instance;
    }

    /**
     * @param cs
     * @param cmnd
     * @param alias
     * @param args
     * @return
     */
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String alias, String[] args) {
        // Remember that we can return null to default to online player name matching
        if (args.length <= 1) {
            List<String> part = partial(args[0], ROOT_SUBS);
            return (part.size() > 0) ? part : null;
        } else if (args.length == 2) {
            String sub = args[0];
            if (sub.equalsIgnoreCase("block")) {
                return of("true", "false");
            } else if (sub.equalsIgnoreCase("info")) {
                return of("2");
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
