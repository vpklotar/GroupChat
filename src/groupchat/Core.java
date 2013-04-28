/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupchat;

import Commands.G;
import Commands.mute;
import groupcore.Config;
import groupcore.GroupCoreAPI;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Tim
 */
public class Core extends JavaPlugin implements Listener {
    public GroupCoreAPI api;
    public Config config;
    //public HashMap<String, ArrayList<String>> PlayerCurrentGroups = new HashMap<>();
    //public HashMap<String, String> PlayerWritingGroup = new HashMap<>();
    public HashMap<String, ChatGroup> ChatGroups = new HashMap<>();
    public HashMap<String, GroupPlayer> players = new HashMap<>();
    public String DefaultChatGroup;
    public int DefaultLevel = 1;
    
    @Override
    public void onEnable(){
        if(Bukkit.getPluginManager().isPluginEnabled("GroupCore")){
            this.api = groupcore.Core.GetAPI();
            this.api.RegisterHook(this);
            
            new PlayerListener(this);
            
            this.SetupConfigs();
            this.setupPlayers();
            this.SetupChatGroups();
            this.setupCommands();
            
        }else{
            this.info("GroupCore is required!");
        }
    }
    
    @Override
    public void onDisable(){
        if(Bukkit.getPluginManager().isPluginEnabled("GroupCore")){
            
            ArrayList<String> s = new ArrayList<>();
            for(String name : this.ChatGroups.keySet()){
                s.add(name);
            }
            this.config.SetList("Groups.ChatGroups", s);
            
            for(String name : this.players.keySet()) {
                GroupPlayer p = this.players.get(name);
                p.Save();
            }
            
            this.config.save();
        }
    }
    
    public void SetupConfigs(){
        this.config = this.api.GetExtentionConfig(this, "config");
        
        this.config.setDefault("ChatInServer", "true");
        
        this.config.setDefault("Format.ChatColorsEnabled", "true");
        this.config.setDefault("Format.Syntax", "(%level%)&f[&b%group%&f] &a%username%: &f%message%");
        
        this.config.setDefault("Groups.Default", "Default");
        this.config.setDefault("Groups.AutoJoinGroupByLevel", "true");
        this.config.setDefault("Groups.DefaultLevel", "1");
        
        ArrayList<String> groups = new ArrayList<>();
        groups.add("Default");
        
        this.config.SetDefaultList("Groups.ChatGroups", groups);
        
        this.config.save();
        
        this.DefaultChatGroup = this.config.getString("Groups.Default");
        this.DefaultLevel = Integer.parseInt(this.config.getString("Groups.DefaultLevel"));
    }
    
    public void SetupChatGroups(){
        for(String s : this.config.GetList("Groups.ChatGroups")){
            this.ChatGroups.put(s, new ChatGroup(this, s));
        }
        
        this.info("Loaded "+this.ChatGroups.size()+" ChatGroups successfully!");
    }
    
    public void setupPlayers() {
        // Load all online players
        for(Player p : this.getServer().getOnlinePlayers()){
            if(!this.players.containsKey(p.getName())) {
                this.players.put(p.getName(), new GroupPlayer(this, p.getName()));
            }
        }
    }
    
    private void setupCommands() {
        this.api.GetCommandHandler().RegisterCommand("/G", new G(this));
        this.api.GetCommandHandler().RegisterCommand("/mute", new mute(this));
    }
    
    public String Syntaxinate(String str, HashMap<String, String> replacement){
        String r = str;
        
        for(String key : replacement.keySet()){
            r = r.replaceAll(key, replacement.get(key));
        }
        
        if("true".equals(this.config.getString("Format.ChatColorsEnabled"))){
            r = this.api.addColor(r, true);
        }
        
        return r;
    }
    
    public void info(String msg){
        Logger.getLogger("Minecraft").info("["+this.getDescription().getName().toString()+" v. "+this.getDescription().getVersion().toString()+"] "+msg);
    } 
    
    public ArrayList<ChatGroup> GetAvilableChatGroups(Player p){
        ArrayList<ChatGroup> r = new ArrayList<>();
        
        for(String s : this.ChatGroups.keySet()){
            if(!this.players.get(p.getName()).chatGroups.contains(s) && this.ChatGroups.get(s).HasAccess(p)) {
                r.add(this.ChatGroups.get(s));
            }
        }
        
        return r;
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        if (!this.players.containsKey(event.getPlayer().getName())) {
            this.players.put(event.getPlayer().getName(), new GroupPlayer(this, event.getPlayer().getName()));
        }
    }
    
    public int GetPlayerLevel(Player p){
        
        if(this.api.Has(p, "groupchat.level")){
            for(String s : this.api.GetPermissionsManager().overlay.GetNodes(p)){
                if(s.startsWith("groupchat.level.")){
                    String w = s.replace("groupchat.level.", "");
                    return Integer.parseInt(w);
                }
            }
        }
        
        return 1;
    }
}
