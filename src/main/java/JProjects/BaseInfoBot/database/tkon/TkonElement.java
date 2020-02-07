package JProjects.BaseInfoBot.database.tkon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

import JProjects.BaseInfoBot.database.Emotes;

public enum TkonElement {

	// Major elements
	GRASS, WATER, FIRE,

	// Grass type mutations
	EARTH, SPIRIT, POISON,

	// Water type mutations
	ICE, AIR, STORM,

	// Fire type mutations
	DRAGON, LAVA, PLASMA;

	private static HashMap<TkonElement, HashMap<TkonElement, Double>> damageValues = new HashMap<TkonElement, HashMap<TkonElement, Double>>();

	private static void createMap(HashMap<TkonElement, Double> map, TkonElement[] key, double[] val) {
		int min = Math.min(key.length, val.length);
		for (int a = 0; a < min; a++)
			map.put(key[a], val[a]);
	}

	public static double getDamageScaler(TkonElement source, TkonElement target) {
		if (damageValues == null || damageValues.isEmpty())
			init();
		return damageValues.get(source).get(target);
	}

	public boolean isRootElement() {
		switch (this) {
		case GRASS:
		case WATER:
		case FIRE:
			return true;
		default:
			return false;
		}
	}

	public TkonElement getRandomLeafElement() {
		Random r = new Random();
		TkonElement[] leafElements = null;
		switch (this) {
		case GRASS:
			leafElements = new TkonElement[] { EARTH, SPIRIT, POISON };
			break;
		case WATER:
			leafElements = new TkonElement[] { ICE, AIR, STORM };
			break;
		case FIRE:
			leafElements = new TkonElement[] { DRAGON, LAVA, PLASMA };
			break;
		default:
			return null;
		}

		return leafElements[r.nextInt(leafElements.length)];
	}

	public TkonElement getRootElement() {
		if (this.isRootElement())
			return this;
		switch (this) {
		case EARTH:
		case SPIRIT:
		case POISON:
			return GRASS;
		case ICE:
		case AIR:
		case STORM:
			return WATER;
		case DRAGON:
		case LAVA:
		case PLASMA:
			return FIRE;
		default:
			return this;
		}
	}

	public TkonElement[] getRelatedElements() {
		switch (this) {
		case GRASS:
		case EARTH:
		case SPIRIT:
		case POISON:
			return new TkonElement[] { GRASS, EARTH, SPIRIT, POISON };
		case WATER:
		case ICE:
		case AIR:
		case STORM:
			return new TkonElement[] { WATER, ICE, AIR, STORM };
		case FIRE:
		case DRAGON:
		case LAVA:
		case PLASMA:
			return new TkonElement[] { FIRE, DRAGON, LAVA, PLASMA };
		}
		return null;
	}

	public BufferedImage getImage() {
		return null;
	}

	public static TkonElement getRandomElement() {
		TkonElement[] values = TkonElement.values();
		return values[new Random().nextInt(values.length)];
	}

	public static TkonElement getRandomStartingElement() {
		int element = new Random().nextInt(3);
		switch (element) {
		case 0:
			return GRASS;
		case 1:
			return WATER;
		case 2:
			return FIRE;
		}
		return null;
	}

	public Color getColor() {
		switch (this) {
		case GRASS:
			return new Color(0, 255, 0);
		case WATER:
			return new Color(0, 0, 255);
		case FIRE:
			return new Color(255, 0, 0);
		case EARTH:
			return new Color(204, 102, 51);
		case SPIRIT:
			return new Color(170, 222, 233);
		case POISON:
			return new Color(153, 255, 102);
		case ICE:
			return new Color(0, 255, 255);
		case AIR:
			return new Color(177, 222, 225);
		case STORM:
			return new Color(88, 123, 127);
		case DRAGON:
			return new Color(247, 236, 31);
		case LAVA:
			return new Color(255, 84, 0);
		case PLASMA:
			return new Color(235, 102, 255);
		}
		return null;
	}

	public String getEmote() {
		switch (this) {
		case GRASS:
			return Emotes.POPPIN_PARTY;
		case WATER:
			return Emotes.POPPIN_PARTY;
		case FIRE:
			return Emotes.POPPIN_PARTY;
		case EARTH:
			return Emotes.POPPIN_PARTY;
		case SPIRIT:
			return Emotes.POPPIN_PARTY;
		case POISON:
			return Emotes.POPPIN_PARTY;
		case ICE:
			return Emotes.POPPIN_PARTY;
		case AIR:
			return Emotes.POPPIN_PARTY;
		case STORM:
			return Emotes.POPPIN_PARTY;
		case DRAGON:
			return Emotes.POPPIN_PARTY;
		case LAVA:
			return Emotes.POPPIN_PARTY;
		case PLASMA:
			return Emotes.POPPIN_PARTY;
		}
		return null;
	}

	public String getDisplayName() {
		if (!this.toString().contains("_"))
			return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
		String[] words = this.toString().split("_");
		StringBuilder sb = new StringBuilder();
		for (String word : words)
			sb.append(word.substring(0, 1).toUpperCase() + word.toLowerCase() + " ");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static void init() {
		TkonElement[] elements = new TkonElement[] { GRASS, WATER, FIRE, EARTH, SPIRIT, POISON, ICE, AIR, STORM, DRAGON,
				LAVA, PLASMA };
		HashMap<TkonElement, Double> dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 1, 2, 0.5, 0.75, 1, 1, 0.5, 1.5, 1, 0.75, 0.25, 1 });
		damageValues.put(GRASS, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 0.5, 1, 2, 1.5, 1, 0.75, 1.5, 1, 0.75, 0.5, 1.5, 1 });
		damageValues.put(WATER, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 2, 0.5, 1, 1.5, 1, 1, 0.5, 2, 0.75, 1, 1, 1 });
		damageValues.put(FIRE, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 0.5, 1.5, 0.75, 1, 1.5, 0.75, 0.5, 1.5, 1, 0.75, 1.5, 1 });
		damageValues.put(EARTH, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 1, 1.5, 1.5, 1.5, 1, 0.75, 0.75, 1, 0.75, 0.5, 1, 1.5 });
		damageValues.put(SPIRIT, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 1.5, 1.5, 0.5, 1.5, 0.5, 1, 0.75, 1.5, 0.5, 2, 0.5, 1 });
		damageValues.put(POISON, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 2, 1.5, 1.5, 0.75, 0.75, 0.5, 1, 1, 0.75, 1.5, 0.5, 1.5 });
		damageValues.put(ICE, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 0.75, 0.75, 0.5, 1, 1.5, 2, 1.5, 1, 0.75, 0.75, 1 });
		damageValues.put(AIR, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 1.5, 1.5, 1.5, 0.75, 0.5, 1.5, 0.75, 1.5, 1, 0.75, 0.75, 1 });
		damageValues.put(STORM, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		damageValues.put(DRAGON, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 2, 1.5, 0.75, 1.5, 0.5, 1, 1.5, 0.75, 1, 1, 1, 0.75 });
		damageValues.put(LAVA, dmgMap);

		dmgMap = new HashMap<TkonElement, Double>();
		createMap(dmgMap, elements, new double[] { 1.5, 0.75, 1, 1, 2, 1, 0.5, 0.75, 1, 1.5, 1, 1 });
		damageValues.put(PLASMA, dmgMap);
	}

}
