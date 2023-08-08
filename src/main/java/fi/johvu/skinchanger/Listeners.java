package fi.johvu.skinchanger;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;
import java.util.logging.Level;

public class Listeners implements Listener {
    Main plugin;
    Permission perms;
    public Listeners(Main plugin, Permission perms) {
        this.plugin = plugin;
        this.perms = perms;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (Main.playerObject.containsKey(p.getUniqueId())) {

            PlayerObject data = Main.playerObject.get(p.getUniqueId());

            // check if the skin or group is changed
            if (data.getOrgTexture().getSkin() != null && p.getPlayerProfile().getTextures().getSkin() != null || data.getGroup() != null) {
                if (!data.getOrgTexture().getSkin().equals(p.getPlayerProfile().getTextures().getSkin()) || !data.getGroup().equals(perms.getPrimaryGroup(p))) {
                    System.out.println("§eSkini tai grouppi vaihtunut poistetaan Skini data!");

                    data.setTextureKey(null);
                    data.setTextureValue(null);
                    data.setGroup(null);
                }
            }
        }
        // change the skin
        plugin.changeSkin(p);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){}
}
