package JProjects.BaseInfoBot.database.tkon;

public enum TkonSkillModifier {

	DESTINY_HIT, DESTINY_CRIT, ALWAYS_FIRST, ALWAYS_LAST;

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
