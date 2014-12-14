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
package net.pekkit.projectrassilon;

import net.pekkit.projectrassilon.api.RassilonAPI;
import net.pekkit.projectrassilon.commands.BaseCommandExecutor;
import net.pekkit.projectrassilon.commands.BaseCommandTabCompleter;
import net.pekkit.projectrassilon.commands.RegenCommandExecutor;
import net.pekkit.projectrassilon.commands.RegenCommandTabCompleter;
import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.listeners.PlayerListener;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.Constants;
import net.pekkit.projectrassilon.util.RassilonUtils;
import net.pekkit.projectrassilon.util.Version;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Project Rassilon: Become a Time Lord and regenerate!
 *
 * @author Squawkers13
 * @version 1.3
 */
public class ProjectRassilon extends JavaPlugin {
    
    private MessageSender ms;

    private RDataHandler rdh;

    private RegenManager rm;

    private RassilonAPI ra;

    /**
     * Called by Bukkit when enabling the plugin.
     */
    @Override
    public void onEnable() {
        ms = new MessageSender();

        // --- Version check ---
        Version installed = RassilonUtils.getServerVersion(getServer().getVersion());
        Version required = new Version(Constants.MIN_MINECRAFT_VERSION);
        if (installed.compareTo(required) < 0) {
            MessageSender.log("This plugin requires CraftBukkit 1.7.9 or higher!");
            MessageSender.log("Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        rdh = new RDataHandler(this, ms);

        rm = new RegenManager(this, rdh, ms);

        // --- Config check ---
        if (getConfig().getDouble("settings.config-version", -1.0D) != Constants.CONFIG_VERSION) {
            String old = getConfig().getString("settings.config-version", "OLD");
            MessageSender.log("&cIncompatible config detected! Renaming it to config-" + old + ".yml");
            MessageSender.log("&cA new config has been created, please transfer your settings.");
            MessageSender.log("&cWhen you have finished, type &6/pr reload&c to load your settings.");
            try {
                getConfig().save(new File(getDataFolder(), "config-" + old + ".yml"));
            } catch (IOException ex) {
                MessageSender.logStackTrace(ex);
            }
            saveResource("config.yml", true);
        }
       
        getServer().getPluginManager().registerEvents(new PlayerListener(this, rdh, rm, ms), this);

        getCommand("pr").setExecutor(new BaseCommandExecutor(this, rdh, rm, ms));
        getCommand("regen").setExecutor(new RegenCommandExecutor(this, rdh, rm, ms));

        getCommand("pr").setTabCompleter(new BaseCommandTabCompleter(this));
        getCommand("regen").setTabCompleter(new RegenCommandTabCompleter(this));

        ra = new RassilonAPI(this, rdh, rm);

    }

    /**
     * Called by Bukkit when disabling the plugin.
     */
    @Override
    public void onDisable() {
        //Nothing here right now...
    }

    /**
     * Getter method for the API instance.
     *
     * @return API instance
     *
     * @since 1.3
     */
    public RassilonAPI getAPI() {
        return ra;
    }

}
