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
public class Chat{
    
    public void SendMessage(String player, String group, GroupPlayer p, String message){
        if(p != null){
            p.sendMessage(player, group, message);
        }
    }
    
    public void SendMessage(String player, String group, String to, String message){
        this.SendMessage(player, group, Core.players.get(to), message);
    }
}
