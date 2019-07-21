package JProjects.BaseInfoBot.database.moe;

import java.util.ArrayList;
import java.util.HashMap;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.database.BotConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.TableMaker;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class MoeSuit implements Cloneable {
	private String owner;
	private String ownerId;
	private MoeSuitType suitType;
	private String name;
	private MoeGrade grade;
	private int level;
	private double exp;

	// Statistics
	private int hp;
	private final int mp = 100; // NOT INCLUDED IN TABLE
	private int attack;
	private int defense;
	private int move; // NOT INCLUDED IN TABLE
	private int accuracy;
	private int evasion;
	private double counterChance;

	private double criticalChance;
	private double criticalDamage;
	private double stunResistance;
	private double freezeResistance;
	private double silenceResistance;
	private double acidResistance;

	public MoeSuit(String owner, String ownerId, MoeSuitType suitType, String name, MoeGrade grade, int level, int hp,
			int attack, int defense, int move, int accuracy, int evasion, double counterChance, double criticalChance,
			double criticalDamage, double stunResistance, double freezeResistance, double silenceResistance,
			double acidResistance) {
		this.owner = owner;
		this.ownerId = ownerId;
		this.suitType = suitType;
		this.name = name;
		this.grade = grade;
		this.level = level;
		this.exp = 0D;
		this.hp = hp;
		this.attack = attack;
		this.defense = defense;
		this.move = move;
		this.accuracy = accuracy;
		this.evasion = evasion;
		this.counterChance = counterChance;
		this.criticalChance = criticalChance;
		this.criticalDamage = criticalDamage;
		this.stunResistance = stunResistance;
		this.freezeResistance = freezeResistance;
		this.silenceResistance = silenceResistance;
		this.acidResistance = acidResistance;
	}

	public MoeSuit(String owner, String ownerId, HashMap<String, String> info)
			throws ClassCastException, NullPointerException {
		this.owner = owner.replace("_", " ");
		this.ownerId = ownerId;
		this.suitType = MoeSuitType.fromString(info.get("type"));
		this.name = rebuildNameReadable(info.get("name"));
		this.grade = MoeGrade.fromString(info.get("grade"));
		this.level = (Integer) GeneralTools.getNumeric(info.get("level"));

		this.exp = (Double) GeneralTools.getNumeric(info.get("exp"));
		if (this.exp == -1)
			this.exp = 0;

		this.hp = (Integer) GeneralTools.getNumeric(info.get("hp"));
		this.attack = (Integer) GeneralTools.getNumeric(info.get("atk"));
		this.defense = (Integer) GeneralTools.getNumeric(info.get("def"));
		this.move = this.suitType.getMove();
		this.accuracy = (Integer) GeneralTools.getNumeric(info.get("acc"));
		this.evasion = (Integer) GeneralTools.getNumeric(info.get("eva"));
		this.counterChance = (Double) GeneralTools.getNumeric(info.get("cntr"), true);
		this.criticalChance = (Double) GeneralTools.getNumeric(info.get("crt%"), true);
		this.criticalDamage = (Double) GeneralTools.getNumeric(info.get("crit"), true);
		this.stunResistance = (Double) GeneralTools.getNumeric(info.get("stn"), true);
		this.freezeResistance = (Double) GeneralTools.getNumeric(info.get("frz"), true);
		this.silenceResistance = (Double) GeneralTools.getNumeric(info.get("sil"), true);
		this.acidResistance = (Double) GeneralTools.getNumeric(info.get("acd"), true);
	}

	/**
	 * Leveling Functions: US => +1 x4 => +2 x5 => +3 x6 <br>
	 * Assuming: +4 = x7 and linear distribution
	 */
	public void levelChange(int target) {
		double multiplier = 1D;
		if (this.level == 1) {
			if (target == 31)
				multiplier = 4D;
			else if (target == 41)
				multiplier = 5D;
			else if (target == 51)
				multiplier = 6D;
		} else if (this.level == 31) {
			if (target == 1)
				multiplier = 0.25D;
			else if (target == 41)
				multiplier = 1.25D;
			else if (target == 51)
				multiplier = 1.5D;
		} else if (this.level == 41) {
			if (target == 1)
				multiplier = 0.2;
			else if (target == 31)
				multiplier = 0.8D;
			else if (target == 51)
				multiplier = 1.2D;
		} else if (this.level == 51) {
			if (target == 1)
				multiplier = 1D / 6D;
			else if (target == 31)
				multiplier = 2D / 3D;
			else if (target == 41)
				multiplier = 5D / 6D;
		}
		if (multiplier == 1)
			return;
		this.level = target;
		this.statsLevelChange(multiplier);
	}

	private void statsLevelChange(double multiplier) {
		this.hp *= multiplier;
		this.attack *= multiplier;
		this.defense *= multiplier;
		this.accuracy *= multiplier;
		this.evasion *= multiplier;
	}

	public void gainExp(int exp) {
		// TODO
	}

	/*
	 * Custom Functions
	 */
	public MessageEmbed toEmbededMessage(boolean newData, String imgUrl) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.suitType.getColor());
		builder.setTitle(this.owner + "'s " + this.name);
		builder.setDescription("Type: " + this.suitType);

		double expPercentage = Math.round(this.level * 100D / this.grade.getMaxLevel());
		int hashCount = (int) (expPercentage * 40D / 100D);
		int dashCount = 40 - hashCount;
		StringBuilder xpString = new StringBuilder("[");
		for (int a = 0; a < hashCount; a++)
			xpString.append("#");
		for (int a = 0; a < dashCount; a++)
			xpString.append("-");
		xpString.append("]");
		Field exp = new Field("**Grade: " + this.grade.toString(this.level) + "**",
				"```Lv." + this.level + " " + xpString.toString() + " " + Math.round(expPercentage) + "%```", false);
		builder.addField(exp);

		builder.addField(new Field("**Statistics:**", this.makeDefaultTable(), false));
		if (newData)
			builder.setFooter("Data saved", imgUrl);
		return builder.build();
	}

	public MessageEmbed toFullStatsEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.suitType.getColor());
		builder.setTitle(this.owner + "'s " + this.name);
		builder.setDescription("Type: " + this.suitType);

		this.levelChange(1);
		builder.addField(new Field("**Statistics Lv.1:**", this.makeDefaultTable(), false));
		this.levelChange(31);
		builder.addField(new Field("**Statistics Lv.31:**", this.makeDefaultTable(), false));
		this.levelChange(41);
		builder.addField(new Field("**Statistics Lv.41:**", this.makeDefaultTable(), false));
		this.levelChange(51);
		builder.addField(new Field("**Statistics Lv.51:**", this.makeDefaultTable(), false));
		return builder.build();
	}

	public String makeDefaultTable() {
		ArrayList<HashMap<String, Number>> data = getLeftRightDisplayMaps();
		return TableMaker.makeDefaultTable(data.get(0), data.get(1));
	}

	public String makeComparisonTable(MoeSuit other) {
		ArrayList<HashMap<String, Number>> data = getLeftRightDisplayMaps();
		ArrayList<HashMap<String, Number>> dataOther = other.getLeftRightDisplayMaps();
		return TableMaker.makeComparisonTable(data, dataOther);
	}

	public double getExpectedDamage() {
		return this.attack * (this.criticalDamage / 100D) * (this.criticalChance / 100D)
				+ this.attack * (1 - this.criticalChance / 100D);
	}

	public ArrayList<HashMap<String, Number>> getLeftRightDisplayMaps() {
		HashMap<String, Number> leftCol = new HashMap<String, Number>();
		HashMap<String, Number> rightCol = new HashMap<String, Number>();
		leftCol.put("HP", this.hp);
		leftCol.put("ATK", this.attack);
		leftCol.put("DEF", this.defense);
		leftCol.put("ACC", this.accuracy);
		leftCol.put("EVA", this.evasion);
		leftCol.put("CNTR", this.counterChance);
		rightCol.put("CRT%", this.criticalChance);
		rightCol.put("CRIT", this.criticalDamage);
		rightCol.put("STNΩ", this.stunResistance);
		rightCol.put("FRZΩ", this.freezeResistance);
		rightCol.put("SILΩ", this.silenceResistance);
		rightCol.put("ACDΩ", this.acidResistance);
		ArrayList<HashMap<String, Number>> data = new ArrayList<HashMap<String, Number>>();
		data.add(leftCol);
		data.add(rightCol);
		return data;
	}

	public static String rebuildName(String s) {
		StringBuilder rebuild = new StringBuilder();
		char[] chars = s.toCharArray();
		rebuild.append(Character.toUpperCase(chars[0]));
		boolean nextCaps = false;
		for (int a = 1; a < chars.length; a++) {
			if (chars[a] != '_' && chars[a] != '-') {
				if (nextCaps) {
					rebuild.append(Character.toUpperCase(chars[a]));
					nextCaps = false;
					continue;
				}
				rebuild.append(chars[a]);
				continue;
			}
			if (a + 1 > chars.length)
				continue;
			nextCaps = true;
			rebuild.append(chars[a]);
		}
		return rebuild.toString();
	}

	public static String rebuildNameReadable(String s) {
		return rebuildName(s).replace("_", " ");
	}

	public double getAspect(String aspect) {
		if (aspect.equals("hp"))
			return this.hp;
		if (aspect.equals("atk"))
			return this.attack;
		if (aspect.equals("def"))
			return this.defense;
		if (aspect.equals("acc"))
			return this.accuracy;
		if (aspect.equals("eva"))
			return this.evasion;
		if (aspect.equals("cntr"))
			return this.counterChance;
		if (aspect.equals("crt%"))
			return this.criticalChance;
		if (aspect.equals("crit"))
			return this.criticalDamage;
		if (aspect.equals("acd"))
			return this.acidResistance;
		if (aspect.equals("stn"))
			return this.stunResistance;
		if (aspect.equals("frz"))
			return this.freezeResistance;
		if (aspect.equals("sil"))
			return this.silenceResistance;
		return 0;
	}

	public String getDisplayName() {
		return rebuildName(name).replace("_", " ");
	}

	/*
	 * Data Storage Functions
	 */
	public static MoeSuit fromString(String suitString) throws NullPointerException, ClassCastException {
		String[] map = suitString.split(":=>:");
		HashMap<String, String> dataMap = new HashMap<String, String>();
		String[] dataArr = map[1].split(" ");
		for (int a = 0; a < dataArr.length; a++) {
			String[] data = dataArr[a].split("=");
			dataMap.put(data[0], data[1].replace("_", " "));
		}
		dataMap.put("owner", App.bot.getJDA().getUserById(map[0].replace(BotConfig.PREFIX + "hangar import ", ""))
				.getName().replace(" ", "_"));
		return new MoeSuit(dataMap.get("owner"), map[0], dataMap);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.ownerId + ":=>:");
		HashMap<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("type", this.suitType.toString());
		infoMap.put("name", this.name.replace(" ", "_"));
		infoMap.put("grade", this.grade.toString());
		infoMap.put("level", this.level + "");
		infoMap.put("hp", this.hp + "");
		infoMap.put("atk", this.attack + "");
		infoMap.put("def", this.defense + "");
		infoMap.put("acc", this.accuracy + "");
		infoMap.put("eva", this.evasion + "");
		infoMap.put("cntr", this.counterChance + "");
		infoMap.put("crt%", this.criticalChance + "");
		infoMap.put("crit", this.criticalDamage + "");
		infoMap.put("stn", this.stunResistance + "");
		infoMap.put("frz", this.freezeResistance + "");
		infoMap.put("sil", this.silenceResistance + "");
		infoMap.put("acd", this.acidResistance + "");

		for (String key : infoMap.keySet())
			sb.append(key + "=" + infoMap.get(key) + " ");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public void changeOwner(User owner) {
		this.owner = owner.getName();
		this.ownerId = owner.getId();
	}

	public MoeSuit clone() throws CloneNotSupportedException {
		return (MoeSuit) super.clone();
	}

	/*
	 * Setters and Getters
	 */
	public String getOwner() {
		return this.owner;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	public MoeSuitType getSuitType() {
		return suitType;
	}

	public String getName() {
		return name;
	}

	public String getCodeName() {
		return this.getName().replace(" ", "_");
	}

	public MoeGrade getGrade() {
		return grade;
	}

	public int getLevel() {
		return level;
	}

	public double getExp() {
		return exp;
	}

	public int getHP() {
		return hp;
	}

	public int getMP() {
		return mp;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public int getMove() {
		return move;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public int getEvasion() {
		return evasion;
	}

	public double getCounterChance() {
		return counterChance;
	}

	public double getCriticalChance() {
		return criticalChance;
	}

	public double getCriticalDamage() {
		return criticalDamage;
	}

	public double getStunResistance() {
		return stunResistance;
	}

	public double getFreezeResistance() {
		return freezeResistance;
	}

	public double getSilenceResistance() {
		return silenceResistance;
	}

	public double getAcidResistance() {
		return acidResistance;
	}

	/*
	 * Setters
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setSuitType(MoeSuitType suitType) {
		this.suitType = suitType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGrade(MoeGrade grade) {
		this.grade = grade;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setExp(double exp) {
		this.exp = exp;
	}

	public void setHP(int hp) {
		this.hp = hp;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public void setEvasion(int evasion) {
		this.evasion = evasion;
	}

	public void setCounterChance(double counterChance) {
		this.counterChance = counterChance;
	}

	public void setCriticalChance(double criticalChance) {
		this.criticalChance = criticalChance;
	}

	public void setCriticalDamage(double criticalDamage) {
		this.criticalDamage = criticalDamage;
	}

	public void setStunResistance(double stunResistance) {
		this.stunResistance = stunResistance;
	}

	public void setFreezeResistance(double freezeResistance) {
		this.freezeResistance = freezeResistance;
	}

	public void setSilenceResistance(double silenceResistance) {
		this.silenceResistance = silenceResistance;
	}

	public void setAcidResistance(double acidResistance) {
		this.acidResistance = acidResistance;
	}

}
