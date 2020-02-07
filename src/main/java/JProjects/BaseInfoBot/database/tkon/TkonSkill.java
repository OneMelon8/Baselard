package JProjects.BaseInfoBot.database.tkon;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class TkonSkill {

	private String name;
	private String description;

	private int mpCost;
	private int recycle;
	private double range;
	private double damageScaler;
	private double accuracy;
	private double criticalChance;

	private ArrayList<TkonElement> elements;
	private ArrayList<TkonSkillModifier> modifiers;
	private ArrayList<TkonEffect> inflictEffects;
	private ArrayList<TkonEffect> selfInflictEffects;

	public TkonSkill(String name, String description, int mpCost, int recycle, double range, double damageScaler,
			double accuracy, double criticalChance, ArrayList<TkonElement> elements,
			ArrayList<TkonSkillModifier> modifiers, ArrayList<TkonEffect> inflictEffects,
			ArrayList<TkonEffect> selfInflictEffects) {
		this.name = name;
		this.description = description;

		this.mpCost = mpCost;
		this.recycle = recycle;
		this.range = range;
		this.damageScaler = damageScaler;
		this.accuracy = accuracy;
		this.criticalChance = criticalChance;

		this.elements = elements == null ? new ArrayList<TkonElement>() : elements;
		this.modifiers = modifiers == null ? new ArrayList<TkonSkillModifier>() : modifiers;
		this.inflictEffects = inflictEffects == null ? new ArrayList<TkonEffect>() : inflictEffects;
		this.selfInflictEffects = selfInflictEffects == null ? new ArrayList<TkonEffect>() : selfInflictEffects;
	}

	/**
	 * Less strict constructor for manually creating skills
	 */
	public TkonSkill(String name, String description, int mpCost, int recycle, double range, double damageScaler,
			double accuracy, double criticalChance, List<TkonElement> elements, List<TkonSkillModifier> modifiers,
			List<TkonEffect> inflictEffects, List<TkonEffect> selfInflictEffects) {
		this(name, description, mpCost, recycle, range, damageScaler, accuracy, criticalChance,
				elements == null ? null : new ArrayList<TkonElement>(elements),
				modifiers == null ? null : new ArrayList<TkonSkillModifier>(modifiers),
				inflictEffects == null ? null : new ArrayList<TkonEffect>(inflictEffects),
				selfInflictEffects == null ? null : new ArrayList<TkonEffect>(selfInflictEffects));
	}

	/**
	 * Construct an instance from JSON string
	 */
	public TkonSkill(String jsonString) {
		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		this.name = obj.get("name").getAsString();
		this.description = obj.get("description").getAsString();

		this.mpCost = obj.get("mp_cost").getAsInt();
		this.recycle = obj.get("recycle").getAsInt();
		this.range = obj.get("range").getAsDouble();
		this.damageScaler = obj.get("damage_scaler").getAsDouble();
		this.accuracy = obj.get("accuracy").getAsDouble();
		this.criticalChance = obj.get("critical_chance").getAsDouble();

		this.elements = parseJsonElementsArray(obj.get("elements").getAsJsonArray());
		this.modifiers = parseJsonModifiersArray(obj.get("modifiers").getAsJsonArray());
		this.inflictEffects = parseJsonEffectsArray(obj.get("inflict_effects").getAsJsonArray());
		this.selfInflictEffects = parseJsonEffectsArray(obj.get("self_inflict_effects").getAsJsonArray());
	}

	public MessageEmbed getEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getElements().get(0).getColor());
		builder.setAuthor("Skill: " + this.getName());
		builder.setDescription(this.getDescription());

		StringBuilder sb = new StringBuilder();
		sb.append((this.getElements().size() > 1 ? "Elements" : "Element") + ": **" + this.getElementsDisplay() + "**");
		sb.append("\nMP Cost: **" + this.getMpCost() + " mana**");
		sb.append("\nRecycle: **" + this.getRecycle() + " turns**");
		sb.append("\nRange: **" + GeneralTools.round(this.getRange(), 2) + " tiles**");
		sb.append("\nDamage: **" + this.getDamageScalerDisplay() + " attack**");
		builder.addField(new Field("**Skill Information:**", sb.toString(), false));

		if (!this.getModifiers().isEmpty())
			builder.addField(new Field("**Skill Modifiers:**", this.getModifiersDisplay(), false));
		if (!this.getInflictEffects().isEmpty())
			builder.addField(new Field("**Inflict Effects:**", this.getInflictEffectsDisplay(), false));
		if (!this.getSelfInflictEffects().isEmpty())
			builder.addField(new Field("**Self Effects:**", this.getSelfInflictEffectsDisplay(), false));

		return builder.build();
	}

	/*
	 * Storage
	 */
	public JsonObject toJsonObject() {
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive(this.getName()));
		obj.add("description", new JsonPrimitive(this.getDescription()));

		obj.add("mp_cost", new JsonPrimitive(this.getMpCost()));
		obj.add("recycle", new JsonPrimitive(this.getRecycle()));
		obj.add("range", new JsonPrimitive(this.getRange()));
		obj.add("damage_scaler", new JsonPrimitive(this.getDamageScaler()));
		obj.add("accuracy", new JsonPrimitive(this.getAccuracy()));
		obj.add("critical_chance", new JsonPrimitive(this.getCriticalChance()));

		obj.add("elements", getElementsJsonArray());
		obj.add("modifiers", getModifiersJsonArray());
		obj.add("inflict_effects", getEffectsJsonArray(false));
		obj.add("self_inflict_effects", getEffectsJsonArray(true));

		return obj;
	}

	public JsonArray getElementsJsonArray() {
		JsonArray arr = new JsonArray();
		for (TkonElement obj : this.elements)
			arr.add(obj.toString());
		return arr;
	}

	public JsonArray getModifiersJsonArray() {
		JsonArray arr = new JsonArray();
		for (TkonSkillModifier obj : this.modifiers)
			arr.add(obj.toString());
		return arr;
	}

	public JsonArray getEffectsJsonArray(boolean selfInflict) {
		JsonArray arr = new JsonArray();
		for (TkonEffect obj : (selfInflict ? this.selfInflictEffects : this.inflictEffects))
			arr.add(obj.toJsonObject());
		return arr;
	}

	public ArrayList<TkonElement> parseJsonElementsArray(JsonArray arr) {
		ArrayList<TkonElement> arrayList = new ArrayList<TkonElement>();
		for (JsonElement skillJson : arr)
			arrayList.add(TkonElement.valueOf(skillJson.getAsString()));
		return arrayList;
	}

	public ArrayList<TkonSkillModifier> parseJsonModifiersArray(JsonArray arr) {
		ArrayList<TkonSkillModifier> arrayList = new ArrayList<TkonSkillModifier>();
		for (JsonElement skillJson : arr)
			arrayList.add(TkonSkillModifier.valueOf(skillJson.getAsString()));
		return arrayList;
	}

	public ArrayList<TkonEffect> parseJsonEffectsArray(JsonArray arr) {
		ArrayList<TkonEffect> arrayList = new ArrayList<TkonEffect>();
		for (JsonElement skillJson : arr)
			arrayList.add(new TkonEffect(skillJson.getAsJsonObject().toString()));
		return arrayList;
	}

	public String toString() {
		return this.toJsonObject().toString();
	}

	// G & S
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getMpCost() {
		return mpCost;
	}

	public int getRecycle() {
		return recycle;
	}

	public double getRange() {
		return range;
	}

	public double getRangeDisplay() {
		return GeneralTools.round(this.getRange(), 2);
	}

	public double getDamageScaler() {
		return damageScaler;
	}

	public String getDamageScalerDisplay() {
		return GeneralTools.round(this.getDamageScaler() * 100) + "%";
	}

	public double getAccuracy() {
		return accuracy;
	}

	public String getAccuracyDisplay() {
		return GeneralTools.round(this.getAccuracy() * 100) + "%";
	}

	public double getCriticalChance() {
		return criticalChance;
	}

	public String getCriticalChanceDisplay() {
		return GeneralTools.round(this.getCriticalChance() * 100) + "%";
	}

	public ArrayList<TkonElement> getElements() {
		return elements;
	}

	public String getElementsDisplay() {
		StringBuilder builder = new StringBuilder();
		for (TkonElement element : this.getElements())
			builder.append(element.getEmote() + " " + element.getDisplayName() + ", ");
		if (builder.length() != 0)
			builder.delete(builder.length() - 2, builder.length() - 1);
		return builder.toString();
	}

	public String getElementsEmotesDisplay() {
		StringBuilder builder = new StringBuilder();
		for (TkonElement element : this.getElements())
			builder.append(element.getEmote() + " ");
		if (builder.length() != 0)
			builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	public boolean hasModifiers() {
		return !(this.getModifiers() == null || this.getModifiers().isEmpty());
	}

	public ArrayList<TkonSkillModifier> getModifiers() {
		return modifiers;
	}

	public String getModifiersDisplay() {
		StringBuilder sb = new StringBuilder();
		for (TkonSkillModifier modifier : this.getModifiers())
			sb.append("**" + modifier.getDisplayName() + "**, ");
		if (sb.length() != 0)
			sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public boolean hasInflictEffects() {
		return !(this.getInflictEffects() == null || this.getInflictEffects().isEmpty());
	}

	public ArrayList<TkonEffect> getInflictEffects() {
		return inflictEffects;
	}

	public String getInflictEffectsDisplay() {
		StringBuilder sb = new StringBuilder();
		for (TkonEffect eff : this.getInflictEffects())
			sb.append("**" + eff.getEffectType().getDisplayName() + "** (" + eff.getDuration() + " "
					+ (eff.getDuration() > 1 ? "turns" : "turn") + "), ");
		if (sb.length() != 0)
			sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public boolean hasSelfInflictEffects() {
		return !(this.getSelfInflictEffects() == null || this.getSelfInflictEffects().isEmpty());
	}

	public ArrayList<TkonEffect> getSelfInflictEffects() {
		return selfInflictEffects;
	}

	public String getSelfInflictEffectsDisplay() {
		StringBuilder sb = new StringBuilder();
		for (TkonEffect eff : this.getSelfInflictEffects())
			sb.append("**" + eff.getEffectType().getDisplayName() + "** (" + eff.getDuration() + " "
					+ (eff.getDuration() > 1 ? "turns" : "turn") + "), ");
		if (sb.length() != 0)
			sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMpCost(int mpCost) {
		this.mpCost = mpCost;
	}

	public void setRecycle(int recycle) {
		this.recycle = recycle;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public void setDamageScaler(double damageScaler) {
		this.damageScaler = damageScaler;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public void setCriticalChance(double criticalChance) {
		this.criticalChance = criticalChance;
	}

	public void setElements(ArrayList<TkonElement> elements) {
		this.elements = elements;
	}

	public void setModifiers(ArrayList<TkonSkillModifier> modifiers) {
		this.modifiers = modifiers;
	}

	public void setInflictEffects(ArrayList<TkonEffect> inflictEffects) {
		this.inflictEffects = inflictEffects;
	}

	public void setSelfInflictEffects(ArrayList<TkonEffect> selfInflictEffects) {
		this.selfInflictEffects = selfInflictEffects;
	}

}
