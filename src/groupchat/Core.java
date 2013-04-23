/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupchat;

import Commands.G;
import groupcore.Config;
import groupcore.GroupCoreAPI;
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
    public HashMap<String, ArrayList<String>> PlayerCurrentGroups = new HashMap<>();
    public HashMap<String, String> PlayerWritingGroup = new HashMap<>();
    public HashMap<String, ChatGroup> ChatGroups = new HashMap<>();
    public String DefaultChatGroup;
    public int DefaultLevel = 1;
    
    @Override
    public void onEnable(){
        if(Bukkit.getPluginManager().isPluginEnabled("GroupCore")){
            this.api = groupcore.Core.GetAPI();
            this.api.RegisterHook(this);
            
            new PlayerListener(this);
            
            this.SetupConfigs();
            this.SetupChatGroups();
            this.setupPlayers();
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
            
            for(String name : this.PlayerCurrentGroups.keySet()){
                this.config.SetList("Players.List."+name, this.PlayerCurrentGroups.get(name));
            }
            
            for(String name : this.PlayerWritingGroup.keySet()){
                this.config.setValue("Players."+name, this.PlayerWritingGroup.get(name));
            }
            
            this.config.save();
        }
    }
    
    public void SetupConfigs(){
        this.config = this.api.GetExtentionConfig(this, "config");
        
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
        for(Player p : this.getServer().getOnlinePlayers()){
            if(!this.config.config.contains("Players.List."+p.getName()) || this.config.GetList("Players.List."+p.getName()).isEmpty()){
                this.ChatGroups.get(this.DefaultChatGroup).Join(p, true);
            }else{
                for(String s : this.config.GetList("Players.List."+p.getName())){
                    this.ChatGroups.get(s).Join(p, false);
                }
                
                this.PlayerWritingGroup.put(p.getName(), this.config.getString("Players."+p.getName()));
            }
        }
    }
    
    private void setupCommands() {
        G g = new G(this);
        this.api.GetCommandHandler().RegisterCommand("/G", g);
        //this.api.GetCommandHandler().RegisterCommand("G", g);
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
            if(!this.PlayerCurrentGroups.get(p.getName()).contains(s) && this.ChatGroups.get(s).HasAccess(p)) {
                r.add(this.ChatGroups.get(s));
            }
        }
        
        return r;
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        this.setupPlayers();
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

    public GroupPlayer getPlayer(String p) {
        
    }
}
