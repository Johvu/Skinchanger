package fi.johvu.skinchanger.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import fi.johvu.skinchanger.Main;
import fi.johvu.skinchanger.PlayerObject;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkinsCommand implements CommandExecutor {

    Main plugin;

    Permission perms;


    public SkinsCommand(Main plugin, Permission perms) {
        this.perms = perms;
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            PlayerProfile PlayerSkin = p.getPlayerProfile();

            if(!Main.playerObject.containsKey(p.getUniqueId()))
                Main.playerObject.put(p.getUniqueId(), new PlayerObject(p.getUniqueId(), PlayerSkin.getTextures(), null, null, null, null));
            if (!(p.hasPermission("motimaa.identtiteettivarkaus")))
                return false;

            if (args.length == 0){
                p.sendMessage("Laitetaan skini");
                PlayerSkin.setTextures(Main.playerObject.get(p.getUniqueId()).getOrgTexture());
                p.setPlayerProfile(PlayerSkin);
                return false;
            }
            if (args[0].equalsIgnoreCase("skinmerge")){
                p.sendMessage("Yhdistetään skini");

                plugin.changeSkin(p);
            }
            if (args[0].equalsIgnoreCase("null")){
                p.sendMessage("Poistettu skini");
                Main.playerObject.remove(p.getUniqueId());
                p.getPersistentDataContainer().remove(new NamespacedKey(Main.getPlugin(), "PrisonSkin"));
            }
    }
        return false;
    }

}
