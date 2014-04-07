package me.whobanned;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WhoBannedMe extends JavaPlugin implements Listener {

    private File config;
    private Player player;
    private String pName;
    private final BanLookup lookup = new BanLookup(this);
    private final UpdateCheck update = new UpdateCheck(this);
    public int minBans;
    public int maxBans;
    public boolean updateCheck;
    public boolean debugMode;
    public boolean consoleOutput;
    public String broadcastTag = ChatColor.RED + "[WhoBannedMe] " + ChatColor.GRAY;
    public String noPerms = broadcastTag + "You do not have permission to do that!";
    public String overBansMessage;

    @Override
    public void onEnable() {
        if (this.getServer().getOnlineMode() != true) {
            this.getLogger().warning("***********************************************");
            this.getLogger().warning(" Results may not be accurate in offline mode!  ");
            this.getLogger().warning("Please set online-mode to true for best results");
            this.getLogger().warning("***********************************************");
        }

        config = new File(getDataFolder(), "config.yml");

        if (!(config.exists())) {
            getLogger().log(Level.INFO, "Configuration not found! Creating default config.yml");
            this.saveDefaultConfig();
        }

        load();
        update.check();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("whobanned").setExecutor(new CommandWhoBanned(this, lookup));

        if (debugMode == true) {
            getLogger().log(Level.INFO, "Debug mode enabled");
            getLogger().log(Level.INFO, "Minimum bans setting: {0}", minBans);
        }

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        getLogger().log(Level.INFO, "WhoBannedMe Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "WhoBannedMe Stopped!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        player = event.getPlayer();
        pName = player.getName();
        if (debugMode == true) {
            getLogger().log(Level.INFO, "Player {0} has connected and is being scanned.", player.getName());
        }
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            @Override
            public void run() {
                try {
                    lookup.check(pName);
                    if (lookup.totalBans > maxBans) {
                        try { //New ban method
                            Bukkit.getBanList(BanList.Type.NAME).addBan(pName, overBansMessage, null, "WhoBannedMe");
                        } catch (java.lang.NoClassDefFoundError e) {
                            player.setBanned(true);
                            if (debugMode = true) {
                                getLogger().info("Using legacy ban method.");
                            }
                        }
                        player.kickPlayer(overBansMessage);
                        return;
                    }
                    lookup.notify(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void load() {
        minBans = getConfig().getInt("minimum-bans");
        maxBans = getConfig().getInt("maximum-bans");
        overBansMessage = getConfig().getString("maximum-bans-message");
        updateCheck = getConfig().getBoolean("update-check");
        debugMode = getConfig().getBoolean("debug-mode");
        consoleOutput = getConfig().getBoolean("console-output");
    }
}
