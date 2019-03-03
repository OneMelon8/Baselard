package JProjects.BaseInfoBot.database.files;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HangarFileEditor {

	@SuppressWarnings("unchecked")
	public static void write(String key, String msg) throws IOException {
		JSONObject obj;
		try {
			obj = read();
		} catch (Exception e) {
			obj = new JSONObject();
		}
		obj.put(key, msg);
		Files.write(Paths.get("./userSuits.json"), obj.toJSONString().getBytes());
	}

	public static JSONObject read() throws IOException, ParseException {
		FileReader reader = new FileReader("./userSuits.json");
		JSONParser jsonParser = new JSONParser();
		return (JSONObject) jsonParser.parse(reader);
	}

}
