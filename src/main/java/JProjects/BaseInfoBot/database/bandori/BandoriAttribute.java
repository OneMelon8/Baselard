package JProjects.BaseInfoBot.database.bandori;

import java.awt.image.BufferedImage;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.files.assets.BandoriImageAssets;

public enum BandoriAttribute {
	PURE, HAPPY, COOL, POWER;

	public static BandoriAttribute fromString(String attr) {
		attr = attr.toLowerCase();
		if (attr.equals("pure"))
			return PURE;
		else if (attr.equals("power"))
			return POWER;
		else if (attr.equals("happy"))
			return HAPPY;
		else if (attr.equals("cool"))
			return COOL;
		return null;
	}

	public static BandoriAttribute fromEmote(String emote) {
		return fromString(Emotes.getName(emote).replace("attr_", ""));
	}

	public String getDisplayName() {
		switch (this) {
		case PURE:
			return "Pure";
		case HAPPY:
			return "Happy";
		case COOL:
			return "Cool";
		case POWER:
			return "Powerful";
		default:
			return null;
		}
	}

	public String getEmote() {
		switch (this) {
		case PURE:
			return Emotes.ATTR_PURE;
		case HAPPY:
			return Emotes.ATTR_HAPPY;
		case COOL:
			return Emotes.ATTR_COOL;
		case POWER:
			return Emotes.ATTR_POWER;
		default:
			return null;
		}
	}

	public static BandoriAttribute fromIndex(int index) {
		while (index > 4)
			index -= 4;
		switch (index) {
		case 2:
			return COOL;
		case 4:
			return HAPPY;
		case 3:
			return PURE;
		case 1:
			return POWER;
		default:
			return null;
		}
	}

	public int getIndex() {
		switch (this) {
		case COOL:
			return 2;
		case HAPPY:
			return 4;
		case PURE:
			return 3;
		case POWER:
			return 1;
		default:
			return 1;
		}
	}

	public BufferedImage getBufferedImage() {
		String name = null;
		switch (this) {
		case COOL:
			name = BandoriImageAssets.ATTR_COOL;
			break;
		case HAPPY:
			name = BandoriImageAssets.ATTR_HAPPY;
			break;
		case PURE:
			name = BandoriImageAssets.ATTR_PURE;
			break;
		case POWER:
			name = BandoriImageAssets.ATTR_POWERFUL;
			break;
		}
		return BandoriImageAssets.getImage(name);
	}
}
