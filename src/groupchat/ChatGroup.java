/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupchat;

import groupcore.Config;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Tim
 */
public class ChatGroup extends Chat {

    private ArrayList<String> Players = new ArrayList<>();
    private Config config;
    private String name;
    private int Level = 1;

    public String GetName() {
        return this.name;
    }

    public ChatGroup(Core core, String name) {
        super(core);
        this.name = name;

        this.SetupConfigs();
        
        if("true".equals(this.core.config.getString("Groups.AutoJoinGroupByLevel"))){
            /*for(Player p : this.core.getServer().getOnlinePlayers()){
                this.Listen(p.getName());
            }
            for(OfflinePlayer p : this.core.getServer().getOfflinePlayers()){
                this.Listen(p.getName());
            }*/
            
            for(String n : this.core.players.keySet()) {
                GroupPlayer p = this.core.players.get(n);
                this.Listen(p);
            }
        }
    }
    
    public void BroadcastMessage(String player, String message, boolean syntaxinate) {
        if(syntaxinate) {
            message = this.Syntaxinate(this.core.getServer().getPlayer(player), message);
        }
        this.BroadcastMessage(player, message);
    }
    
    public void BroadcastMessage(String player, String message) {
        this.RemoveDuplications();
        this.core.info(message);
        
        ArrayList<String> sentToPlayer = new ArrayList<>(); // Easy quick fix
        
        for (String p : this.Players) {
            if(!sentToPlayer.contains(p)) {
                this.SendMessage(player, this.name, p, message);
                sentToPlayer.add(p);
            }
        }
    }

    public void AddPlayer(Player p) {
        this.AddPlayer(p.getName());
    }

    public void AddPlayer(String p) {
        if(!this.Players.contains(p)) {
            this.Players.add(p);
        }
        this.RemoveDuplications();
    }

    public String Syntaxinate(Player p, String message) {
        String syntax = this.config.getString("Group.Syntax");
        if ("Default".equals(syntax)) {
            syntax = this.core.config.getString("Format.Syntax");
        }
        HashMap<String, String> replace = new HashMap<>();

        replace.put("%player%", p.getDisplayName());
        replace.put("%username%", p.getName());
        replace.put("%group%", this.name);
        replace.put("%message%", message);
        replace.put("%prefix%", this.core.api.GetPermissionsManager().overlay.GetPrefix(p));
        replace.put("%level%", Integer.toString(this.core.GetPlayerLevel(p)));

        return this.core.Syntaxinate(syntax, replace);
    }

    public void SetupConfigs() {
        this.config = this.core.api.GetExtentionConfig(this.core, "ChatGroups/" + name);

        this.config.setDefault("Group.Name", this.name);
        this.config.setDefault("Group.Syntax", "Default");
        this.config.setDefault("Group.Message.Join", "Wellcome %username% to %group%!");
        this.config.setDefault("Group.Message.Leave", "See you later %username%!");

        this.config.SetDefaultList("Players.List", this.Players);

        this.config.save();

        this.Players = (ArrayList<String>) this.config.GetList("Players.List");
        this.RemoveDuplications();
    }
    
    private void RemoveDuplications() {
        ArrayList<String> temp = new ArrayList<String>();
        
        for(String s : this.Players) {
            if(!temp.contains(s)) {
                temp.add(s);
            }
        }
        
        this.Players = temp;
    }

    public void Save() {
        this.config.SetList("Players.List", this.Players);

        this.config.save();
    }

    public void SetupPlayers() {
        for (String s : this.Players) {
            if (this.core.getServer().getPlayer(s).isOnline()) {
                this.Join(this.core.players.get(s), false);
            }
        }
    }

    public boolean HasAccess(Player p) {
        if(this.core.GetPlayerLevel(p) < this.Level){            
            return false;
        }
        
        return true;
    }
    
    public void Listen(GroupPlayer p){
        /*if (this.core.PlayerCurrentGroups.get(p) == null) {
            this.core.PlayerCurrentGroups.put(p, new ArrayList<String>());
        }
        
        if(!this.Players.contains(p)){
            this.AddPlayer(p);
        }
        
        if(!this.core.PlayerCurrentGroups.get(p).contains(this.name)){
            this.core.PlayerCurrentGroups.get(p).add(this.name);
        }*/
        
        this.AddPlayer(p.name);
        
        /*if(!p.chatGroups.contains(this.name)) {
            p.chatGroups.add(this.name);
        }*/
        
        this.Save();
    }

    public void Join(GroupPlayer p, boolean Message) {
        /*if (this.core.PlayerCurrentGroups.get(p.name) == null) {
            this.core.PlayerCurrentGroups.put(p.getName(), new ArrayList<String>());
        }

        if (!this.core.PlayerWritingGroup.containsKey(p.getName()) || Message) {
            this.core.PlayerWritingGroup.put(p.getName(), this.name);
            this.core.info("Set "+p.getName()+" to "+this.name);
        }
        
        if(!this.Players.contains(p.getName())){
            this.AddPlayer(p);
        }
        
        if(!this.core.PlayerCurrentGroups.get(p.getName()).contains(this.name)){
            this.core.PlayerCurrentGroups.get(p.getName()).add(this.name);
        }*/
        
        this.AddPlayer(p.name);
        
        if(!p.chatGroups.contains(this.name))
        {
            p.chatGroups.add(this.name);
        }
        p.writingGroup = this.name;
        
        
        this.Save();
        p.Save();

        if (Message) {
            p.sendMessage("", this.name, this.Syntaxinate(this.core.getServer().getPlayer(p.name), this.config.getString("Group.Message.Join")));
        }
    }
    
    public void Leave(GroupPlayer p, boolean Message){
        p.chatGroups.remove(this.name);
        
        if(p.writingGroup.equals(this.name)) {
            if(p.chatGroups.size() < 1){
                p.chatGroups.add(name);
            }
            p.writingGroup = p.chatGroups.get(0); // If the player had this as it's writing group go back to the first in his index
        }
        
        this.Save();
        p.Save();

        if (Message && p.Online()) {
            p.sendMessage("", this.name, this.Syntaxinate(this.core.getServer().getPlayer(p.name), this.config.getString("Group.Message.Leave")));
        }
    }
    
    public void Delete(Player p){
        this.core.ChatGroups.remove(this.name);
        for(String s : this.Players){
            this.Leave(this.core.players.get(s), true);
        }
        new File(this.config.config.getCurrentPath()).delete();
        p.sendMessage(ChatColor.RED+"Group "+this.name+" removed as requested!");
    }
}
