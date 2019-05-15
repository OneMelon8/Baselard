package JProjects.BaseInfoBot.database;

import java.awt.Color;

public class Messages {
	/*
	 * Messages
	 */
	public static final String prefix = "/";
	public static final String[] unknownCommand = { "Hmm, that's not a valid command. Try **" + prefix + "help**",
			"I don't understand, check out **" + prefix + "help**",
			"Even a genius cannot understand you. Maybe you need to study up on **" + prefix + "help** :p" };
	public static final String[] commandSuggestion = { "Do you mean by **" + prefix + "#**?" };

	/*
	 * Miscellaneous
	 */
	public static final Color colorMisc = new Color(66, 244, 66);
}
