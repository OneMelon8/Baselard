package JProjects.BaseInfoBot.database.files;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import JProjects.BaseInfoBot.database.Suit;
import JProjects.BaseInfoBot.database.SuitType;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.StringSimilarity;

public class SuitFileEditor {

	private static final String fileName = "suits";

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

	public static JSONArray getSuitsFromType(String key) throws IOException, ParseException {
		return (JSONArray) read().get(key);
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getSuitsFromAllTypes() throws IOException, ParseException {
		JSONArray arr = new JSONArray();
		arr.addAll(getSuitsFromType("assault"));
		arr.addAll(getSuitsFromType("support"));
		arr.addAll(getSuitsFromType("bombardier"));
		arr.addAll(getSuitsFromType("sniper"));
		return arr;
	}

	public static boolean isSuitValid(String name) {
		try {
			JSONArray assaults = getSuitsFromType("assault");
			JSONArray supports = getSuitsFromType("support");
			JSONArray bombardiers = getSuitsFromType("bombardier");
			JSONArray snipers = getSuitsFromType("sniper");
			if (assaults.contains(name) || supports.contains(name) || bombardiers.contains(name)
					|| snipers.contains(name))
				return true;
			return false;
		} catch (Exception ex) {
			GeneralTools.logError(ex);
			return false;
		}
	}

	public static Object[] suggestSuit(String suitName) throws IOException, ParseException {
		JSONArray assaults = getSuitsFromType("assault");
		JSONArray supports = getSuitsFromType("support");
		JSONArray bombardiers = getSuitsFromType("bombardier");
		JSONArray snipers = getSuitsFromType("sniper");

		Object[] objArr = new Object[2];
		String suggestion = "";
		if (assaults.contains(suitName) || supports.contains(suitName) || bombardiers.contains(suitName)
				|| snipers.contains(suitName)) {
			suggestion = Suit.rebuildName(suitName);
			objArr[1] = 1D;
		} else if (SuitAliasesFileEditor.getSuitFromAlias(suitName) != null) {
			suggestion = SuitAliasesFileEditor.getSuitFromAlias(suitName);
			objArr[1] = 1D;
		} else {
			Object[] attemptObj = findMaxSim(suitName,
					new ArrayList<JSONArray>(Arrays.asList(assaults, supports, bombardiers, snipers)));
			String attempt = (String) attemptObj[0];
			double attemptVal = (Double) attemptObj[1];
			suggestion = attempt;
			objArr[1] = attemptVal;
		}

		objArr[0] = suggestion;
		return objArr;
	}

	public static Object[] findMaxSim(String s, ArrayList<JSONArray> arrs) {
		String maxSimStr = "";
		double maxSim = 0;
		for (JSONArray jarr : arrs)
			for (Object val : jarr) {
				String comp = (String) val;
				double sim = StringSimilarity.similarity(s, comp);
				if (sim > maxSim) {
					maxSim = sim;
					maxSimStr = comp;
				}
			}
		return new Object[] { maxSimStr, maxSim };
	}

	public static SuitType getSuitType(String name) {
		try {
			JSONArray assaults = getSuitsFromType("assault");
			JSONArray supports = getSuitsFromType("support");
			JSONArray bombardiers = getSuitsFromType("bombardier");
			JSONArray snipers = getSuitsFromType("sniper");

			if (!isSuitValid(name))
				return null;
			String type = null;
			if (assaults.contains(name))
				type = "assault";
			else if (supports.contains(name))
				type = "support";
			else if (bombardiers.contains(name))
				type = "bombardier";
			else if (snipers.contains(name))
				type = "sniper";
			return SuitType.fromString(type);
		} catch (Exception ex) {
			GeneralTools.logError(ex);
			return null;
		}

	}
}
