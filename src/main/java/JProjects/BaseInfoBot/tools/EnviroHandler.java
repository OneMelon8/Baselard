package JProjects.BaseInfoBot.tools;

import java.util.ArrayList;
import java.util.Arrays;

public class EnviroHandler {

	public static String getBotToken() {
		return System.getenv("token");
	}

	public static ArrayList<String> getAdministrators() {
		String s = System.getenv("admins");
		return new ArrayList<String>(Arrays.asList(s.split(" ")));
//		return new ArrayList<String>(Arrays.asList("233735408737976320", "163543999104155649"));
	}
}
