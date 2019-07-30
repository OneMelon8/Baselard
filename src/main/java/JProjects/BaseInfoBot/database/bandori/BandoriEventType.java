package JProjects.BaseInfoBot.database.bandori;

public enum BandoriEventType {
	NORMAL, VS_LIVE, LIVE_GOALS, CHALLENGE_LIVE;

	public static BandoriEventType fromString(String attr) {
		attr = attr.toLowerCase();
		if (attr.equals("normal"))
			return NORMAL;
		else if (attr.equals("challenge live"))
			return CHALLENGE_LIVE;
		else if (attr.equals("vs live"))
			return VS_LIVE;
		else if (attr.equals("live goals"))
			return LIVE_GOALS;
		return null;
	}

	public String getDisplayName() {
		switch (this) {
		case NORMAL:
			return "Normal";
		case VS_LIVE:
			return "VS Live";
		case LIVE_GOALS:
			return "Live Goals";
		case CHALLENGE_LIVE:
			return "Challenge Live";
		default:
			return null;
		}
	}

	public String getEmote() {
		switch (this) {
		case NORMAL:
			return "🎸";
		case VS_LIVE:
			return "🆚";
		case LIVE_GOALS:
			return "🏆";
		case CHALLENGE_LIVE:
			return "🔥";
		default:
			return null;
		}
	}
}
