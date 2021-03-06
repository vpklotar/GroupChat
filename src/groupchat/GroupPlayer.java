/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupchat;

import groupcore.Config;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Tim
 */
public class GroupPlayer implements Listener {
    private Config config;
    public String name = "";
    private ArrayList<String> mutedPlayers = new ArrayList<>();
    private ArrayList<String> mutedGroups = new ArrayList<>();
    public ArrayList<String> chatGroups = new ArrayList<>();
    public String writingGroup = "";
    
    public GroupPlayer(String name) {
        this.name = name;
        this.writingGroup = Core.DefaultChatGroup;
        Core.getPlugin().getServer().getPluginManager().registerEvents(this, Core.getPlugin());
        
        this.setupConfigs();
    }
    
    private void setupConfigs() {
        this.config = Core.api.GetExtentionConfig(Core.getPlugin(), "Players/"+this.name);
        
        this.config.setDefault("WritingGroup", writingGroup);
        this.config.SetDefaultList("muted.players", mutedPlayers);
        this.config.SetDefaultList("muted.groups", mutedGroups);
        this.config.SetDefaultList("ChatGroups", chatGroups);
        
        this.config.save();
        
        this.mutedPlayers = (ArrayList<String>) this.config.GetList("muted.players");
        this.mutedGroups = (ArrayList<String>) this.config.GetList("muted.groups");
        this.writingGroup = this.config.getString("WritingGroup");
    }
    
    public void Save() {
        this.config.setValue("WritingGroup", writingGroup);
        this.config.SetList("muted.players", mutedPlayers);
        this.config.SetList("muted.groups", mutedGroups);
        this.config.SetList("ChatGroups", chatGroups);
        
        this.config.save();
    }
    
    // Events
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(event.getPlayer().getName().equals(this.name)) {
            Core.ChatGroups.get(this.writingGroup).BroadcastMessage(event.getPlayer().getName(), event.getMessage(), true);
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        /*this.core.info("Player " + this.name + " left, preparing to remove object from refrence!");
        this.core.players.remove(this.name);
        try {
            this.finalize();
        } catch (Throwable ex) {
            this.core.info(ex.getMessage());
        }*/
    }
     
     // End of events
    
    void sendMessage(String player, String group, String message) {
        if(this.Online() && !this.mutedPlayers.contains(player) && !this.mutedGroups.contains(group)) { // If the player is online and the origin of the message isn't muted proceed
            Core.getPlugin().getServer().getPlayer(this.name).sendMessage(message); // Send the message to the player
        }
    }
    
    public boolean Online() { // Is the player online?
        Player p  = Core.getPlugin().getServer().getPlayer(this.name);
        if(p == null) {
            return false;
        }
        return p.isOnline();
    }
    
    public void toggleGroupMute(String groupName) { // Toggle the mute of a group
        if(this.mutedGroups.contains(groupName)) { // Does the mute exsist? Then remove it
            this.mutedGroups.remove(groupName); // Remove the mute
            if(this.Online()) {
                this.sendMessage(this.name, "", ChatColor.BLUE + "Group " + groupName + " is now un-muted!");
            }
        }else {
            this.mutedGroups.add(groupName); // Add to mute list

            if (this.Online()) {
                this.sendMessage(this.name, "", ChatColor.BLUE + "Group " + groupName + " is now muted!");
            }
        }
        this.Save();
    }
    
    public void togglePlayerMute(String playerName) { // Same procedure as toggleGroupMute excpet that this is for a player
        if(this.mutedPlayers.contains(playerName)) {
            this.mutedPlayers.remove(playerName);
            if(this.Online()) {
                this.sendMessage(this.name, "", ChatColor.BLUE + "Player " + playerName + " is now un-muted!");
            }
        }else {
            this.mutedPlayers.add(playerName);

            if (this.Online()) {
                this.sendMessage(this.name, "", ChatColor.BLUE + "Player " + playerName + " is now muted!");
            }
        }
        this.Save();
    }
    
    
    
}
