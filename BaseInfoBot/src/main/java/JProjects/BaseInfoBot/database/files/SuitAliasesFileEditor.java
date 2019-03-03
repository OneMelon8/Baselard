package JProjects.BaseInfoBot.database.files;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SuitAliasesFileEditor {

	private static final String fileName = "suitAliases";

	@SuppressWarnings("unchecked")
	public static void write(String key, Object value) throws IOException {
		JSONObject obj;
		try {
			obj = read();
		} catch (Exception e) {
			obj = new JSONObject();
		}
		obj.put(key, value);
		Files.write(Paths.get("./" + fileName + ".json"), obj.toJSONString().getBytes());
	}

	public static JSONObject read() throws IOException, ParseException {
		FileReader reader = new FileReader("./" + fileName + ".json");
		JSONParser jsonParser = new JSONParser();
		return (JSONObject) jsonParser.parse(reader);
	}

	public static String getSuitFromAlias(String alias) throws IOException, ParseException {
		return (String) read().get(alias);
	}
}
