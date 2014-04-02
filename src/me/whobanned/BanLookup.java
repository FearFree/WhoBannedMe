package me.whobanned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
    private Object username;
    private Object UUID;
    private long banCount;

    public BanLookup(WhoBannedMe plugin) {
        this.plugin = plugin;
    }

    public void check(String pName) throws IOException {
        exempt = false;
        error = null;
        totalBans = 0;
        if (Bukkit.getPlayer(pName) != null) {
            Player player = Bukkit.getPlayer(pName);
            if (player.hasPermission("whobannedme.exempt")) {

                exempt = true;

                if (plugin.debugMode == true || plugin.consoleOutput == true) {
                    plugin.getLogger().info("Player check cancelled by permissions");
                }
                return;
            }
        }
        try {
            checkURL = new URL("http://api.fishbans.com/stats/" + pName + "/");
        } catch (MalformedURLException e) {
            return;
        }
        try {
            connection = checkURL.openConnection();
            connection.setReadTimeout(5000);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = reader.readLine();
        } catch (IOException e) {
            plugin.getLogger().warning("Could not reach ban server!");
        }
        try {
            JSONParser j = new JSONParser();
            JSONObject o = (JSONObject) j.parse(response);
            success = (boolean) o.get("success");
            if (success != true) {
                error = o.get("error");
                plugin.getLogger().log(Level.WARNING, "Error: {0}", error);
                return;
            } else {
                if (plugin.debugMode == true) {
                    plugin.getLogger().log(Level.INFO, "Result found!");
                }
            }
            if (o.get("stats") != null) {
                Map output = (Map) o.get("stats");
                username = output.get("username");
                UUID = output.get("uuid");
                totalBans = Integer.valueOf(String.valueOf(output.get("totalbans")));

                if (plugin.consoleOutput == true || plugin.debugMode == true) {
                    plugin.getLogger().log(Level.INFO, "Player Name: {0}", username);
                    plugin.getLogger().log(Level.INFO, "Player UUID: {0}", UUID);
                    plugin.getLogger().log(Level.INFO, "Total Bans: {0}", totalBans);
                }

            }
        } catch (ParseException ex) {
            Logger.getLogger(BanLookup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void details(String pName, CommandSender sentBy) throws IOException {
        exempt = false;
        error = null;
        if (Bukkit.getPlayer(pName) != null) {
            Player player = Bukkit.getPlayer(pName);
            if (player.hasPermission("whobannedme.exempt")) {
                exempt = true;
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + pName + ChatColor.GRAY + " is exempt from ban lookups");

                if (plugin.debugMode == true || plugin.consoleOutput == true) {
                    plugin.getLogger().info("Player check cancelled by permissions");
                }
                return;
            }
        }

        try {
            checkURL = new URL("http://api.fishbans.com/bans/" + pName + "/");
        } catch (MalformedURLException e) {
            return;
        }

        try {
            connection = checkURL.openConnection();
            connection.setReadTimeout(5000);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = reader.readLine();
        } catch (IOException e) {
            if (plugin.debugMode == true) {
                plugin.getLogger().warning("Could not reach ban server!");
            }
            sentBy.sendMessage(plugin.broadcastTag + ChatColor.RED + "Could not reach ban server, try again in a few minutes.");
        }

        try {
            JSONParser j = new JSONParser();
            JSONObject o = (JSONObject) j.parse(response);
            success = (boolean) o.get("success");
            if (success != true) {
                error = o.get("error");
                if (plugin.debugMode == true || plugin.consoleOutput == true) {
                    plugin.getLogger().log(Level.WARNING, "Error: {0}", error);
                }
                sentBy.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + error);
                return;
            } else {
                if (plugin.debugMode == true) {
                    plugin.getLogger().log(Level.INFO, "Result found!");
                }
            }
            if (o.get("bans") != null) {
                Map output = (Map) o.get("bans");
                username = output.get("username");
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.GOLD + "Results for " + ChatColor.BOLD + pName);
                UUID = output.get("uuid");

                if (plugin.consoleOutput == true || plugin.debugMode == true) {
                    plugin.getLogger().log(Level.INFO, "Player Name: {0}", username);
                    plugin.getLogger().log(Level.INFO, "Player UUID: {0}", UUID);

                }

                Map serviceOutput = (Map) output.get("service");
                sentBy.sendMessage(plugin.broadcastTag + "Server | Ban Reason");

                //McBans
                banCount = 0L;
                Map mcbansOutput = (Map) serviceOutput.get("mcbans");
                banCount = (long) mcbansOutput.get("bans");
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + "Bans found in McBans legacy data: " + ChatColor.RED + banCount);

                if (banCount > 0L) {
                    Map mcbansInfo = (Map) mcbansOutput.get("ban_info");
                    Iterator entries = mcbansInfo.entrySet().iterator();
                    while (entries.hasNext()) {
                        Entry thisEntry = (Entry) entries.next();
                        Object key = thisEntry.getKey();
                        Object value = thisEntry.getValue();
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.GRAY + key + ChatColor.WHITE + " | " + ChatColor.GRAY + value);
                    }
                }

                //McBouncer
                banCount = 0L;
                Map mcbouncerOutput = (Map) serviceOutput.get("mcbouncer");
                banCount = (long) mcbouncerOutput.get("bans");
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + "Bans found in McBouncer data: " + ChatColor.RED + banCount);

                if (banCount > 0L) {
                    Map mcbouncerInfo = (Map) mcbouncerOutput.get("ban_info");
                    Iterator entries = mcbouncerInfo.entrySet().iterator();
                    while (entries.hasNext()) {
                        Entry thisEntry = (Entry) entries.next();
                        Object key = thisEntry.getKey();
                        Object value = thisEntry.getValue();
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.GRAY + key + ChatColor.WHITE + " | " + ChatColor.GRAY + value);
                    }
                }

                //MineBans
                banCount = 0L;
                Map minebansOutput = (Map) serviceOutput.get("minebans");
                banCount = (long) minebansOutput.get("bans");
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + "Bans found in MineBans data: " + ChatColor.RED + banCount);

                if (banCount > 0L) {
                    Map minebansInfo = (Map) minebansOutput.get("ban_info");
                    Iterator entries = minebansInfo.entrySet().iterator();
                    while (entries.hasNext()) {
                        Entry thisEntry = (Entry) entries.next();
                        Object key = thisEntry.getKey();
                        Object value = thisEntry.getValue();
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.GRAY + key + ChatColor.WHITE + " | " + ChatColor.GRAY + value);
                    }
                }

                //McBlockIt
                banCount = 0L;
                Map mcblockitOutput = (Map) serviceOutput.get("mcblockit");
                banCount = (long) mcblockitOutput.get("bans");
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + "Bans found in McBlockIt legacy data: " + ChatColor.RED + banCount);

                if (banCount > 0L) {
                    Map mcblockitInfo = (Map) mcblockitOutput.get("ban_info");
                    Iterator entries = mcblockitInfo.entrySet().iterator();
                    while (entries.hasNext()) {
                        Entry thisEntry = (Entry) entries.next();
                        Object key = thisEntry.getKey();
                        Object value = thisEntry.getValue();
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.GRAY + key + ChatColor.WHITE + " | " + ChatColor.GRAY + value);
                    }
                }

                //Glizer
                banCount = 0L;
                Map glizerOutput = (Map) serviceOutput.get("glizer");
                banCount = (long) glizerOutput.get("bans");
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + "Bans found in Glizer data: " + ChatColor.RED + banCount);

                if (banCount > 0L) {
                    Map glizerInfo = (Map) glizerOutput.get("ban_info");
                    Iterator entries = glizerInfo.entrySet().iterator();
                    while (entries.hasNext()) {
                        Entry thisEntry = (Entry) entries.next();
                        Object key = thisEntry.getKey();
                        Object value = thisEntry.getValue();
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.GRAY + key + ChatColor.WHITE + " | " + ChatColor.GRAY + value);
                    }
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(BanLookup.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void notify(Player player) throws IOException {
        String pName = player.getName();
        if (error != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("whobannedme.notify.all")) {
                    p.sendMessage("Error checking " + pName + ": " + error);
                }
            }
            return;
        }

        if (exempt != false) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("whobannedme.notify.all")) {
                    p.sendMessage(plugin.broadcastTag + "Conected player " + ChatColor.YELLOW + pName + ChatColor.GRAY + " is exempt from ban lookups.");
                }
            }
            return;
        }

        if (error != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("whobannedme.notify.all")) {
                    p.sendMessage("Error checking " + pName + ": " + error);
                }
            }
            return;
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

    public void report(CommandSender sentBy, String pName) {
        if (error != null) {
            sentBy.sendMessage(plugin.broadcastTag + "Error checking " + pName + ": " + error);
            return;
        }

        if (exempt != false) {
            sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + pName + ChatColor.GRAY + " is exempt from ban lookups.");
            return;
        }

        if (totalBans > 0) {
            if (totalBans == 1) {
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.YELLOW + pName + ChatColor.GRAY + " has " + totalBans + " ban. To view ban details, type /whobanned details " + pName);
            } else {
                sentBy.sendMessage(plugin.broadcastTag + ChatColor.RED + pName + ChatColor.GRAY + " has " + totalBans + " bans. To view ban details, type /whobanned details " + pName);
            }
        } else {
            sentBy.sendMessage(plugin.broadcastTag + ChatColor.GREEN + pName + ChatColor.GRAY + " has no bans on record.");
        }
    }
}
