package JProjects.BaseInfoBot.database.files.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageAssets {

	public static final String PREFIX_PATH = "./assets/";

	public static final String BACKGROUND_MULTI_ROOM = "BackgroundMultiRoom.png";
	public static final String BACKGROUND_CARD = "BackgroundCard.png";

	public static final String RAINBOW_FRAME = "RainbowFrame.png";
	public static final String GOLD_FRAME = "GoldFrame.png";
	public static final String SILVER_FRAME = "SilverFrame.png";

	public static final String RAINBOW_CARD_FRAME = "RainbowCardFrame.png";
	public static final String GOLD_CARD_FRAME = "GoldCardFrame.png";
	public static final String SILVER_CARD_FRAME = "SilverCardFrame.png";

	public static final String LOGO_POPPIN_PARTY = "LogoPoppinParty.png";
	public static final String LOGO_AFTERGLOW = "LogoAfterglow.png";
	public static final String LOGO_PASTEL_PALETTES = "LogoPastelPalettes.png";
	public static final String LOGO_ROSELIA = "LogoRoselia.png";
	public static final String LOGO_HELLO_HAPPY_WORLD = "LogoHelloHappyWorld.png";
	public static final String LOGO_RAISE_A_SUILEN = "LogoRaiseASuilen.png";

	public static final String ATTR_PURE = "AttrPure.png";
	public static final String ATTR_POWERFUL = "AttrPowerful.png";
	public static final String ATTR_COOL = "AttrCool.png";
	public static final String ATTR_HAPPY = "AttrHappy.png";

	public static final String BANDORI_STAR = "BandoriStar.png";
	public static final String BANDORI_STAR_PREMIUM = "BandoriStarPremium.png";

	public static BufferedImage getImage(String name) throws IOException {
		return ImageIO.read(new File(PREFIX_PATH + name));
	}

}
