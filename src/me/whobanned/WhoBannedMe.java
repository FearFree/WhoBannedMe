package me.whobanned;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WhoBannedMe extends JavaPlugin implements Listener {
    
    private File config;
    private Player player;
    private BanLookup lookup;
    public int maxBans;
    public boolean debugMode;
    public boolean consoleOutput;
    
    @Override
    public void onEnable(){
        
        config = new File(getDataFolder(), "config.yml");
        
        if (!(config.exists())) {
            getLogger().log(Level.INFO, "Configuration not found! Creating default config.yml");
            this.saveDefaultConfig();
        }
        
        load();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        if (debugMode == true){
            getLogger().log(Level.INFO, "Debug mode enabled");
            getLogger().log(Level.INFO, "Max bans setting: {0}", maxBans);
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
    public void onDisable(){
        getLogger().log(Level.INFO, "WhoBannedMe Stopped!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        player = event.getPlayer();
	lookup = new BanLookup(this);
	if(debugMode == true){
	    getLogger().log(Level.INFO, "Player {0} has connected and is being scanned.", player.getName());
	}
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

            @Override
            public void run() {
		try{
		    lookup.check(player);
		} catch (IOException e) {
		    e.printStackTrace();
		}
            }
            
        }, 30L);
    }

    public void load() {
        maxBans = getConfig().getInt("max-bans");
        debugMode = getConfig().getBoolean("debug-mode");
	consoleOutput = getConfig().getBoolean("console-output");
    }
}
