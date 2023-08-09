package fi.johvu.skinchanger;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Data
public class PlayerSkin {

    private UUID uuid;
    private PlayerTextures orgTexture;
    private String textureKey;
    private String textureValue;
    private String group;
    private PlayerTextures newTexture;
    private NamespacedKey Namespacedkey = new NamespacedKey(Main.getPlugin(), "PrisonSkin");

    public PlayerSkin(UUID uuid, PlayerTextures orgTexture, PlayerTextures newTexture, String textureKey, String textureValue, String group) {
        this.uuid = uuid;
        this.orgTexture = orgTexture;
        this.newTexture = newTexture;
        this.textureKey = textureKey;
        this.textureValue = textureValue;
        this.group = group;
    }

    public void savetoDataContainer() {
        Bukkit.getPlayer(uuid).getPersistentDataContainer().set(Namespacedkey, PersistentDataType.STRING, toString());
    }

    public String loadFromDataContainer() {
        try {
            String[] arr = getDataContainer();
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
            return Bukkit.getPlayer(uuid).getPersistentDataContainer().get(Namespacedkey, PersistentDataType.STRING).split("@@");
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return newTexture.getSkin() + "@@" + textureKey + "@@" + textureValue + "@@" + orgTexture.getSkin() + "@@" + group;
    }

}
