package me.whobanned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UpdateCheck {

    URL updateCheck = null;
    URLConnection connection = null;
    BufferedReader reader = null;
    String response = null;

    private final WhoBannedMe plugin;
    private String[] cV1;
    private int currentVersion;

    public UpdateCheck(WhoBannedMe plugin) {
        this.plugin = plugin;
    }

    void check() {

        if (plugin.updateCheck == true) {
            if (plugin.debugMode == true) {
                plugin.getLogger().info("Update check initiated!");
            }

            try {
                updateCheck = new URL("https://api.curseforge.com/servermods/files?projectids=76783");
            } catch (MalformedURLException e) {
                return;
            }

            try {
                connection = updateCheck.openConnection();
                connection.setReadTimeout(5000);
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response = reader.readLine();
            } catch (IOException e) {
                plugin.getLogger().warning("There was a problem checking for updates! Are you connected to the internet?");
                return;
            }

            JSONArray array = (JSONArray) JSONValue.parse(response);
            JSONObject latest = (JSONObject) array.get(array.size() - 1);
            String version = (String) latest.get("name");

            int versionNum = Integer.parseInt(version.replaceAll("[^0-9]", ""));

            String cV = plugin.getDescription().getVersion();
            if (cV.contains(".")) {
                cV1 = cV.split("\\.");

                if (cV1.length == 3) {
                    String cV2 = cV1[0] + cV1[1];
                    currentVersion = Integer.parseInt(cV2);
                } else {
                    currentVersion = 0;
                }
            }

            if (plugin.debugMode == true) {
                plugin.getLogger().log(Level.INFO, "Current version: {0}", plugin.getDescription().getVersion());
                plugin.getLogger().log(Level.INFO, "Latest release version: {0}", versionNum);
            }

            if (currentVersion != 0 && currentVersion < versionNum) {
                plugin.getLogger().info("An update is available!");
                plugin.getLogger().info("Get it at http://dev.bukkit.org/bukkit-plugings/whobannedme");
            }
        }
    }
}
