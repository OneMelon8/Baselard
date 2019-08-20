package JProjects.BaseInfoBot.database.bandori;

import JProjects.BaseInfoBot.database.Emojis;

public enum BandoriSkillType {
	SCORE_UP, LIFE_RECOVERY, PERFECT_LOCK, LIFE_GUARD;

	public static BandoriSkillType fromClutterString(String msg) {
		msg = msg.toLowerCase();
		if (msg.contains("life guard"))
			return LIFE_GUARD;
		else if (msg.contains("life recovery"))
			return LIFE_RECOVERY;
		else if (msg.contains("perfect lock"))
			return PERFECT_LOCK;
		else if (msg.contains("score up"))
			return SCORE_UP;
		return null;
	}

	public static BandoriSkillType fromString(String attr) {
		attr = attr.toLowerCase();
		if (attr.equals("score up"))
			return SCORE_UP;
		else if (attr.equals("life guard"))
			return LIFE_GUARD;
		else if (attr.equals("life recovery"))
			return LIFE_RECOVERY;
		else if (attr.equals("perfect lock"))
			return PERFECT_LOCK;
		return null;
	}

	public String getDisplayName() {
		switch (this) {
		case SCORE_UP:
			return "Score Up";
		case LIFE_RECOVERY:
			return "Life Recovery";
		case PERFECT_LOCK:
			return "Perfect Lock (Note Boost)";
		case LIFE_GUARD:
			return "Life Guard";
		default:
			return null;
		}
	}

	public String getEmote() {
		switch (this) {
		case SCORE_UP:
			return Emojis.MUSIC_NOTES;
		case LIFE_RECOVERY:
			return Emojis.SPARKLING_HEART;
		case PERFECT_LOCK:
			return Emojis.LOCK;
		case LIFE_GUARD:
			return Emojis.SHIELD;
		default:
			return null;
		}
	}
}
