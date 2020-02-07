package JProjects.BaseInfoBot.database.tkon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.database.config.TkonConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class TkonEntity {

	private static Random r = new Random();

	private String uuid;
	private String name;
	private String ownerId;

	private int level;
	private int exp;
	private int maxHp;
	private int maxMp;

	private double happiness;
	private double hunger;
	private double energy;
	private ArrayList<TkonSkill> skills;

	/*
	 * Combat
	 */
	private boolean isInCombat;
	private TkonElement element;
	private HashMap<TkonSkill, Integer> skillCooldowns;

	private int hp;
	private int mp;
	private int attack;
	private int defense;
	private int speed;
	private double criticalChance;
	private double criticalDamage;
	private int accuracy;
	private int evasion;
	private double resistance;

	/*
	 * Temporary variables (does not need to be stored in file)
	 */
	private int tempLevel;
	private int tempMaxHp;
	private int tempMaxMp;
	private int tempAttack;
	private int tempDefense;
	private int tempSpeed;
	private double tempCriticalChance;
	private double tempCriticalDamage;
	private int tempAccuracy;
	private int tempEvasion;
	private double tempResistance;

	/**
	 * Starting TkON
	 */
	public TkonEntity(String ownerId, String name) {
		this.ownerId = ownerId;
		this.uuid = UUID.randomUUID().toString();
		this.name = name;

		this.level = 1;
		this.exp = 0;
		this.maxHp = (int) (TkonConfig.STARTING_HP + randomByZero() * TkonConfig.STARTING_HP_RANDOMNESS);
		this.maxMp = (int) (TkonConfig.STARTING_MP + randomByZero() * TkonConfig.STARTING_MP_RANDOMNESS);

		this.happiness = 1.0;
		this.hunger = 1.0;
		this.energy = 1.0;
		this.skills = new ArrayList<TkonSkill>(); // Add skill later when element is known

		this.isInCombat = false;
		this.element = TkonElement.getRandomStartingElement();
		// TODO: UNCOMMENT!
		for (int a = 0; a <= r.nextInt(2); a++)
			this.addSkill(TkonSkillPool.getRandomSkill(this.getLevel(), this.getElement())); // Add 1-2 skills
		this.skillCooldowns = new HashMap<TkonSkill, Integer>();

		this.hp = this.maxHp;
		this.mp = this.maxMp;
		this.attack = (int) (TkonConfig.STARTING_ATTACK + randomByZero() * TkonConfig.STARTING_ATTACK_RANDOMNESS);
		this.defense = (int) (TkonConfig.STARTING_DEFENSE + randomByZero() * TkonConfig.STARTING_DEFENSE_RANDOMNESS);
		this.speed = (int) (TkonConfig.STARTING_SPEED + randomByZero() * TkonConfig.STARTING_SPEED_RANDOMNESS);
		this.criticalChance = TkonConfig.STARTING_CRITICAL_CHANCE
				+ randomByZero() * TkonConfig.STARTING_CRITICAL_CHANCE_RANDOMNESS;
		this.criticalDamage = TkonConfig.STARTING_CRITICAL_DAMAGE
				+ randomByZero() * TkonConfig.STARTING_CRITICAL_DAMAGE_RANDOMNESS;
		this.accuracy = (int) (TkonConfig.STARTING_ACCURACY + randomByZero() * TkonConfig.STARTING_ACCURACY_RANDOMNESS);
		this.evasion = (int) (TkonConfig.STARTING_EVASION + randomByZero() * TkonConfig.STARTING_EVASION_RANDOMNESS);
		this.resistance = TkonConfig.STARTING_RESISTANCE + randomByZero() * TkonConfig.STARTING_RESISTANCE_RANDOMNESS;

		// More??
	}

	/**
	 * Construct an instance from JSON string
	 */
	public TkonEntity(String jsonString) {
		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		this.uuid = obj.get("uuid").getAsString();
		this.name = obj.get("name").getAsString();
		this.ownerId = obj.get("owner_id").getAsString();

		this.level = obj.get("level").getAsInt();
		this.exp = obj.get("exp").getAsInt();
		this.maxHp = obj.get("max_hp").getAsInt();
		this.maxMp = obj.get("max_mp").getAsInt();

		this.happiness = obj.get("happiness").getAsDouble();
		this.hunger = obj.get("hunger").getAsDouble();
		this.energy = obj.get("energy").getAsDouble();
		this.skills = this.parseJsonSkillArray(obj.get("skills").getAsJsonArray());

		this.isInCombat = false;
		this.element = TkonElement.valueOf(obj.get("element").getAsString());
		this.skillCooldowns = new HashMap<TkonSkill, Integer>();

		this.hp = obj.get("hp").getAsInt();
		this.mp = obj.get("mp").getAsInt();
		this.attack = obj.get("attack").getAsInt();
		this.defense = obj.get("defense").getAsInt();
		this.speed = obj.get("speed").getAsInt();
		this.criticalChance = obj.get("critical_chance").getAsDouble();
		this.criticalDamage = obj.get("critical_damage").getAsDouble();
		this.accuracy = obj.get("accuracy").getAsInt();
		this.evasion = obj.get("evasion").getAsInt();
		this.resistance = obj.get("resistance").getAsDouble();

	}

	public MessageEmbed getEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElement().getColor());
		builder.setAuthor("Information of " + this.getName() + " (" + this.getOwner().getAsTag() + ")");
		builder.setDescription("Level " + this.getLevel() + " ・ EXP: " + this.getExp() + "/" + this.getExpCap() + " -- "
				+ GeneralTools.getPercentage(this.getExp(), this.getExpCap()));

		// TODO: uncomment
//		builder.setThumbnail(ImgbbSpider.uploadImage(this.getElement().getImage()));

		StringBuilder sb = new StringBuilder();
		sb.append("Owner: **" + this.getOwner().getAsTag() + "**");
		sb.append("\nElement: **" + this.getElement().getEmote() + " " + this.getElement().getDisplayName() + "**");
		sb.append("\nSkills: ```");
		boolean del = false;
		for (TkonSkill skill : this.getSkills()) {
			sb.append(skill.getName() + ", ");
			del = true;
		}
		if (del)
			sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("**Basic Info:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-10s %s", "Happiness:",
				GeneralTools.getBar(this.getHappiness(), 1, TkonConfig.BAR_LENGTH_INFO) + " "
						+ GeneralTools.getPercentage(this.getHappiness(), 1)));
		sb.append("\n" + String.format("%-10s %s", "Hunger:",
				GeneralTools.getBar(this.getHunger(), 1, TkonConfig.BAR_LENGTH_INFO) + " "
						+ GeneralTools.getPercentage(this.getHunger(), 1)));
		sb.append("\n" + String.format("%-10s %s", "Energy:",
				GeneralTools.getBar(this.getEnergy(), 1, TkonConfig.BAR_LENGTH_INFO) + " "
						+ GeneralTools.getPercentage(this.getEnergy(), 1)));
		sb.append("```");
		builder.addField(new Field("**Mood Info:**", sb.toString(), false));

		return builder.build();
	}

	public MessageEmbed getStatsEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElement().getColor());
		builder.setAuthor("Stats for " + this.getName() + " (" + this.getOwner().getAsTag() + ")");
		builder.setDescription("Level " + this.getLevel() + " ・ EXP: " + this.getExp() + "/" + this.getExpCap() + " -- "
				+ GeneralTools.getPercentage(this.getExp(), this.getExpCap()));

		StringBuilder sb = new StringBuilder();
		sb.append("Health: **" + this.getHp() + "**");
		sb.append("\nMana: **" + this.getMp() + "**");
		sb.append("\nAttack: **" + this.getAttack() + "**");
		sb.append("\nDefense: **" + this.getDefense() + "**");
		sb.append("\nSpeed: **" + this.getSpeed() + "**");
		builder.addField(new Field("**Basic Statistics:**", sb.toString(), true));

		sb = new StringBuilder();
		sb.append("C.Chance: **" + GeneralTools.getPercentage(this.getCriticalChance()) + "**");
		sb.append("\nC.Damage: **" + GeneralTools.getPercentage(this.getCriticalDamage()) + "**");
		sb.append("\nAccuracy: **" + this.getAccuracy() + "**");
		sb.append("\nEvasion: **" + this.getEvasion() + "**");
		sb.append("\nResistance: **" + GeneralTools.getPercentage(this.getResistance(), 2) + "**");
		builder.addField(new Field("**Advanced Statistics:**", sb.toString(), true));

		return builder.build();
	}

	public MessageEmbed getStatsComparedToStandardEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElement().getColor());
		builder.setAuthor("Stats for " + this.getName() + " (" + this.getOwner().getAsTag() + ")");
		builder.setDescription("Level " + this.getLevel() + " ・ EXP: " + this.getExp() + "/" + this.getExpCap() + " -- "
				+ GeneralTools.getPercentage(this.getExp(), this.getExpCap()));

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-8s %s %s", "Lv." + this.getLevel(),
				GeneralTools.getBar(this.getExp(), this.getExpCap(), TkonConfig.BAR_LENGTH_EXP),
				"Lv." + (this.getLevel() + 1)));
		sb.append("```");
		builder.addField(new Field("**Experience:**", sb.toString(), false));

		sb = new StringBuilder();
		sb.append("Health: **" + this.getHp() + GeneralTools.signNumberEmotes(this.getHp() - this.getStandardHp(), true)
				+ "**");
		sb.append("\nMana: **" + this.getMp() + GeneralTools.signNumberEmotes(this.getMp() - this.getStandardMp(), true)
				+ "**");
		sb.append("\nAttack: **" + this.getAttack()
				+ GeneralTools.signNumberEmotes(this.getAttack() - this.getStandardAttack(), true) + "**");
		sb.append("\nDefense: **" + this.getDefense()
				+ GeneralTools.signNumberEmotes(this.getDefense() - this.getStandardDefense(), true) + "**");
		sb.append("\nSpeed: **" + this.getSpeed()
				+ GeneralTools.signNumberEmotes(this.getSpeed() - this.getStandardSpeed(), true) + "**");
		builder.addField(new Field("**Basic Statistics:**", sb.toString(), true));

		sb = new StringBuilder();
		sb.append("C.Chance: **" + GeneralTools.getPercentage(this.getCriticalChance()) + " "
				+ GeneralTools.signNumberEmotesPercentage(
						GeneralTools.round(this.getCriticalChance() - this.getStandardCriticalChance(), 2), true)
				+ "**");
		sb.append("\nC.Damage: **" + GeneralTools.getPercentage(this.getCriticalDamage())
				+ GeneralTools.signNumberEmotesPercentage(
						GeneralTools.round(this.getCriticalDamage() - this.getStandardCriticalDamage(), 2), true)
				+ "**");
		sb.append("\nAccuracy: **" + this.getAccuracy()
				+ GeneralTools.signNumberEmotes(this.getAccuracy() - this.getStandardAccuracy(), true) + "**");
		sb.append("\nEvasion: **" + this.getEvasion()
				+ GeneralTools.signNumberEmotes(this.getEvasion() - this.getStandardEvasion(), true) + "**");
		sb.append(
				"\nResistance: **" + GeneralTools.getPercentage(this.getResistance(), 2)
						+ GeneralTools.signNumberEmotesPercentage(
								GeneralTools.round(this.getResistance() - this.getStandardResistance(), 2), true)
						+ "**");
		builder.addField(new Field("**Advanced Statistics:**", sb.toString(), true));

		return builder.build();
	}

	public MessageEmbed getLevelUpEmbeded(boolean detailed) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElement().getColor());
		builder.setAuthor(this.getName() + " has leveled up!");
		builder.setDescription("Level " + this.getLevel() + " ・ EXP: " + this.getExp() + "/" + this.getExpCap() + " -- "
				+ GeneralTools.getPercentage(this.getExp(), this.getExpCap()));

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-8s %s %s", "Lv." + this.getLevel(),
				GeneralTools.getBar(this.getExp(), this.getExpCap(), TkonConfig.BAR_LENGTH_EXP),
				"Lv." + (this.getLevel() + 1)));
		sb.append("```");
		builder.addField(new Field("**Experience:**", sb.toString(), false));

		sb = new StringBuilder();
		sb.append("Health: **" + this.tempMaxHp + " ➤ " + this.getHp()
				+ GeneralTools.signNumberEmotes(this.getHp() - this.tempMaxHp, detailed) + "**");
		sb.append("\nMana: **" + this.tempMaxMp + " ➤ " + this.getMp()
				+ GeneralTools.signNumberEmotes(this.getMp() - this.tempMaxMp, detailed) + "**");
		sb.append("\nAttack: **" + this.tempAttack + " ➤ " + this.getAttack()
				+ GeneralTools.signNumberEmotes(this.getAttack() - this.tempAttack, detailed) + "**");
		sb.append("\nDefense: **" + this.tempDefense + " ➤ " + this.getDefense()
				+ GeneralTools.signNumberEmotes(this.getDefense() - this.tempDefense, detailed) + "**");
		sb.append("\nSpeed: **" + this.tempSpeed + " ➤ " + this.getSpeed()
				+ GeneralTools.signNumberEmotes(this.getSpeed() - this.tempSpeed, detailed) + "**");
		builder.addField(new Field("**Basic Statistics:**", sb.toString(), true));

		sb = new StringBuilder();
		sb.append("C.Chance: **" + GeneralTools.getPercentage(this.tempCriticalChance) + " ➤ "
				+ GeneralTools.getPercentage(this.getCriticalChance())
				+ GeneralTools.signNumberEmotes(
						GeneralTools.round((this.getCriticalChance() - this.tempCriticalChance) * 100, 2), detailed)
				+ "**");
		sb.append("\nC.Damage: **" + GeneralTools.getPercentage(this.tempCriticalDamage) + " ➤ "
				+ GeneralTools.getPercentage(this.getCriticalDamage())
				+ GeneralTools.signNumberEmotes(
						GeneralTools.round((this.getCriticalDamage() - this.tempCriticalDamage) * 100, 2), detailed)
				+ "**");
		sb.append("\nAccuracy: **" + this.tempAccuracy + " ➤ " + this.getAccuracy()
				+ GeneralTools.signNumberEmotes(this.getAccuracy() - this.tempAccuracy, detailed) + "**");
		sb.append("\nEvasion: **" + this.tempEvasion + " ➤ " + this.getEvasion()
				+ GeneralTools.signNumberEmotes(this.getEvasion() - this.tempEvasion, detailed) + "**");
		sb.append(
				"\nResistance: **" + GeneralTools.getPercentage(this.tempResistance) + " ➤ "
						+ GeneralTools.getPercentage(this.getResistance())
						+ GeneralTools.signNumberEmotes(
								GeneralTools.round((this.getResistance() - this.tempResistance) * 100, 2), detailed)
						+ "**");
		builder.addField(new Field("**Advanced Statistics:**", sb.toString(), true));

		return builder.build();
	}

	public MessageEmbed getNewSkillEmbeded(TkonSkill newSkill) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElement().getColor());
		builder.setAuthor(this.getName() + " has learned a new skill!");
		builder.setDescription("Since there are less than " + TkonConfig.MAX_SKILLS
				+ " skills, it's automatically added into " + this.getName() + "'s arsenal");
		builder.addField(this.getSkillConciseField(newSkill, "New Skill"));
		return builder.build();
	}

	public MessageEmbed getNewSkillFullEmbeded(TkonSkill newSkill) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElement().getColor());
		builder.setAuthor(this.getName() + " can learn a new skill!");
		builder.setDescription("Choose which skill to replace using reactions");

		builder.addField(this.getSkillConciseField(newSkill, "New Skill"));

		int count = 1;
		for (TkonSkill skill : this.getSkills()) {
			builder.addField(this.getSkillConciseField(skill, "Current Skill #" + count));
			count++;
		}

		return builder.build();
	}

	private Field getSkillConciseField(TkonSkill skill, String fieldTitle) {
		StringBuilder sb = new StringBuilder();
		sb.append(skill.getElementsEmotesDisplay() + "・**" + skill.getDamageScalerDisplay() + "** ATK・**"
				+ skill.getMpCost() + "** MP・**" + skill.getRecycle() + "** CD・**" + skill.getRangeDisplay()
				+ "** tiles");
		sb.append("\nDescription: **" + skill.getDescription() + "**");
		if (skill.hasModifiers())
			sb.append("\nModifiers: " + skill.getModifiersDisplay());
		if (skill.hasInflictEffects())
			sb.append("\nInflict Effects: " + skill.getInflictEffectsDisplay());
		if (skill.hasSelfInflictEffects())
			sb.append("\nSelf Effects: " + skill.getSelfInflictEffectsDisplay());
		return new Field("**" + fieldTitle + ": " + skill.getName() + "**", sb.toString(), false);
	}

	/**
	 * Convenient method to gain experience and level up the entity
	 * 
	 * @param amount the amount of experience to gain
	 * @return a boolean showing whether the entity has leveled up
	 */
	public boolean gainExp(int amount) {
		// Save variables for embeded
		this.tempLevel = this.getLevel();
		this.tempMaxHp = this.getMaxHp();
		this.tempMaxMp = this.getMaxMp();
		this.tempAttack = this.getAttack();
		this.tempDefense = this.getDefense();
		this.tempSpeed = this.getSpeed();
		this.tempCriticalChance = this.getCriticalChance();
		this.tempCriticalDamage = this.getCriticalDamage();
		this.tempAccuracy = this.getAccuracy();
		this.tempEvasion = this.getEvasion();
		this.tempResistance = this.getResistance();

		this.exp += amount;
		boolean levelUp = false;
		while (this.getExp() >= this.getExpCap()) {
			levelUp = true;
			this.exp = this.getExp() - this.getExpCap();
			this.level++; // Level up
			this.levelUpStats(); // Stats up
			// Gain a new skill
			if (this.getLevel() % 10 == 1) {
				// TODO: replace/add new skill, user interaction might be needed
				TkonSkill newSkill = TkonSkillPool.getRandomSkill(this.getLevel(), this.getElement());
			}
		}
		return levelUp;
	}

	private void levelUpStats() {
		this.maxHp += (int) Math.round(TkonConfig.LEVELING_HP + randomByZero() * TkonConfig.LEVELING_HP_RANDOMNESS);
		this.hp = this.getMaxHp();
		this.maxMp += (int) Math.round(TkonConfig.LEVELING_MP + randomByZero() * TkonConfig.LEVELING_MP_RANDOMNESS);
		this.mp = this.getMaxMp();
		this.attack += (int) Math
				.round(TkonConfig.LEVELING_ATTACK + randomByZero() * TkonConfig.LEVELING_ATTACK_RANDOMNESS);
		this.defense += (int) Math
				.round(TkonConfig.LEVELING_DEFENSE + randomByZero() * TkonConfig.LEVELING_DEFENSE_RANDOMNESS);
		this.speed += (int) Math
				.round(TkonConfig.LEVELING_SPEED + randomByZero() * TkonConfig.LEVELING_SPEED_RANDOMNESS);
		this.criticalChance += TkonConfig.LEVELING_CRITICAL_CHANCE
				+ randomByZero() * TkonConfig.LEVELING_CRITICAL_CHANCE_RANDOMNESS;
		this.criticalChance = boundDouble(this.getCriticalChance(), 0, TkonConfig.MAX_CRITICAL_CHANCE);
		this.criticalDamage += TkonConfig.LEVELING_CRITICAL_DAMAGE
				+ randomByZero() * TkonConfig.LEVELING_CRITICAL_DAMAGE_RANDOMNESS;
		this.criticalDamage = boundDouble(this.getCriticalDamage(), 1, TkonConfig.MAX_CRITICAL_DAMAGE);
		this.accuracy += (int) Math
				.round(TkonConfig.LEVELING_ACCURACY + randomByZero() * TkonConfig.LEVELING_ACCURACY_RANDOMNESS);
		this.evasion += (int) Math
				.round(TkonConfig.LEVELING_EVASION + randomByZero() * TkonConfig.LEVELING_EVASION_RANDOMNESS);
		this.resistance += TkonConfig.LEVELING_RESISTANCE + randomByZero() * TkonConfig.LEVELING_RESISTANCE_RANDOMNESS;
		this.resistance = boundDouble(this.getResistance(), 0, TkonConfig.MAX_RESISTANCE);
	}

	private double boundDouble(double value, double min, double max) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	/**
	 * Generates a random double ranging from -1 to 1
	 */
	public static double randomByZero() {
		return r.nextDouble() * 2 - 1;
	}

	public boolean addSkill(TkonSkill skill) {
		if (skill == null || this.hasSkill(skill.getName()) || this.getSkills().size() >= TkonConfig.MAX_SKILLS)
			return false;
		this.getSkills().add(skill);
		return true;
	}

	/**
	 * Swaps the skill set with a new skill
	 * 
	 * @param newSkill the skill to be swapped in
	 * @param index    the index (0-3) to swap
	 * @return the skill that has been swapped out or null if it's non-existent
	 */
	public TkonSkill setSkill(TkonSkill newSkill, int index) {
		TkonSkill output = null;
		if (this.getSkills().size() >= index + 1 && this.getSkills().get(index) != null)
			output = this.getSkills().get(index);
		if (this.getSkills().size() >= index + 1)
			this.getSkills().set(index, newSkill);
		else
			this.getSkills().add(newSkill);
		return output;
	}

	public boolean hasSkill(String name) {
		for (TkonSkill skill : this.getSkills()) {
			if (!skill.getName().equalsIgnoreCase(name))
				continue;
			return true;
		}
		return false;
	}

	/*
	 * Storage Functions
	 */
	public JsonObject toJsonObject() {
		JsonObject obj = new JsonObject();
		obj.add("uuid", new JsonPrimitive(this.getUuid()));
		obj.add("name", new JsonPrimitive(this.getName()));
		obj.add("owner_id", new JsonPrimitive(this.getOwnerId()));

		obj.add("level", new JsonPrimitive(this.getLevel()));
		obj.add("exp", new JsonPrimitive(this.getExp()));
		obj.add("max_hp", new JsonPrimitive(this.getMaxHp()));
		obj.add("max_mp", new JsonPrimitive(this.getMaxMp()));

		obj.add("happiness", new JsonPrimitive(this.getHappiness()));
		obj.add("hunger", new JsonPrimitive(this.getHunger()));
		obj.add("energy", new JsonPrimitive(this.getEnergy()));
		obj.add("skills", getSkillsJsonArray());

		// isInCombat value is not stored! Defaults to be false
		obj.add("element", new JsonPrimitive(this.getElement().toString()));
		// skillCooldowns value is not stored! It's dynamically changed in combat

		obj.add("hp", new JsonPrimitive(this.getHp()));
		obj.add("mp", new JsonPrimitive(this.getMp()));
		obj.add("attack", new JsonPrimitive(this.getAttack()));
		obj.add("defense", new JsonPrimitive(this.getDefense()));
		obj.add("speed", new JsonPrimitive(this.getSpeed()));
		obj.add("critical_chance", new JsonPrimitive(this.getCriticalChance()));
		obj.add("critical_damage", new JsonPrimitive(this.getCriticalDamage()));
		obj.add("accuracy", new JsonPrimitive(this.getAccuracy()));
		obj.add("evasion", new JsonPrimitive(this.getEvasion()));
		obj.add("resistance", new JsonPrimitive(this.getResistance()));

		return obj;
	}

	public static void mainf(String[] args) throws InterruptedException, IOException {
		int p = 0;
		for (int a = 0; a < 10000000; a++) {
			double r = randomByZero();
			if (r > 0)
				p++;
			if (r < 0)
				p--;
		}
		System.out.println(p);
//		final TkonEntity entity = new TkonEntity("meow?", "TkON!");
//		System.out.println(entity.toString());
//		entity.gainExp(100999999);
//
//		System.out.println(entity.toString());

//		TkonSkill skill = new TkonSkill("TkON Shadow Strike!", "Poisioning attack!", 3, 1, 1, 1.10, 0.9, 0.05,
//				Arrays.asList(TkonElement.POISON, TkonElement.GRASS), Arrays.asList(TkonSkillModifier.DESTINY_HIT),
//				Arrays.asList(new TkonEffect(TkonEffectType.POISON, 3)), null);
//		System.out.println(TkonSkillPool.addSkill(1, skill));
//		TkonSkillsFileEditor.saveSkill(1, skill);

		// TODO: delete this!
	}

	/**
	 * Convert this instance to a JSON string for storage <br>
	 * Use {@code TkonEntity(JsonObject obj)} to reconstruct this
	 */
	public String toString() {
		return this.toJsonObject().toString();
	}

	public JsonArray getSkillsJsonArray() {
		JsonArray skills = new JsonArray();
		for (TkonSkill skill : this.getSkills())
			skills.add(skill.toJsonObject());
		return skills;
	}

	public ArrayList<TkonSkill> parseJsonSkillArray(JsonArray arr) {
		ArrayList<TkonSkill> skills = new ArrayList<TkonSkill>();
		for (JsonElement skillJson : arr)
			skills.add(new TkonSkill(skillJson.getAsJsonObject().toString()));
		return skills;
	}

	// G & S
	public String getOwnerId() {
		return ownerId;
	}

	public User getOwner() {
		return App.bot.getUserById(this.getOwnerId());
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
		return exp;
	}

	public int getExpCap() {
		return (this.level - 1) * TkonConfig.EXP_M + TkonConfig.EXP_B;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public int getMaxMp() {
		return maxMp;
	}

	public double getHappiness() {
		return happiness;
	}

	public double getHunger() {
		return hunger;
	}

	public double getEnergy() {
		return energy;
	}

	public ArrayList<TkonSkill> getSkills() {
		return skills;
	}

	public boolean isInCombat() {
		return isInCombat;
	}

	public TkonElement getElement() {
		return element;
	}

	public HashMap<TkonSkill, Integer> getSkillCooldowns() {
		return skillCooldowns;
	}

	public int getHp() {
		return hp;
	}

	public int getStandardHp() {
		return TkonConfig.getStandardHp(this.getLevel());
	}

	public int getMp() {
		return mp;
	}

	public int getStandardMp() {
		return TkonConfig.getStandardMp(this.getLevel());
	}

	public int getAttack() {
		return attack;
	}

	public int getStandardAttack() {
		return TkonConfig.getStandardAttack(this.getLevel());
	}

	public int getDefense() {
		return defense;
	}

	public int getStandardDefense() {
		return TkonConfig.getStandardDefense(this.getLevel());
	}

	public int getSpeed() {
		return speed;
	}

	public int getStandardSpeed() {
		return TkonConfig.getStandardSpeed(this.getLevel());
	}

	public double getCriticalChance() {
		return criticalChance;
	}

	public double getStandardCriticalChance() {
		return TkonConfig.getStandardCriticalChance(this.getLevel());
	}

	public double getCriticalDamage() {
		return criticalDamage;
	}

	public double getStandardCriticalDamage() {
		return TkonConfig.getStandardCriticalDamage(this.getLevel());
	}

	public int getAccuracy() {
		return accuracy;
	}

	public int getStandardAccuracy() {
		return TkonConfig.getStandardAccuracy(this.getLevel());
	}

	public int getEvasion() {
		return evasion;
	}

	public int getStandardEvasion() {
		return TkonConfig.getStandardEvasion(this.getLevel());
	}

	public double getResistance() {
		return resistance;
	}

	public double getStandardResistance() {
		return TkonConfig.getStandardResistance(this.getLevel());
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
	}

	public void setHappiness(double happiness) {
		this.happiness = happiness;
	}

	public void setHunger(double hunger) {
		this.hunger = hunger;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public void setSkills(ArrayList<TkonSkill> skills) {
		this.skills = skills;
	}

	public void setInCombat(boolean isInCombat) {
		this.isInCombat = isInCombat;
	}

	public void setElement(TkonElement element) {
		this.element = element;
	}

	public void setSkillCooldowns(HashMap<TkonSkill, Integer> skillCooldowns) {
		this.skillCooldowns = skillCooldowns;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setCriticalChance(double criticalChance) {
		this.criticalChance = criticalChance;
	}

	public void setCriticalDamage(double criticalDamage) {
		this.criticalDamage = criticalDamage;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public void setEvasion(int evasion) {
		this.evasion = evasion;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

}
