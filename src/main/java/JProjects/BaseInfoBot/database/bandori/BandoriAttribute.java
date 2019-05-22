package JProjects.BaseInfoBot.database.bandori;

import JProjects.BaseInfoBot.database.Emotes;

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
}
