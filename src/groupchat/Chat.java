/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package groupchat;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author Tim
 */
public class Chat implements Listener{
    protected Core core;
    
    // Constructor
    public Chat(Core core){
        this.core = core;
        this.core.getServer().getPluginManager().registerEvents(this, core);
    }
    
    public void SendMessage(Player p, String message){
        if(p != null){
            p.sendMessage(message);
        }
    }
    
    public void SendMessage(String p, String message){
        this.SendMessage(this.core.getServer().getPlayer(p), message);
    }
}
