package me.whobanned;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import net.ae97.fishbans.api.Fishbans;
import net.ae97.fishbans.api.FishbansPlayer;
import net.ae97.fishbans.api.exceptions.NoSuchUserException;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WhoBannedMe extends JavaPlugin implements Listener {

    public final String broadcastTag = ChatColor.RED + "[WhoBannedMe] " + ChatColor.GRAY;
    public final String noPerms = broadcastTag + "You do not have permission to do that!";
    public int minBans;
    public int maxBans;
    public boolean updateCheck;
    public boolean debugMode;
    public boolean consoleOutput;
    public String overBansMessage;

    @Override
    public void onEnable() {
        if (!this.getServer().getOnlineMode()) {
            this.getLogger().warning("***********************************************");
            this.getLogger().warning(" Results may not be accurate in offline mode!  ");
            this.getLogger().warning("Please set online-mode to true for best results");
            this.getLogger().warning("***********************************************");
        }

        File config = new File(getDataFolder(), "config.yml");

        if (!(config.exists())) {
            getLogger().log(Level.INFO, "Configuration not found! Creating default config.yml");
            this.saveDefaultConfig();
        }

        load();
        new UpdateCheck(this).check();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("whobanned").setExecutor(new CommandWhoBanned(this));

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
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        final String pName = event.getName();
        if (debugMode == true) {
            getLogger().log(Level.INFO, "Player {0} has connected and is being scanned.", pName);
        }
        try {
            //This exists to trigger the loading of data
            //The data is loaded into a cache which can be quickly accessed on the main thread
            Fishbans.getFishbanPlayer(pName);
        } catch (IOException | NoSuchUserException e) {
            getLogger().log(Level.SEVERE, "Error occured while checking bans for " + pName, e);
        }
    }

    @EventHandler
    public void OnPlayerJoin(PlayerLoginEvent event) {
        if (event.getPlayer().hasPermission("whobannedme.exempt")) {
            if (debugMode || consoleOutput) {
                getLogger().info("Player check cancelled by permissions");
            }
            return;
        }

        try {
            final FishbansPlayer player = Fishbans.getFishbanPlayer(event.getPlayer().getName());

            if (maxBans != -1 && player.getBanCount() > maxBans) {
                try {
                    //New ban method
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), overBansMessage, null, "WhoBannedMe");
                } catch (java.lang.NoClassDefFoundError e) {
                    //Old ban method
                    Bukkit.getScheduler().runTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getOfflinePlayer(player.getName()).setBanned(true);
                        }
                    });
                }
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, overBansMessage);
            } else {
                String detailURL = "http://fishbans.com/u/" + player.getName();
                if (player.getBanCount() > 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("whobannedme.notify") && player.getBanCount() > minBans || p.hasPermission("whobannedme.notify.all")) {
                            p.sendMessage(broadcastTag + "Connected player " + ChatColor.RED + player.getName() + ChatColor.GRAY + " has " + player.getBanCount() + " bans. To view ban details, visit " + detailURL);
                        }
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("whobannedme.notify.all")) {
                            p.sendMessage(broadcastTag + "Conected player " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " has no bans on record.");
                        }
                    }
                }
            }
        } catch (IOException | NoSuchUserException e) {
            getLogger().log(Level.SEVERE, "Error occured while checking bans for " + event.getPlayer().getName(), e);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("whobannedme.notify.all")) {
                    p.sendMessage("Error checking " + event.getPlayer().getName() + ": " + e.getMessage());
                }
            }
        }
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
