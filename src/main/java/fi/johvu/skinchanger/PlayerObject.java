package fi.motimaa.johvu;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;


public class PlayerObject {

    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    private PlayerTextures orgTexture;
    @Getter
    @Setter
    private String textureKey;
    @Getter
    @Setter
    private String textureValue;

    @Getter
    @Setter
    private String group;

    @Getter
    @Setter
    private PlayerTextures newTexture;
    @Getter
    private NamespacedKey Namespacedkey = new NamespacedKey(Main.getPlugin(), "PrisonSkin");

    public PlayerObject(UUID uuid, PlayerTextures orgTexture, PlayerTextures newTexture, String textureKey, String textureValue, String group) {
        this.uuid = uuid;
        this.orgTexture = orgTexture;
        this.newTexture = newTexture;
        this.textureKey = textureKey;
        this.textureValue = textureValue;
        this.group = group;
    }

    public void SavetoDataContainer() {
        Bukkit.getPlayer(uuid).getPersistentDataContainer().set(Namespacedkey, PersistentDataType.STRING, toString());
    }

    public String LoadFromDataContainer() {
        try {
            String[] arr = Bukkit.getPlayer(uuid).getPersistentDataContainer().get(Namespacedkey, PersistentDataType.STRING).split("@@");
            this.textureKey = arr[1];
            this.textureValue = arr[2];
            this.orgTexture.setSkin(new URL(arr[3]));
            this.group = arr[4];
            return arr[4];
        } catch (NullPointerException | MalformedURLException ex) {
            return null;
        }
    }

    public String[] getDataContainer() {
        try {
            String[] arr = Bukkit.getPlayer(uuid).getPersistentDataContainer().get(Namespacedkey, PersistentDataType.STRING).split("@@");
            return arr;
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return newTexture.getSkin() + "@@" + textureKey + "@@" + textureValue + "@@" + orgTexture.getSkin() + "@@" + group;
    }

}
