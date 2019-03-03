package JProjects.BaseInfoBot.spider;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import JProjects.BaseInfoBot.database.Grade;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.SuitType;
import JProjects.BaseInfoBot.tools.GeneralTools;

public class Spider {

	private static final String[] statArr = new String[] { "hp", "atk", "def", "acc", "eva", "cntr", "crt%", "crit",
			"stn", "frz", "sil", "acd" };
	private static final List<String> statLst = Arrays.asList(statArr);

	// General query function
	public static HashMap<String, String> query(String subUrl, SuitType type, Grade grade) throws IOException {
		Document doc = Jsoup.connect(Messages.masterUrl + subUrl).userAgent("Chrome").timeout(20 * 1000).get();
		HashMap<String, String> stats = new HashMap<String, String>();
		stats.put("name", subUrl.replace("_", " "));
		stats.put("type", type.toString());
		stats.put("grade", grade.toString());
		Element table = doc.select("table.wikitable.mw-collapsible tbody").get(0);
		for (Element tableRow : table.select("tr")) {
			String rowText = tableRow.text();
			if (rowText == null || rowText.isEmpty())
				continue;
			String[] contents = rowText.split(" ");
			for (int a = 0; a < contents.length; a++) {
				String text = contents[a].toLowerCase().replace("ω", "").trim();
				// Basic statistics
				if (a + 1 < contents.length && statLst.contains(text)) {
					String statStr = contents[a + 1].replace("%", "").trim();
					if (!GeneralTools.isNumeric(statStr))
						continue;
					stats.put(text, statStr);
				} else if (text.contains("lv.") && !stats.containsKey("level")) {
					// Level information
					stats.put("level", text.replace("lv.", ""));
				}
			}
		}
		return stats;
	}

}
