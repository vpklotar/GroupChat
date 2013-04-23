/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupchat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Tim
 */
public class PlayerListener implements Listener {
    private Core core;
    
    public PlayerListener(Core core){
        this.core = core;
        this.core.getServer().getPluginManager().registerEvents(this, core);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
       this.core.setupPlayers();
    }    
}
