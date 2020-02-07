package JProjects.BaseInfoBot.database.files.tkon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JProjects.BaseInfoBot.database.tkon.TkonEntity;

public class TkonFileEditor {

	public static void saveTkonEntity(String ownerId, TkonEntity entity) throws IOException {
		JsonObject obj;
		try {
			obj = readFile();
			if (obj == null)
				throw new NullPointerException();
		} catch (Exception e) {
			obj = new JsonObject();
		}
		obj.add(ownerId, entity.toJsonObject());
		Files.write(Paths.get("./tkons.json"), obj.toString().getBytes());
	}

	public static TkonEntity getTkonEntityById(String ownerId) {
		JsonObject obj;
		try {
			obj = readFile();
			if (obj == null)
				throw new NullPointerException();
		} catch (Exception e) {
			return null;
		}
		if (!obj.has(ownerId))
			return null;
		return new TkonEntity(obj.get(ownerId).getAsJsonObject().toString());
	}

	public static JsonObject readFile() throws FileNotFoundException {
		FileReader reader = new FileReader("./tkons.json");
		return new JsonParser().parse(reader).getAsJsonObject();
	}

}
