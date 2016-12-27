/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Doctor Squawk
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
import net.pekkit.projectrassilon.data.TimelordDataHandler;
import net.pekkit.projectrassilon.listeners.PlayerListener;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.RassilonUtils;
import net.pekkit.projectrassilon.util.RassilonUtils.SimplifiedVersion;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.util.EnumMap;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.CORE;
import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.values;

/**
 * Project Rassilon: Become a Time Lord and regenerate!
 *
 * @author Doctor Squawk
 * @version 2.0
 */
public class ProjectRassilon extends JavaPlugin {

    private EnumMap<RassilonUtils.ConfigurationFile, YamlConfiguration> configs;

    public TimelordDataHandler tdh;

    private RScoreboardManager rsm;

    private RegenManager rm;

    private RassilonAPI ra;

    /**
     * Called by Bukkit when enabling the plugin.
     */
    @Override
    public void onEnable() {

        // --- Version check ---
        if (RassilonUtils.getCurrentVersion(this) == SimplifiedVersion.PRE_UUID) {
            MessageSender.log("&cThis plugin requires CraftBukkit 1.7.9 or higher!");
            MessageSender.log("&cDisabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MessageSender.log("&eEnabling Project Rassilon " + getDescription().getVersion());

        MessageSender.log("Loading configuration files...");
        configs = new EnumMap<RassilonUtils.ConfigurationFile, YamlConfiguration>(RassilonUtils.ConfigurationFile.class);
        for (RassilonUtils.ConfigurationFile cf: values()) {
            File f = new File(getDataFolder(), cf.getFileName());

            MessageSender.log("&cConfiguration is overwritten on preview builds!");
            //if (!f.exists()) {
                f.getParentFile().mkdirs();
                RassilonUtils.copy(getResource(cf.getFileName()), f);
            //}

            YamlConfiguration c = new YamlConfiguration();
            try {
                c.load(f);
            } catch (Exception e) {
                e.printStackTrace();
            }

            configs.put(cf, c);
        }

        MessageSender.log("Initialising dataset...");
        tdh = new TimelordDataHandler(this);

        MessageSender.log("Loading regeneration framework...");
        rm = new RegenManager(this, tdh);

        // --- Config check ---
        // TODO Reimplement next time configs change
        /*if (getConfig(RassilonUtils.ConfigurationFile.CORE).getDouble("core.configVersion", -1.0D) != Constants.CONFIG_VERSION) {
            String old = getConfig().getString("settings.config-version", "OLD");
            MessageSender.log("&cIncompatible config detected! Renaming it to config-" + old + ".yml");
            MessageSender.log("&cA new config has been created, please transfer your settings.");
            MessageSender.log("&cWhen you have finished, type &6/pr reload&c to load your settings.");
            try {
                getConfig().save(new File(getDataFolder(), "config-" + old + ".yml"));
            } catch (IOException ex) {
                MessageSender.logStackTrace(ex);
            }
            saveResource("core.yml", true);
        } */

        // --- MCStats submission ---
        if (getConfig(CORE).getBoolean("core.general.stats")) {
            try {
                MessageSender.log("Starting Metrics...");
                Metrics metrics = new Metrics(this);
                metrics.start();
            } catch (Exception e) {
                MessageSender.logStackTrace(e);
            }
        }

        MessageSender.log("Calculating scoreboards...");
        rsm = new RScoreboardManager(this, tdh);

        MessageSender.log("Initialising listeners...");
        getServer().getPluginManager().registerEvents(new PlayerListener(this, tdh, rm, rsm), this);

        MessageSender.log("Registering commands...");
        getCommand("pr").setExecutor(new BaseCommandExecutor(this, tdh, rm, rsm));
        getCommand("regen").setExecutor(new RegenCommandExecutor(this, tdh, rm, rsm));

        getCommand("pr").setTabCompleter(new BaseCommandTabCompleter(this));
        getCommand("regen").setTabCompleter(new RegenCommandTabCompleter(this));

        MessageSender.log("Loading API...");
        ra = new RassilonAPI(this, tdh, rm);

        MessageSender.log("&eProject Rassilon has been successfully enabled!");
    }

    /**
     * Called by Bukkit when disabling the plugin.
     */
    @Override
    public void onDisable() {
        MessageSender.log("&eDisabling Project Rassilon " + getDescription().getVersion());

        tdh.writeAllToDB();

        MessageSender.log("&eProject Rassilon has been successfully disabled!");
    }

    @Deprecated
    @Override
    public YamlConfiguration getConfig() {
        return configs.get(CORE);
    }

    public YamlConfiguration getConfig(RassilonUtils.ConfigurationFile cf) {
        return configs.get(cf);
    }

    @Deprecated
    @Override
    public void reloadConfig() {
        throw new UnsupportedOperationException(); //THIS SHOULD NEVER BE CALLED
    }

    public void reloadConfigs() {
        configs.clear();

        for (RassilonUtils.ConfigurationFile cf: values()) {
            File f = new File(getDataFolder(), cf.getFileName());

            if (!f.exists()) {
                f.getParentFile().mkdirs();
                RassilonUtils.copy(getResource(cf.getFileName()), f);
            }

            YamlConfiguration c = new YamlConfiguration();
            try {
                c.load(f);
            } catch (Exception e) {
                e.printStackTrace();
            }

            configs.put(cf, c);
        }
    }

    /**
     * Getter method for the API instance.
     *
     * @return API instance
     *
     * @since 1.3
     */
    @SuppressWarnings("unused")
    public RassilonAPI getAPI() {
        return ra;
    }

}
