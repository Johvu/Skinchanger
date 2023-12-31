package fi.johvu.skinchanger;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

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

        System.out.println("§aFild downloaded succesfully.");
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
