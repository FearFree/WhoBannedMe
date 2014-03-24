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
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class BanLookup {
    
    private final WhoBannedMe plugin;
    private String apiURL;
    
    URL checkURL = null;
    URLConnection connection = null;
    BufferedReader reader = null;
    String response = null;
    
    public BanLookup(WhoBannedMe plugin){
	this.plugin = plugin;
    }
    
    public void check(Player player) throws IOException {
        String pName = player.getName();
        //if (player.hasPermission("whobannedme.exempt")){
        //    return;
        //}
	try{
	    checkURL = new URL("http://api.fishbans.com/stat/" + pName + "/");
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
		if(o.get("success") != "true"){
		    plugin.getLogger().log(Level.WARNING, "Error: {0}", o.get("error"));
		    return;
		} 
		if(o.get("stats") != null){
		    Map output = (Map)o.get("stats");
		    if(plugin.consoleOutput == true || plugin.debugMode == true){
			plugin.getLogger().log(Level.INFO, "Player Name: {0}", output.get("username"));
			plugin.getLogger().log(Level.INFO, "Player UUID: {0}", output.get("uuid"));
			plugin.getLogger().log(Level.INFO, "Total Bans: {0}", output.get("totalbans"));
		    }
		}
	    } catch (ParseException ex) {
		Logger.getLogger(BanLookup.class.getName()).log(Level.SEVERE, null, ex);
	    }
    }
    
}
