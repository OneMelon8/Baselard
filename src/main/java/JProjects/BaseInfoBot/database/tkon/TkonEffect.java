package JProjects.BaseInfoBot.database.tkon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class TkonEffect {

	private TkonEffectType effectType;
	private TkonEntity target;
	private TkonEntity caster;
	private int duration;

	/**
	 * Construct an "abstract" effect for displaying
	 */
	public TkonEffect(TkonEffectType effectType, int duration) {
		this.effectType = effectType;
		this.duration = duration;
	}

	/**
	 * Combat effect constructor
	 */
	public TkonEffect(TkonEffectType effectType, TkonEntity target, TkonEntity caster, int duration) {
		this(effectType, duration);
		this.target = target;
		this.caster = caster;
	}

	/**
	 * Construct an "abstract" instance from JSON string
	 */
	public TkonEffect(String jsonString) {
		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		this.effectType = TkonEffectType.valueOf(obj.get("effect_type").getAsString());
		this.duration = obj.get("duration").getAsInt();
	}

	public JsonObject toJsonObject() {
		JsonObject obj = new JsonObject();
		obj.add("effect_type", new JsonPrimitive(this.getEffectType().toString()));
		// target and caster values are not stored! These will be changed dynamically
		obj.add("duration", new JsonPrimitive(this.getDuration()));

		return obj;
	}

	public String toString() {
		return this.toJsonObject().toString();
	}

	public TkonEffectType getEffectType() {
		return effectType;
	}

	public TkonEntity getTarget() {
		return target;
	}

	public TkonEntity getCaster() {
		return caster;
	}

	public int getDuration() {
		return duration;
	}

	public void setEffectType(TkonEffectType effectType) {
		this.effectType = effectType;
	}

	public void setTarget(TkonEntity target) {
		this.target = target;
	}

	public void setCaster(TkonEntity caster) {
		this.caster = caster;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
