package JProjects.BaseInfoBot.database.config;

import java.awt.Color;

public class BotConfig {
	public static final String BOT_ID = "548973434437107751";

	/*
	 * Reaction time-out time in ms
	 */
	public static final long REACTION_TIME_OUT = 30;
	public static final long REACTION_TIME_OUT_MS = REACTION_TIME_OUT * 1000;

	/*
	 * Messages
	 */
	public static final String PREFIX = "/";
	public static final String[] UNKNOWN_COMMAND = { "Hmm, that's not a valid command. Try **" + PREFIX + "help**",
			"I don't understand, check out **" + PREFIX + "help**",
			"Even a genius cannot understand you. Maybe you need to study up on **" + PREFIX + "help** :p" };
	public static final String[] COMMAND_SUGGESTION = { "Do you mean by **" + PREFIX + "#**?" };

	/*
	 * Miscellaneous
	 */
	public static final Color COLOR_MISC = new Color(66, 244, 66);
}
