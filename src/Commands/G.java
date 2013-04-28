/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Commands;

import groupchat.ChatGroup;
import groupchat.Core;
import groupcore.GroupCommand;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Tim
 */
public class G implements GroupCommand {
    
    @Override
    public void Process(PlayerCommandPreprocessEvent pcpe, ArrayList<String> args) {
        if (args.size() > 0) {
            String method = args.get(0).toLowerCase();
            if ("list".equalsIgnoreCase(method)) {
                if (Core.api.Has(pcpe.getPlayer(), "groupchat.group.list")) {
                    pcpe.getPlayer().sendMessage(ChatColor.GOLD + "---" + ChatColor.BLUE + "Avilable groups" + ChatColor.GOLD + "---");
                    for (ChatGroup cg : Core.GetAvilableChatGroups(pcpe.getPlayer())) {
                        pcpe.getPlayer().sendMessage(cg.GetName());
                    }
                } else {
                    pcpe.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to issue that command!");
                }
            } else if ("create".equalsIgnoreCase(method)) {
                if (Core.api.Has(pcpe.getPlayer(), "groupchat.group.create")) {
                    if (args.size() >= 1) {
                        if (!Core.ChatGroups.containsKey(args.get(1))) {
                            // In case of success:
                            ChatGroup cg = new ChatGroup(args.get(1));
                            Core.ChatGroups.put(args.get(1), cg);
                            cg.Join(Core.players.get(pcpe.getPlayer().getName()), true);
                        } else {
                            pcpe.getPlayer().sendMessage(ChatColor.RED + "That group already exsist!");
                        }
                    } else {
                        pcpe.getPlayer().sendMessage(ChatColor.RED + "/g create <name>");
                    }
                } else {
                    pcpe.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to issue that command!");
                }
            } else if ("join".equalsIgnoreCase(method) || "j".equalsIgnoreCase(method)) {
                if (Core.api.Has(pcpe.getPlayer(), "groupchat.group.join")) {
                    if (args.size() >= 2) {
                        if (Core.ChatGroups.containsKey(args.get(1))) {
                            // In case of success:
                            Core.ChatGroups.get(args.get(1)).Join(Core.players.get(pcpe.getPlayer().getName()), true);
                        } else {
                            pcpe.getPlayer().sendMessage(ChatColor.RED + "That group doesn't exsist!");
                        }
                    } else {
                        pcpe.getPlayer().sendMessage(ChatColor.RED + "/g join <name>");
                    }
                } else {
                    pcpe.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to issue that command!");
                }
            } else if ("del".equalsIgnoreCase(method) || "delete".equalsIgnoreCase(method)) {
                if (args.size() >= 2) {
                    if (Core.api.Has(pcpe.getPlayer(), "groupchat.group.delete") || Core.api.Has(pcpe.getPlayer(), "groupchat." + args.get(1) + ".delete")) {
                        if (Core.ChatGroups.containsKey(args.get(1))) {
                            // In case of success:
                            Core.ChatGroups.get(args.get(1)).Delete(pcpe.getPlayer());
                        } else {
                            pcpe.getPlayer().sendMessage(ChatColor.RED + "That group doesn't exsist!");
                        }
                    } else {
                        pcpe.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to issue that command!");
                    }
                } else {
                    pcpe.getPlayer().sendMessage(ChatColor.RED + "/g del <name>");
                }

            } else if ("l".equalsIgnoreCase(method) || "leave".equalsIgnoreCase(method)) {
                if (args.size() >= 2) {
                    if (Core.api.Has(pcpe.getPlayer(), "groupchat.group.leave")) {
                        if (Core.ChatGroups.containsKey(args.get(1))) {
                            // In case of success:
                            Core.ChatGroups.get(args.get(1)).Leave(Core.players.get(pcpe.getPlayer().getName()), true);
                        } else {
                            pcpe.getPlayer().sendMessage(ChatColor.RED + "That group doesn't exsist!");
                        }
                    } else {
                        pcpe.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions to issue that command!");
                    }
                } else {
                    pcpe.getPlayer().sendMessage(ChatColor.RED + "/g leave <name>");
                }
            }
        } else {
            this.ListPlayerGroups(pcpe.getPlayer());
        }
    }

    public void ListPlayerGroups(Player p) {
        p.sendMessage(ChatColor.GOLD + "---" + ChatColor.BLUE + "Current groups" + ChatColor.GOLD + "---");

        for (String group : Core.players.get(p.getName()).chatGroups) {
            p.sendMessage(group);
        }
    }

    @Override
    public int GetMinArgs() {
        return 0;
    }

    @Override
    public String GetErrorMessage() {
        return ChatColor.RED + "You have to specify group name!";
    }

    @Override
    public String GetDescription() {
        return "Used to interact with my groups";
    }

    @Override
    public String GetName() {
        return "/g";
    }
}
