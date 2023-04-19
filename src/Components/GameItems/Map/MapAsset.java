package Components.GameItems.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class is a simple BufferedImage with some dimensions.
 * Its purpose is in Game map class.
 */
public class MapAsset {
    private final BufferedImage image;
    private final int width;
    private final int height;
    public MapAsset(String path , int width , int height) throws IOException {
        this.width= width;
        this.height = height;

        image = ImageIO.read(new File(path));
    }

    public int getWidth(){
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getImage(){
        return image;
    }
}