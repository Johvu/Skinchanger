package fi.johvu.skinchanger.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import fi.johvu.skinchanger.Main;
import fi.johvu.skinchanger.PlayerSkin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

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
            UUID uuid = p.getUniqueId();
            PlayerProfile PlayerSkin = p.getPlayerProfile();
            HashMap<UUID, fi.johvu.skinchanger.PlayerSkin> players = Main.getPlayers();

            if (!players.containsKey(uuid)) {
                players.put(uuid,
                        new PlayerSkin(uuid, PlayerSkin.getTextures(), null, null, null, null));
            }

            if (!(p.hasPermission("skinchanger.skin"))) {
                return false;
            } else if (args.length == 0) {
                p.sendMessage("Laitetaan skini");
                PlayerSkin.setTextures(players.get(uuid).getOrgTexture());
                p.setPlayerProfile(PlayerSkin);
            } else if (args[0].equalsIgnoreCase("skinmerge")) {
                p.sendMessage("Yhdistetään skini");
                plugin.changeSkin(p);
            } else if (args[0].equalsIgnoreCase("delete")) {
                p.sendMessage("Poistettu skini");
                players.remove(uuid);
                p.getPersistentDataContainer().remove(new NamespacedKey(Main.getPlugin(), "Skin"));
            }
        }
        return false;
    }

}
