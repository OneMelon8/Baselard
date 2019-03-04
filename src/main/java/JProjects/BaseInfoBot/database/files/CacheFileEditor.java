package JProjects.BaseInfoBot.database.files;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import JProjects.BaseInfoBot.database.Suit;
import net.dv8tion.jda.core.entities.User;

public class CacheFileEditor {

	private static final String fileName = "cache";

	// OBJ:
	// - SUIT NAME {
	// level => suit
	// }

	@SuppressWarnings("unchecked")
	public static void write(Suit suit) throws IOException {
		JSONObject obj;
		try {
			obj = read();
		} catch (Exception ex) {
			obj = new JSONObject();
		}

		String name = suit.getCodeName().toLowerCase();
		JSONObject parentDir;
		boolean expired = true;
		try {
			parentDir = getSuitMap(name);
			if (Long.valueOf((String) parentDir.get("expire")) > System.currentTimeMillis())
				expired = false;
		} catch (Exception ex) {
			parentDir = new JSONObject();
		}

		suit.levelChange(1);
		parentDir.put("suit", suit.toString());
		if (expired) {
			// 60 minutes of cache time
			long expire = System.currentTimeMillis() + 60 * 60 * 1000;
			parentDir.put("expire", expire);
		}

		obj.put(name, parentDir);
		Files.write(Paths.get("./" + fileName + ".json"), obj.toJSONString().getBytes());
	}

	@SuppressWarnings("unchecked")
	public static void writePerm(Suit suit) throws IOException {
		JSONObject obj;
		try {
			obj = read();
		} catch (Exception ex) {
			obj = new JSONObject();
		}

		String name = suit.getCodeName().toLowerCase();
		JSONObject parentDir;
		boolean expired = true;
		try {
			parentDir = getSuitMap(name);
			if (Long.valueOf((String) parentDir.get("expire")) > System.currentTimeMillis())
				expired = false;
		} catch (Exception ex) {
			parentDir = new JSONObject();
		}

		suit.levelChange(1);
		parentDir.put("suit", suit.toString());
		if (expired) {
			// 5 years
			long expire = System.currentTimeMillis() + 5 * 365 * 24 * 60 * 60 * 1000;
			parentDir.put("expire", expire);
		}

		obj.put(name, parentDir);
		Files.write(Paths.get("./" + fileName + ".json"), obj.toJSONString().getBytes());
	}

	public static JSONObject read() throws IOException, ParseException {
		FileReader reader = new FileReader("./" + fileName + ".json");
		JSONParser jsonParser = new JSONParser();
		return (JSONObject) jsonParser.parse(reader);
	}

	public static JSONObject getSuitMap(String name) throws IOException, ParseException {
		return (JSONObject) read().get(name);
	}

	public static Suit getSuit(String name, User owner) throws IOException, ParseException {
		JSONObject map = getSuitMap(name);
		if (map == null || map.get("suit") == null
				|| Long.parseLong(String.valueOf(map.get("expire"))) < System.currentTimeMillis())
			return null;
		Suit suit = Suit.fromString((String) map.get("suit"));
		suit.changeOwner(owner);
		return suit;
	}
}
