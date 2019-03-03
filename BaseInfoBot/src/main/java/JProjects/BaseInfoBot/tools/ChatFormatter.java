package JProjects.BaseInfoBot.tools;

public class ChatFormatter {
	private static final String bold = "**";
	private static final String italics = "*";
	private static final String strike_through = "~~";
	private static final String spoiler = "||";

	public static String bold(String msg) {
		return bold + msg + bold;
	}

	public static String italics(String msg) {
		return italics + msg + italics;
	}

	public static String strikethrough(String msg) {
		return strike_through + msg + strike_through;
	}

	public static String spoiler(String msg) {
		return spoiler + msg + spoiler;
	}

}
