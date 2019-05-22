package JProjects.BaseInfoBot.database.moe;

import java.awt.Color;

public enum MoeSuitType {
	ASSAULT, SUPPORT, BOMBARDIER, SNIPER;

	// US => US+1 STATS x4
	// US => US+2 STATS x5
	// US+1 => US+2 STATS x5/4

	public static MoeSuitType fromString(String s) {
		if (s == null)
			return null;
		if (s.equalsIgnoreCase("assault") || s.equalsIgnoreCase("assaults") || s.equalsIgnoreCase("ass"))
			return ASSAULT;
		if (s.equalsIgnoreCase("support") || s.equalsIgnoreCase("supports") || s.equalsIgnoreCase("sup")
				|| s.equalsIgnoreCase("supp"))
			return SUPPORT;
		if (s.equalsIgnoreCase("bombardier") || s.equalsIgnoreCase("bombardiers") || s.equalsIgnoreCase("bomber")
				|| s.equalsIgnoreCase("bombers") || s.equals("bomb"))
			return BOMBARDIER;
		if (s.equalsIgnoreCase("sniper") || s.equalsIgnoreCase("snipers") || s.equalsIgnoreCase("sni")
				|| s.equalsIgnoreCase("snip"))
			return SNIPER;
		else
			return null;
	}

	public String toString() {
		switch (this) {
		case ASSAULT:
			return "Assault";
		case SUPPORT:
			return "Support";
		case BOMBARDIER:
			return "Bombardier";
		case SNIPER:
			return "Sniper";
		default:
			return null;
		}
	}

	public int getMove() {
		switch (this) {
		case ASSAULT:
			return 5;
		case SUPPORT:
			return 4;
		case BOMBARDIER:
			return 3;
		case SNIPER:
			return 3;
		default:
			return 0;
		}
	}

	// For consistency, this is same as TkON's
	// Assault / Support / Bombardier / Sniper
	// #d80f0f, #0cf9ea, #d67608, #fffa00

	public Color getColor() {
		switch (this) {
		case ASSAULT:
			return new Color(216, 15, 15);
		case SUPPORT:
			return new Color(12, 249, 234);
		case BOMBARDIER:
			return new Color(214, 118, 8);
		case SNIPER:
			return new Color(255, 250, 0);
		default:
			return new Color(0, 0, 0);
		}
	}
}
