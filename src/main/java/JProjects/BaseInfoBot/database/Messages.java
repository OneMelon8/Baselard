package JProjects.BaseInfoBot.database;

import java.awt.Color;
import java.util.HashMap;

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
	 * URLs
	 */
	public static final String masterUrl = "https://masterofeternity.gamepedia.com/";
	public static final HashMap<String, String> childUrls = new HashMap<String, String>() {
		private static final long serialVersionUID = 1077261192907910426L;
		{
			put("pixieList", "pixies");
			put("c", "d");
		}
	};

	/*
	 * Miscellaneous
	 */
	public static final Color colorMisc = new Color(66, 244, 66);
}
