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
    
    public void SendMessage(String player, String group, GroupPlayer p, String message){
        if(p != null){
            p.sendMessage(player, group, message);
        }
    }
    
    public void SendMessage(String player, String group, String to, String message){
        this.SendMessage(player, group, this.core.players.get(to), message);
    }
}
