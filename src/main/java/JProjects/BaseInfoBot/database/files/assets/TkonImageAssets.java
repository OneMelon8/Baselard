package JProjects.BaseInfoBot.database.files.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TkonImageAssets {

	public static final String PREFIX_PATH = "./assets/tkon/";
	public static final String PREFIX_ITEMS = "items/";
	public static final String PREFIX_ITEMS_CRYSTALS = "items/crystals/";

	// PATH: GENERAL
	public static final String ITEM_BACKGROUND = "ItemBackground.png";
	public static final String ITEM_FRAME_SILVER = "ItemFrameSilver.png";
	public static final String ITEM_FRAME_GOLD = "ItemFrameGold.png";
	public static final String ITEM_FRAME_RAINBOW = "ItemFrameRainbow.png";

	public static final String INVENTORY_BACKGROUND = "InventoryBackground.png";
	public static final String INVENTORY_SELECTED = "InventorySelected.png";
	public static final String INVENTORY_SLOT = "InventorySlot.png";

	// PATH: ITEMS
	public static final String ITEMS_FOOD = PREFIX_ITEMS + "Food.png";
	public static final String ITEMS_ROSE = PREFIX_ITEMS + "Rose.png";

	public static final String ITEMS_BOX_BLUE = PREFIX_ITEMS + "BoxBlue.png";
	public static final String ITEMS_BOX_GOLD = PREFIX_ITEMS + "BoxGold.png";
	public static final String ITEMS_BOX_PINK = PREFIX_ITEMS + "BoxPink.png";

	// PATH: ITEMS >> CRYSTALS
	public static final String CRYSTAL_1 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal1.png";
	public static final String CRYSTAL_2 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal2.png";
	public static final String CRYSTAL_3 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal3.png";
	public static final String CRYSTAL_4 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal4.png";
	public static final String CRYSTAL_5 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal5.png";
	public static final String CRYSTAL_6 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal6.png";
	public static final String CRYSTAL_7 = PREFIX_ITEMS + PREFIX_ITEMS_CRYSTALS + "Crystal7.png";
	public static final String[] CRYSTALS = new String[] { CRYSTAL_1, CRYSTAL_2, CRYSTAL_3, CRYSTAL_4, CRYSTAL_5,
			CRYSTAL_6, CRYSTAL_7 };

	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(new File(PREFIX_PATH + name));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
