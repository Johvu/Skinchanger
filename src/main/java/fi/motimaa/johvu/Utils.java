package fi.motimaa.johvu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.logging.Level;
import javax.imageio.ImageIO;

public class Utils {

    public static void downloadFile(URL url, String saveDir, String name) throws IOException {
        URLConnection connection = url.openConnection();
        connection.connect();

        // Obtain the input stream
        InputStream inputStream = connection.getInputStream();

        // Create the output file

        // Create the output stream
        FileOutputStream outputStream = new FileOutputStream(saveDir + name + ".png");

        // Read from the input stream and write to the output stream
        byte[] buffer = new byte[1024];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        System.out.println("§aTiedosto ladattu onnistuneesti.");
    }

    public static File getSourceFile(Permission perms, Player p) {

        String source;

        String[] sources = {"source1.png", "source2.png"};

        String path = String.valueOf(Main.getPlugin().getDataFolder());

        Random ran = new Random();

        source = switch (perms.getPrimaryGroup(p)) {
            default -> sources[ran.nextInt(sources.length)];
            case "premium", "premium+", "healper" -> "premium.png";
            case "motiking", "cteam", "chelper", "cmanager" -> "motiking.png";
            case "mod", "ultimate" -> "ultimate.png";
            case "build" -> "build.png";
            case "admin" -> "admin.png";
            case "owner" -> "owner.png";
        };
        return new File(path + "/" +source);
    }

    public static void transferHead(File sourceSkinPath, File targetSkinPath, String name) throws IOException {
        BufferedImage source = ImageIO.read(sourceSkinPath);
        BufferedImage sourceSkin = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        sourceSkin.getGraphics().drawImage(source, 0, 0, null);

        BufferedImage target = ImageIO.read(targetSkinPath);
        BufferedImage targetSkin = new BufferedImage(target.getWidth(), target.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        targetSkin.getGraphics().drawImage(target, 0, 0, null);

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 16; y++) {
                int pixel = sourceSkin.getRGB(x, y);
                targetSkin.setRGB(x, y, pixel);
            }
        }

        ImageIO.write(targetSkin, "PNG", new File(sourceSkinPath.getParent() + "/" + name + ".png"));
    }
}
