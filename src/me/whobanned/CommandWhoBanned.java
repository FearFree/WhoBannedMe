package me.whobanned;

import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWhoBanned implements CommandExecutor {

    private final WhoBannedMe plugin;
    private final BanLookup lookup;
    public String pName;

    public CommandWhoBanned(WhoBannedMe plugin, BanLookup lookup) {
        this.plugin = plugin;
        this.lookup = lookup;
    }

    @Override
    public boolean onCommand(CommandSender sentBy, Command command, String string, String[] args) {
        if (command.getName().equalsIgnoreCase("whobanned")) {
            if (args.length == 1) {
                if (sentBy.hasPermission("whobannedme.lookup")) {
                    pName = args[0];

                    try {
                        lookup.check(pName);
                        lookup.report(sentBy, pName);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    sentBy.sendMessage(plugin.noPerms);
                }
                return true;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("details")) {
                    if (sentBy.hasPermission("whobannedme.lookup.details")) {
                        pName = args[1];
                        try {
                            lookup.details(pName, sentBy);
                        } catch (IOException ex) {
                            ex.printStackTrace();
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
