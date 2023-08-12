package fi.johvu.skinchanger;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fi.johvu.skinchanger.commands.SkinsCommand;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    @Getter
    private static Main plugin;
    @Getter
    private static Permission perms = null;
    @Getter
    private static HashSet<Player> queue = new HashSet<Player>();
    @Getter
    private static HashMap<UUID, PlayerSkin> players = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        setupPermissions();
        getCommand("skins").setExecutor(new SkinsCommand(this, perms));
        getServer().getPluginManager().registerEvents(new Listeners(this, perms), this);
        loadConfig();

        if (!(new File(getDataFolder(), "config.yml")).exists()) {
            saveDefaultConfig();
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!getQueue().isEmpty()) {
                Player p = (Player) getQueue().toArray()[0];
                getQueue().remove(p);
                if (p.isOnline()) {
                    plugin.getServer().getLogger().log(Level.FINE, "Ladataan! " + p.getName());
                    downloadAndApplySkin(p, "final_" + p.getPlayer().getName(), getSourceFile(perms, p), p.getPlayerProfile());
                }
            }
        }, 10, 500);

    }

    public Component parseMesssage(String str) {
        var mm = MiniMessage.miniMessage();
        return mm.deserialize(str);
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void changeSkin(Player p) {
        PlayerProfile playerProfile = p.getPlayerProfile();
        if (!players.containsKey(p.getUniqueId())) {
            PlayerSkin playerSkin = new PlayerSkin(p.getUniqueId(), p.getPlayerProfile().getTextures(), p.getPlayerProfile().getTextures(), null, null, null);
            try {
                // load data from data container
                playerSkin.loadFromDataContainer();
            } catch (ArrayIndexOutOfBoundsException ignored) {
                // if data dosent found print it
                System.out.println("§cDataa ei löydetty luodaan uusi data table!");
            }
            // put object to hashset
            players.put(p.getUniqueId(), playerSkin);
            System.out.println("§aPelajaa laitettu kantaan!");
            p.setPlayerProfile(playerProfile);
        }

        // get the object
        PlayerSkin data = players.get(p.getUniqueId());

        // check that there is everything in the data
        if (data.getNewTexture() != null && data.getTextureKey() != null && data.getGroup() != null) {
            //set textures
            playerProfile.setTextures(data.getNewTexture());
            playerProfile.setProperty(new ProfileProperty("textures", data.getTextureValue(), data.getTextureKey()));
            playerProfile.getTextures().setCape(data.getOrgTexture().getCape());
            // apply skin
            p.setPlayerProfile(playerProfile);
        } else {
            //add to download Queue
            getQueue().add(p);
        }
    }

    private void downloadAndApplySkin(Player p, String final_name, File target, PlayerProfile playerProfile) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (players.get(p.getUniqueId()).getOrgTexture() != null
                        || players.get(p.getUniqueId()).getNewTexture() != null) {
                    try {
                        String path = plugin.getDataFolder() + "/skin/";
                        File source_png = new File(path + p.getName() + ".png");
                        File final_png = new File(path + final_name + ".png");

                        MineskinClient client = new MineskinClient("MyUserAgent");

                        // check if the head exists should be always false
                        if (!source_png.exists())
                            Utils.downloadFile(p.getPlayerProfile().getTextures().getSkin(), path, p.getName());

                        // copy pixels from the head to the wanted skin
                        Utils.transferHead(source_png, target, final_name);

                        // generate texture signature and key for skin
                        client.generateUpload(final_png, SkinOptions.name(String.valueOf(p.getUniqueId()))).thenAccept(skin -> {
                            playerProfile.getTextures().setSkin(null, PlayerTextures.SkinModel.CLASSIC);

                            // put the values to the object
                            players.get(p.getUniqueId()).setTextureKey(skin.data.texture.signature);
                            players.get(p.getUniqueId()).setTextureValue(skin.data.texture.value);

                            //put the textures to the player-profile
                            playerProfile.setProperty(new ProfileProperty("textures", skin.data.texture.value, skin.data.texture.signature));

                            // delete the skins to save space
                            source_png.delete();
                            final_png.delete();
                        });
                        // save the group to the object
                        players.get(p.getUniqueId()).setGroup(perms.getPrimaryGroup(p));
                        // set the new texture
                        players.get(p.getUniqueId()).setNewTexture(playerProfile.getTextures());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        runnable.runTaskAsynchronously(this);
        // make delayed task for the save function to be sure that everything is already saved to the object.
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            p.setPlayerProfile(playerProfile);
            players.get(p.getUniqueId()).savetoDataContainer();
        }, 150);
    }

    public File getSourceFile(Permission perms, Player p) {
        String source;

        String path = String.valueOf(Main.getPlugin().getDataFolder());

        source = getConfig().getString("skins." + perms.getPrimaryGroup(p));

        if (Objects.equals(source, ""))
            source = "default.png";

        return new File(path + "/" +source);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

}
