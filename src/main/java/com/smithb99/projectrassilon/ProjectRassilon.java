package com.smithb99.projectrassilon;

import com.google.inject.Inject;
import com.smithb99.projectrassilon.api.RassilonAPI;
import com.smithb99.projectrassilon.commands.BaseCommandExecutor;
import com.smithb99.projectrassilon.commands.RegenCommandExecutor;
import com.smithb99.projectrassilon.data.RDataHandler;
import com.smithb99.projectrassilon.listeners.EntityDamageEventHandler;
import com.smithb99.projectrassilon.listeners.PlayerListener;
import com.smithb99.projectrassilon.locale.MessageSender;
import com.smithb99.projectrassilon.util.Constants;
import com.smithb99.projectrassilon.util.RassilonUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import org.mcstats.Metrics;
import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.io.File;
import java.io.IOException;

@NonnullByDefault
@Plugin(id = PomData.ARTIFACT_ID, name = PomData.NAME, version = PomData.VERSION)
public class ProjectRassilon {
    private RDataHandler rdh;
    private RegenManager rm;
    private RassilonAPI ra;

    File configFile = new File("ProjectRassilon.conf");

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configHandler = HoconConfigurationLoader.builder().setFile(configFile).build();

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Subscribe
    public void onServerStart(ServerStartingEvent event) {
        try {
            migrateConfig();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }

        if (RassilonUtils.getCurrentVersion(this) == RassilonUtils.SimplifiedVersion.PRE_UUID) {
            MessageSender.log("This plugin requires CraftBukkit 1.7.9 or higher");
            MessageSender.log("Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (new File("plugins/ProjectRassilon.conf").exists()) {
            ConfigurationNode rootNode;

            try {
                rootNode = configHandler.load();
            } catch (IOException e) {
                rootNode = null;
                logger.error("WARNING: Failed to load config!");
            }

            if (rootNode.getChildrenList().isEmpty()) {
                rootNode.getNode("settings", "general", "stats").setValue(true);
                rootNode.getNode("settings", "regen", "count").setValue(12);
                rootNode.getNode("settings", "config-version").setValue(2.5);

                try {
                    configHandler.save(rootNode);
                } catch (IOException e) {
                    MessageSender.log(e.getLocalizedMessage());
                }
            }
        }


        rdh = new RDataHandler(this);
        rm - new RegenManager(this, rdh);

        try {
            if (configHandler.load().getNode("settings", "config-version").getDouble() != Constants.CONFIG_VERSION) {
                String old = configHandler.load().getNode("settings", "config-version").getString();
                MessageSender.log("&cIncompatible config detected! Renaming it to config-" + old + ".yml");
                MessageSender.log("&cA new config has been created, please transfer your settings.");
                MessageSender.log("&cWhen you have finished, type &6/pr reload&c to load your settings.");

                try {
                    getConfig().save(new File(getDataFolder(), "config-" + old + ".yml"));
                } catch (IOException e) {
                    MessageSender.logStackTrace(e);
                }

                saveResource("config.yml", true);
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }


        try {
            if (configHandler.load().getNode("settings", "general", "stats").getBoolean()) {
                PluginContainer pc = new PluginContainer() {
                    public String getId() {
                        return PomData.ARTIFACT_ID;
                    }

                    public String getName() {
                        return PomData.NAME;
                    }

                    public String getVersion() {
                        return PomData.VERSION;
                    }

                    public Object getInstance() {
                        return ProjectRassilon.class;
                    }
                };
                Metrics metrics = new Metrics(game, pc);
                metrics.start();
            } else {
                logger.info("Metrics disabled.");
            }
        } catch (IOException e) {
            logger.error("WARNING: Failed to load config!");
        }


        getServer().getPluginManager().registerEvents(new PlayerListener(this, rdh, rm), this);

        getCommand("pr").setExecutor(new BaseCommandExecutor(this, rdh, rm));
        getCommand("regen").setExecutor(new RegenCommandExecutor(this, rdh, rm));

        ra = new RassilonAPI(this, rdh, rm);
    }

    EventHandler<EntityDamageEvent> entityDamageEventHandler = new EntityDamageEventHandler();


    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {

    }

    private void migrateConfig() throws IOException {
        File bukkitConfigDir = new File("plugins/ProjectRassilon");

        if (bukkitConfigDir.isDirectory() && !configDir.isDirectory()) {
            logger.info("Migrating configuration data from Bukkit");

            if (!bukkitConfigDir.renameTo(configDir)) {
                throw new IOException("Unable to move Bukkit directory to Sponge directory. Contact the plugin author.");
            }
        }

        File bukkitConfigFile = new File(configDir, "config.yml");

        if (bukkitConfigFile.isFile()) {
            ConfigurationLoader<ConfigurationNode> yamlReader = YAMLConfigurationLoader.builder().setFile(bukkitConfigFile).build();
            ConfigurationNode bukkitConfig = yamlReader.load();
            configHandler.save(bukkitConfig);

            if (!bukkitConfigFile.renameTo(new File(configDir, "config.yml.bukkit"))) {
                logger.warn("Could not rename old Bukkit configuration file to old name");
            }
        }
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfig() {
        return configHandler;
    }

    public ProjectRassilon getPlugin() {
        return this;
    }

    public RassilonAPI getAPI() {
        return ra;
    }
}
