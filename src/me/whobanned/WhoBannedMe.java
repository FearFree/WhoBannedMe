package me.whobanned;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
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
    private BanLookup lookup;
    private final UpdateCheck update = new UpdateCheck(this);
    public int minBans;
    public boolean updateCheck;
    public boolean debugMode;
    public boolean consoleOutput;
    public String broadcastTag = ChatColor.RED + "[WhoBannedMe] " + ChatColor.GRAY;
    public String noPerms = broadcastTag + "You do not have permission to do that!";

    @Override
    public void onEnable() {

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
        lookup = new BanLookup(this);
        if (debugMode == true) {
            getLogger().log(Level.INFO, "Player {0} has connected and is being scanned.", player.getName());
        }
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            @Override
            public void run() {
                try {
                    lookup.check(pName);
                    lookup.notify(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }, 30L);
    }

    public void load() {
        minBans = getConfig().getInt("minimum-bans");
	updateCheck = getConfig().getBoolean("update-check");
        debugMode = getConfig().getBoolean("debug-mode");
        consoleOutput = getConfig().getBoolean("console-output");
    }
}
