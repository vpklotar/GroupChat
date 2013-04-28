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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Tim
 */
public class Core extends JavaPlugin implements Listener {
    public static GroupCoreAPI api;
    public static Config config;
    public static HashMap<String, ChatGroup> ChatGroups = new HashMap<>();
    public static HashMap<String, GroupPlayer> players = new HashMap<>();
    public static String DefaultChatGroup;
    public static int DefaultLevel = 1;
    
    @Override
    public void onEnable(){
        if(Bukkit.getPluginManager().isPluginEnabled("GroupCore")){
            Core.api = groupcore.Core.GetAPI();
            Core.api.RegisterHook(this);
            
            new PlayerListener(this);
            
            this.SetupConfigs();
            this.setupPlayers();
            this.SetupChatGroups();
            this.setupCommands();
            
        }else{
            Core.info("GroupCore is required!");
        }
    }
    
    @Override
    public void onDisable(){
        if(Bukkit.getPluginManager().isPluginEnabled("GroupCore")){
            
            ArrayList<String> s = new ArrayList<>();
            for(String name : Core.ChatGroups.keySet()){
                s.add(name);
            }
            Core.config.SetList("Groups.ChatGroups", s);
            
            for(String name : Core.players.keySet()) {
                GroupPlayer p = Core.players.get(name);
                p.Save();
            }
            
            Core.config.save();
        }
    }
    
    public void SetupConfigs(){
        Core.config = this.api.GetExtentionConfig(this, "config");
        
        Core.config.setDefault("ChatInServer", "true");
        
        Core.config.setDefault("Format.ChatColorsEnabled", "true");
        Core.config.setDefault("Format.Syntax", "(%level%)&f[&b%group%&f] &a%username%: &f%message%");
        
        Core.config.setDefault("Groups.Default", "Default");
        Core.config.setDefault("Groups.AutoJoinGroupByLevel", "true");
        Core.config.setDefault("Groups.DefaultLevel", "1");
        
        ArrayList<String> groups = new ArrayList<>();
        groups.add("Default");
        
        Core.config.SetDefaultList("Groups.ChatGroups", groups);
        
        Core.config.save();
        
        Core.DefaultChatGroup = Core.config.getString("Groups.Default");
        Core.DefaultLevel = Integer.parseInt(Core.config.getString("Groups.DefaultLevel"));
    }
    
    public void SetupChatGroups(){
        for(String s : Core.config.GetList("Groups.ChatGroups")){
            Core.ChatGroups.put(s, new ChatGroup(s));
        }
        
        Core.info("Loaded "+Core.ChatGroups.size()+" ChatGroups successfully!");
    }
    
    public void setupPlayers() {
        // Load all online players
        for(Player p : this.getServer().getOnlinePlayers()){
            if(!Core.players.containsKey(p.getName())) {
                Core.players.put(p.getName(), new GroupPlayer(this, p.getName()));
            }
        }
    }
    
    private void setupCommands() {
        Core.api.GetCommandHandler().RegisterCommand("/G", new G());
        Core.api.GetCommandHandler().RegisterCommand("/mute", new mute());
    }
    
    public static String Syntaxinate(String str, HashMap<String, String> replacement){
        String r = str;
        
        for(String key : replacement.keySet()){
            r = r.replaceAll(key, replacement.get(key));
        }
        
        if("true".equals(Core.config.getString("Format.ChatColorsEnabled"))){
            r = Core.api.addColor(r, true);
        }
        
        return r;
    }
    
    public static Core getPlugin() {
        return (Core) Bukkit.getPluginManager().getPlugin("GroupChat");
    }
    
    public static void info(String msg){
        Logger.getLogger("Minecraft").info("["+Core.getPlugin().getDescription().getName().toString()+" v. "+Core.getPlugin().getDescription().getVersion().toString()+"] "+msg);
    } 
    
    public static ArrayList<ChatGroup> GetAvilableChatGroups(Player p){
        ArrayList<ChatGroup> r = new ArrayList<>();
        
        for(String s : Core.ChatGroups.keySet()){
            if(!Core.players.get(p.getName()).chatGroups.contains(s) && Core.ChatGroups.get(s).HasAccess(p)) {
                r.add(Core.ChatGroups.get(s));
            }
        }
        
        return r;
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        if (!Core.players.containsKey(event.getPlayer().getName())) {
            Core.players.put(event.getPlayer().getName(), new GroupPlayer(this, event.getPlayer().getName()));
        }
    }
    
    public static int GetPlayerLevel(Player p){
        
        if(Core.api.Has(p, "groupchat.level")){
            for(String s : Core.api.GetPermissionsManager().overlay.GetNodes(p)){
                if(s.startsWith("groupchat.level.")){
                    String w = s.replace("groupchat.level.", "");
                    return Integer.parseInt(w);
                }
            }
        }
        
        return 1;
    }
}
