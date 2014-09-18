package me.whobanned;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import net.ae97.fishbans.api.Ban;
import net.ae97.fishbans.api.BanService;
import net.ae97.fishbans.api.Fishbans;
import net.ae97.fishbans.api.FishbansPlayer;
import net.ae97.fishbans.api.exceptions.NoSuchUserException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWhoBanned implements CommandExecutor {

    private final WhoBannedMe plugin;

    public CommandWhoBanned(WhoBannedMe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sentBy, Command command, String string, String[] args) {
        if (command.getName().equalsIgnoreCase("whobanned")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sentBy.hasPermission("whobannedme.reload")) {
                        plugin.reloadConfig();
                        plugin.load();
                        sentBy.sendMessage(plugin.broadcastTag + "Configuration reloaded.");
                    } else {
                        sentBy.sendMessage(plugin.noPerms);
                    }
                    return true;
                }

                if (sentBy.hasPermission("whobannedme.lookup")) {
                    try {
                        FishbansPlayer player = Fishbans.getFishbanPlayer(args[0]);
                        if (player.getBanCount() == 0) {
                            sentBy.sendMessage(plugin.broadcastTag + "No bans found for " + player.getName());
                        } else {
                            sentBy.sendMessage(plugin.broadcastTag + player.getName() + " has " + player.getBanCount() + " ban" + (player.getBanCount() == 1 ? "" : "s"));
                        }
                    } catch (IOException e) {
                        plugin.getLogger().log(Level.SEVERE, "Error occured while checking bans for " + args[0], e);
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.RED + "An error occured while checking bans: " + e.getMessage());
                    } catch (NoSuchUserException e) {
                        sentBy.sendMessage(plugin.broadcastTag + ChatColor.RED + "No user with that name exists");
                    }
                } else {
                    sentBy.sendMessage(plugin.noPerms);
                }
                return true;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("details")) {
                    if (sentBy.hasPermission("whobannedme.lookup.details")) {
                        try {
                            FishbansPlayer player = Fishbans.getFishbanPlayer(args[1]);
                            if (player.getBanCount() == 0) {
                                sentBy.sendMessage(plugin.broadcastTag + "No bans found for " + player.getName());
                            } else {
                                sentBy.sendMessage(plugin.broadcastTag + player.getName() + " has " + player.getBanCount() + " ban" + (player.getBanCount() == 1 ? "" : "s"));
                                for (BanService service : BanService.values()) {
                                    List<Ban> bans = player.getBanList(service);
                                    if (bans == null || bans.isEmpty()) {
                                        continue;
                                    }
                                    sentBy.sendMessage(ChatColor.GRAY + "-------------------------------");
                                    sentBy.sendMessage(ChatColor.YELLOW + "Ban Service: " + service.getDisplayName());
                                    for (Ban ban : bans) {
                                        sentBy.sendMessage(ChatColor.GRAY + ban.getServer() + ChatColor.WHITE + "  |  " + ChatColor.GRAY + ban.getReason());
                                    }
                                }
                            }
                        } catch (IOException e) {
                            plugin.getLogger().log(Level.SEVERE, "Error occured while checking bans for " + args[1], e);
                            sentBy.sendMessage(plugin.broadcastTag + ChatColor.RED + "An error occured while checking bans: " + e.getMessage());
                        } catch (NoSuchUserException e) {
                            sentBy.sendMessage(plugin.broadcastTag + ChatColor.RED + "No user with that name exists");
                        }
                    } else {
                        sentBy.sendMessage(plugin.noPerms);
                    }
                }
                return true;
            }
        } else {
            sentBy.sendMessage(plugin.broadcastTag + "Usage: /whobanned [details] <playername>");
        }
        return false;
    }
}
