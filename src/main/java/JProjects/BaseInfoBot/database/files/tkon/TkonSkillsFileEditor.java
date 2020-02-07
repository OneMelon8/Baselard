package JProjects.BaseInfoBot.database.files.tkon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JProjects.BaseInfoBot.database.tkon.TkonElement;
import JProjects.BaseInfoBot.database.tkon.TkonSkill;

public class TkonSkillsFileEditor {

	// 1st Layer: Level >> JSON object containing skills of that level
	// 2nd Layer: Element >> JSON array of skills

	public static void saveSkill(int level, TkonSkill skill) throws IOException {
		if (level <= 0)
			level = 1;
		saveSkill(String.valueOf(level), skill);
	}

	public static boolean saveSkill(String level, TkonSkill skill) throws IOException {
		JsonObject obj;
		try {
			obj = readFile();
			if (obj == null)
				throw new NullPointerException();
		} catch (Exception e) {
			obj = new JsonObject();
		}
		if (!obj.has(level))
			obj.add(level, new JsonObject());

		JsonObject levelSkills = obj.get(level).getAsJsonObject();
		for (TkonElement element : skill.getElements()) {
			if (!levelSkills.has(element.toString()))
				levelSkills.add(element.toString(), new JsonArray());
			JsonArray skills = levelSkills.get(element.toString()).getAsJsonArray();
			if (skills.contains(skill.toJsonObject()))
				return false;
			skills.add(skill.toJsonObject());
		}

		Files.write(Paths.get("./tkonSkills.json"), obj.toString().getBytes());
		return true;
	}

	public static JsonObject readFile() throws FileNotFoundException {
		FileReader reader = new FileReader("./tkonSkills.json");
		return new JsonParser().parse(reader).getAsJsonObject();
	}

}
