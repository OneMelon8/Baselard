package JProjects.BaseInfoBot.database.tkon;

public enum TkonEffectType {

	// Buffs
	ATTACK_UP, DEFENSE_UP, CRITICAL_CHANCE_UP, CRITICAL_DAMAGE_UP, EVASION_UP, ACCURACY_UP, SPEED_UP, RESISTANCE_UP,
	REGENERATION,
	// Debuffs
	ATTACK_DOWN, DEFENSE_DOWN, ACCURACY_DOWN, RESISTANCE_DOWN, BURN, POISON, RADIATION_SICKNESS, SILENCED, OVERHEAT,
	FROSTBITE, FROZEN, PARALYZED;

	public String getDisplayName() {
		if (!this.toString().contains("_"))
			return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
		String[] words = this.toString().split("_");
		StringBuilder sb = new StringBuilder();
		for (String word : words)
			sb.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ");
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
