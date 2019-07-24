package JProjects.BaseInfoBot.database.files.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageAssets {
	public static final String PREFIX_PATH = "./assets/";

	public static final String GARUPA_BACKGROUND = "GarupaBackground.png";
	public static final String RAINBOW_FRAME = "RainbowFrame.png";
	public static final String GOLD_FRAME = "GoldFrame.png";
	public static final String SILVER_FRAME = "SilverFrame.png";

	public static BufferedImage getImage(String name) throws IOException {
		return ImageIO.read(new File(PREFIX_PATH + name));
	}

}
