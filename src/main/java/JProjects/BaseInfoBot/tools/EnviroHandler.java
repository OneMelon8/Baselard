package JProjects.BaseInfoBot.tools;

import java.util.ArrayList;
import java.util.Arrays;

public class EnviroHandler {

	public static String getBotToken() {
		return System.getenv("token");
	}

	public static String getImgbbToken() {
		return System.getenv("imgbb");
	}

	public static String getPastebinToken() {
		return System.getenv("pastebin");
	}

	public static ArrayList<String> getAdministrators() {
		String s = System.getenv("admins");
		return new ArrayList<String>(Arrays.asList(s.split(" ")));
	}
}
