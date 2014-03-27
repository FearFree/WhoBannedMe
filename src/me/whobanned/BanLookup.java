package me.whobanned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class BanLookup {
    
    private final WhoBannedMe plugin;
    
    URL checkURL = null;
    URLConnection connection = null;
    BufferedReader reader = null;
    String response = null;
    private boolean success;
    private Object error = null;
    private boolean exempt = false;
    private int totalBans;
    
    public BanLookup(WhoBannedMe plugin){
	this.plugin = plugin;
    }
    
    public void check(Player player) throws IOException {
        String pName = player.getName();
        if (player.hasPermission("whobannedme.exempt")){
            
            exempt = true;
	    
	    if(plugin.debugMode == true || plugin.consoleOutput == true){
		plugin.getLogger().info("Player check cancelled by permissions");
	    }
            return;
        }
	try{
	    checkURL = new URL("http://api.fishbans.com/stats/" + pName + "/");
	} catch (MalformedURLException e) {
	    return;
	}
	try{
	    connection = checkURL.openConnection();
	    connection.setReadTimeout(5000);
	    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    response = reader.readLine();
	} catch (IOException e) {
	    plugin.getLogger().warning("Could not reach ban server!");
	}
        try {
            JSONParser j = new JSONParser();
            JSONObject o = (JSONObject)j.parse(response);
            success = (boolean) o.get("success");
            if(success != true){
                error = o.get("error");
                plugin.getLogger().log(Level.WARNING, "Error: {0}", error);
                return;
            } else {
                if(plugin.debugMode == true){
                    plugin.getLogger().log(Level.INFO, "Result found!");
                }
            }
            if(o.get("stats") != null){
                Map output = (Map)o.get("stats");
                if(plugin.consoleOutput == true || plugin.debugMode == true){
                    plugin.getLogger().log(Level.INFO, "Player Name: {0}", output.get("username"));
                    plugin.getLogger().log(Level.INFO, "Player UUID: {0}", output.get("uuid"));
                    plugin.getLogger().log(Level.INFO, "Total Bans: {0}", output.get("totalbans"));
                }
                
                totalBans = (int) output.get("totalbans");
                
            }
        } catch (ParseException ex) {
            Logger.getLogger(BanLookup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void notify (Player player) throws IOException {
        String pName = player.getName();
        if(error != null){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.hasPermission("whobannedme.notify.all")){
                    p.sendMessage("Error checking " + pName + ": " + error);
                }
            }
        }
        
        if(exempt = true){
            for(Player p : Bukkit.getOnlinePlayers()){
		if(p.hasPermission("whobannedme.notify.all")){
		    p.sendMessage(plugin.broadcastTag + "Conected player " + ChatColor.YELLOW + pName + ChatColor.GRAY +" is exempt from ban lookups.");
		}
	    }
        }
        
        if(error != null){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.hasPermission("whobannedme.notify.all")){
                    p.sendMessage("Error checking " + pName + ": " + error);
                }
            }
        }
        
        String detailURL = "http://fishbans.com/u/" + pName;
        if (totalBans > 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("whobannedme.notify") && totalBans > plugin.minBans || p.hasPermission("whobannedme.notify.all")) {
                    p.sendMessage(plugin.broadcastTag + "Connected player " + ChatColor.RED + pName + ChatColor.GRAY + " has " + totalBans + " bans. To view ban details, visit " + detailURL);
                }
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("whobannedme.notify.all")) {
                    p.sendMessage(plugin.broadcastTag + "Conected player " + ChatColor.GREEN + pName + ChatColor.GRAY + " has no bans on record.");
                }
            }
        }
    }
}
