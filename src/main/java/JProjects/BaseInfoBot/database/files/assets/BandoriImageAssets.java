package JProjects.BaseInfoBot.database.files.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BandoriImageAssets {

	public static final String PREFIX_PATH = "./assets/";

	public static final String BACKGROUND_MULTI_ROOM = "BackgroundMultiRoom.png";
	public static final String BACKGROUND_NO_USER = "BackgroundNoUser.png";

	public static final String CARD_FRAME_RAINBOW = "CardFrameRainbow.png";
	public static final String CARD_FRAME_GOLD = "CardFrameGold.png";
	public static final String CARD_FRAME_SILVER = "CardFrameSilver.png";
	public static final String CARD_BACKGROUND = "CardBackground.png";

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

	public static final String MICHELLE_ERROR = "MichelleError.png";

	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(new File(PREFIX_PATH + name));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
