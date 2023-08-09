package fi.johvu.skinchanger;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class Listeners implements Listener {

    private final Main plugin;
    private final Permission perms;

    public Listeners(Main plugin, Permission perms) {
        this.plugin = plugin;
        this.perms = perms;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        HashMap<UUID, PlayerSkin> players = Main.getPlayers();
        if (players.containsKey(p.getUniqueId())) {
            PlayerSkin data = players.get(p.getUniqueId());
            // check if the skin or group is changed
            URL orgSkin = data.getOrgTexture().getSkin();
            URL skin = p.getPlayerProfile().getTextures().getSkin();
            if (orgSkin != null && skin != null || data.getGroup() != null) {
                if (!orgSkin.equals(skin) || !data.getGroup().equals(perms.getPrimaryGroup(p))) {
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
