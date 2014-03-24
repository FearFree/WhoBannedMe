package me.whobanned;

import java.io.InputStream;
import java.net.URL;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.bukkit.entity.Player;


public class BanLookup {
    
    private WhoBannedMe plugin;
    
    public void check(Player player){
        String pName = player.getName();
        if (player.hasPermission("whobannedme.exempt")){
            return;
        }
        
        try {
            URL url = new URL("http://api.fishbans.com/stats/" + pName + "/");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
