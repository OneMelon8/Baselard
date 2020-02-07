package JProjects.BaseInfoBot.database.tkon;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import JProjects.BaseInfoBot.database.config.TkonConfig;
import JProjects.BaseInfoBot.database.files.tkon.TkonSkillsFileEditor;

public class TkonSkillPool {

	// Input: element + level
	// Hidden: process mutations and generate random skill
	// Output: a certain skill

	// HashMap: Level >> Map { Element : Skills[] }
	private static HashMap<Integer, HashMap<TkonElement, ArrayList<TkonSkill>>> skillPool = new HashMap<Integer, HashMap<TkonElement, ArrayList<TkonSkill>>>();
	private static final Random r = new Random();

	public static TkonSkill getRandomSkill(int level, TkonElement element) {
		if (!skillPool.containsKey(level))
			return null;
		HashMap<TkonElement, ArrayList<TkonSkill>> skillPoolByLevel = skillPool.get(level);
		int mutationStatus = 0; // 0 - no mutation, 1 - same class, 2 - across branches, 3 - completely random
		double mutationFactor = r.nextDouble();
		if (mutationFactor < TkonConfig.MUTATION_SAME_CLASS + TkonConfig.MUTATION_ACROSS_SPECIFICATIONS
				+ TkonConfig.MUTATION_COMPLETELY_RANDOM) {
			// Mutation confirmed! Now which mutation to choose?
			if (element.isRootElement() && mutationFactor < TkonConfig.MUTATION_SAME_CLASS)
				mutationStatus = 1;
			else if (!element.isRootElement()
					&& mutationFactor < TkonConfig.MUTATION_SAME_CLASS + TkonConfig.MUTATION_ACROSS_SPECIFICATIONS)
				mutationStatus = 2;
			else if (mutationFactor < TkonConfig.MUTATION_SAME_CLASS + TkonConfig.MUTATION_ACROSS_SPECIFICATIONS
					+ TkonConfig.MUTATION_COMPLETELY_RANDOM)
				mutationStatus = 3;
		}

		ArrayList<TkonSkill> finalSkillPool = new ArrayList<TkonSkill>();
		if (mutationStatus == 0)
			finalSkillPool.addAll(skillPoolByLevel.get(element));
		else if (mutationStatus == 1)
			finalSkillPool.addAll(skillPoolByLevel.get(element.getRandomLeafElement()));
		else if (mutationStatus == 2) {
			for (TkonElement elem : element.getRelatedElements())
				finalSkillPool.addAll(skillPoolByLevel.get(elem));
		} else if (mutationStatus == 3) {
			for (TkonElement elem : TkonElement.values())
				finalSkillPool.addAll(skillPoolByLevel.get(elem));
		}

		return finalSkillPool.get(new Random().nextInt(finalSkillPool.size()));
	}

	public static boolean addSkill(int level, TkonSkill skill) {
		HashMap<TkonElement, ArrayList<TkonSkill>> skillPoolByLevel = skillPool.containsKey(level)
				? skillPool.get(level)
				: new HashMap<TkonElement, ArrayList<TkonSkill>>();
		for (TkonElement element : skill.getElements()) {
			ArrayList<TkonSkill> skills = skillPoolByLevel.containsKey(element) ? skillPoolByLevel.get(element)
					: new ArrayList<TkonSkill>();
			if (hasSkill(skills, skill.getName()))
				return false;
			skills.add(skill);
			skillPoolByLevel.put(element, skills);
		}
		skillPool.put(level, skillPoolByLevel);
		return true;
	}

	private static boolean hasSkill(ArrayList<TkonSkill> skills, String name) {
		for (TkonSkill skill : skills) {
			if (!skill.getName().equalsIgnoreCase(name))
				continue;
			return true;
		}
		return false;
	}

	/**
	 * Add skills from the JSON file
	 */
	public static void initSkillPool() {
		JsonObject skillPoolJsonObject;
		try {
			skillPoolJsonObject = TkonSkillsFileEditor.readFile();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		for (Entry<String, JsonElement> poolEntry : skillPoolJsonObject.entrySet()) {
			int level = Integer.parseInt(poolEntry.getKey());
			JsonObject skillPoolByLevel = poolEntry.getValue().getAsJsonObject();
			HashMap<TkonElement, ArrayList<TkonSkill>> skillMap = new HashMap<TkonElement, ArrayList<TkonSkill>>();
			for (Entry<String, JsonElement> poolByLevelEntry : skillPoolByLevel.entrySet()) {
				TkonElement element = TkonElement.valueOf(poolByLevelEntry.getKey());
				JsonArray skillsJsonArray = poolByLevelEntry.getValue().getAsJsonArray();
				ArrayList<TkonSkill> skills = new ArrayList<TkonSkill>();
				Iterator<JsonElement> skillsIterator = skillsJsonArray.iterator();
				while (skillsIterator.hasNext())
					skills.add(new TkonSkill(skillsIterator.next().toString()));
				skillMap.put(element, skills);
			}
			skillPool.put(level, skillMap);
		}

	}
	// a dance of fire and ice (fire + ice element ability super rare)
}
