package JProjects.BaseInfoBot.database.files.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageAssets {

	public static final String PREFIX_PATH = "./assets/";

	public static final String PATH_ICONS = "icons/";

	public static final String MULTI_ROOM_BACKGROUND = "MultiRoomBackground.png";

	public static final String RAINBOW_FRAME = "RainbowFrame.png";
	public static final String GOLD_FRAME = "GoldFrame.png";
	public static final String SILVER_FRAME = "SilverFrame.png";

	public static final String RAINBOW_CARD_FRAME = "RainbowCardFrame.png";
	public static final String GOLD_CARD_FRAME = "GoldCardFrame.png";
	public static final String SILVER_CARD_FRAME = "SilverCardFrame.png";

	public static final String LOGO_POPPIN_PARTY = PATH_ICONS + "LogoPoppinParty.png";
	public static final String LOGO_AFTERGLOW = PATH_ICONS + "LogoAfterglow.png";
	public static final String LOGO_PASTEL_PALETTES = PATH_ICONS + "LogoPastelPalettes.png";
	public static final String LOGO_ROSELIA = PATH_ICONS + "LogoRoselia.png";
	public static final String LOGO_HELLO_HAPPY_WORLD = PATH_ICONS + "LogoHelloHappyWorld.png";

	public static final String ATTR_PURE = PATH_ICONS + "AttrPure.png";
	public static final String ATTR_POWERFUL = PATH_ICONS + "AttrPowerful.png";
	public static final String ATTR_COOL = PATH_ICONS + "AttrCool.png";
	public static final String ATTR_HAPPY = PATH_ICONS + "AttrHappy.png";

	public static final String BANDORI_STAR = PATH_ICONS + "BandoriStar.png";
	public static final String BANDORI_STAR_PREMIUM = PATH_ICONS + "BandoriStarPremium.png";

	public static BufferedImage getImage(String name) throws IOException {
		return ImageIO.read(new File(PREFIX_PATH + name));
	}

}
