/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Commands;

import groupchat.Core;
import groupcore.GroupCommand;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Tim
 */
public class mute implements GroupCommand {
    private Core core; // Core refrence for permissions checking
    
    public mute(Core core) {
        this.core = core;
    }
    
    @Override
    public int GetMinArgs() {
        return 2;
    }

    @Override
    public void Process(PlayerCommandPreprocessEvent pcpe, ArrayList<String> args) {
        
        if("player".equalsIgnoreCase(args.get(0))) {
            if(this.core.api.Has(pcpe.getPlayer(), "groupchat.mute.player")) {
                
            }else {
                pcpe.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to do that!");
            }
        }else if("group".equalsIgnoreCase(args.get(0))) {
            
        }else{
            pcpe.getPlayer().sendMessage(ChatColor.RED + "/mute <player or group> <name>");
        }
        
    }

    @Override
    public String GetErrorMessage() {
        return "You need to specify a player or group to mute!";
    }

    @Override
    public String GetDescription() {
        return "Used to mute/unmute players and groups";
    }

    @Override
    public String GetName() {
        return "/mute";
    }
    
}
