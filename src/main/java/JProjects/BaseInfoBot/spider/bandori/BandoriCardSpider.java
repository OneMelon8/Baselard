package JProjects.BaseInfoBot.spider.bandori;

import java.io.IOException;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.database.bandori.BandoriMember;
import JProjects.BaseInfoBot.tools.StringTools;

public class BandoriCardSpider {

	private static final String masterUrl = "https://bandori.party";
	private static final String querySearchUrl = "/cards/?&search=";
	private static final String queryRandomUrl = "/cards/random";

	// i_rarity=1-4 (stars)
	// i_attribute=1-4 (power pure cool happy)

	public static BandoriCard queryCard(String name) throws IOException {
		return queryCard(name, null);
	}

	public static BandoriCard queryCard(String name, BandoriAttribute attr) throws IOException {
		return queryCard(name, attr, 0, 0);
	}

	public static BandoriCard queryCard(String name, int index) throws IOException {
		return queryCard(name, null, 0, index);
	}

	public static BandoriCard queryCard(String name, BandoriAttribute attr, int rarity) throws IOException {
		return queryCard(name, attr, rarity, 0);
	}

	// Specific card
	public static BandoriCard queryCard(String name, BandoriAttribute attr, int rarity, int index) throws IOException {
		int page = 1;
		int totalCount = 0;
		BandoriCard card = null;

		String attrStr = attr == null ? "" : "&i_attribute=" + attr.getIndex();
		String rarityString = rarity == 0 ? "" : "&i_rarity=" + rarity;

		int total = -1;

		while (true) {
			Document doc = Jsoup.connect(
					masterUrl + querySearchUrl + name.replace(" ", "+") + attrStr + rarityString + "&page=" + page)
					.userAgent("Chrome").timeout(60 * 1000).get();
			if (total == -1) {
				String searchCount = doc.getElementById("page-content-wrapper")
						.select("div.collection-page-wrapper.as-container").select("div.padding20.total-search-results")
						.text().split(" ")[0];
				if (searchCount.isEmpty() || searchCount.equals("No"))
					return null;
				total = Integer.parseInt(searchCount);
			}
			Elements resultsRows = doc.select("div.collection-page-wrapper.as-container").get(0)
					.select("div.row.items");
			if (resultsRows.size() == 0)
				break;
			int rowIndex = 0, colIndex = 0;
			boolean stop = false;
			for (int a = 0; a < resultsRows.size(); a++) {
				int size = resultsRows.get(a).select("div.card-wrapper").size();
				for (int b = 0; b < size; b++) {
					if (totalCount == index) {
						stop = true;
						rowIndex = a;
						colIndex = b; // +1
						break;
					}
					totalCount++;
				}
				if (stop)
					break;
			}

			if (index > totalCount) {
				page++;
				continue;
			}

			if (rowIndex >= resultsRows.size()
					|| colIndex >= resultsRows.get(rowIndex).select("div.card-wrapper").size())
				return null;

			card = queryCardDirect(masterUrl + resultsRows.get(rowIndex).select("div.card-wrapper").get(colIndex)
					.select("a").get(0).attr("href"));
			card.setTotal(total);
			break;
		}
		return card;
	}

	public static BandoriCard queryRandom() throws IOException {
		return queryCardDirect(masterUrl + queryRandomUrl);
	}

	public static BandoriCard queryCardDirect(String url) throws IOException {
		Document doc = Jsoup.connect(url).userAgent("Chrome").timeout(60 * 1000).get();
		Element table = doc.select("table.table.about-table").get(0);
		BandoriCard card = new BandoriCard();
		card.setName(StringTools
				.smartReplaceNonEnglish(table.select("tr[data-field=card_name]").select("td").get(1).text()));
		card.setAttr(BandoriAttribute.fromString(table.select("tr[data-field=attribute]").select("td").get(1).text()));
		card.setMember(
				BandoriMember.valueOf(String
						.join("_",
								Arrays.asList(table.select("tr[data-field=member]").select("td").get(1)
										.select("span.text_with_link").text().split(" ")).subList(0, 2))
						.toUpperCase()));
		card.setRarity(table.select("tr[data-field=rarity]").select("td").get(1).select("img").size());
		card.setVersions(table.select("tr[data-field=versions]").select("td").get(1).text());
		card.setSkillName(StringTools
				.smartReplaceNonEnglish(table.select("tr[data-field=skill_name]").select("td").get(1).text()));
		card.setSkillDesc(table.select("tr[data-field=skill_type]").select("td").get(1).text());

		Elements icons = table.select("tr[data-field=images]").select("td").get(1).select("img");
		if (icons.size() == 1)
			card.setIconUrl("https:" + icons.get(0).select("img").last().attr("src"));
		else {
			card.setIconUrl("https:" + icons.get(1).select("img").last().attr("src"));
			card.setIconUrl2("https:" + icons.get(0).select("img").last().attr("src"));
		}

		if (!table.select("tr[data-field=arts]").isEmpty()) {
			Elements arts = table.select("tr[data-field=arts]").select("td").get(1).select("a");
			card.setArtUrl("https:" + arts.get(0).attr("href"));
			if (arts.size() == 2)
				card.setArtUrl2("https:" + arts.get(1).attr("href"));
		}

		if (!table.select("tr[data-field=chibis]").isEmpty()) {
			card.setChibiUrl("https:" + table.select("tr[data-field=chibis]").select("td").get(1).select("a").get(0)
					.select("img").last().attr("src"));
			card.setChibiUrl2("https:" + table.select("tr[data-field=chibis]").select("td").get(1).select("a").get(1)
					.select("img").last().attr("src"));
			card.setChibiUrl3("https:" + table.select("tr[data-field=chibis]").select("td").get(1).select("a").get(2)
					.select("img").last().attr("src"));
		}

		Elements statsWrapper = doc.select("div.card-statistics").select("div.tab-content").select("div.tab-pane");
		Elements stats = statsWrapper.get(statsWrapper.size() - 1).select("div.row");
		card.setPerformance(Integer.parseInt(stats.get(0).select("div").get(2).text()));
		card.setTechnique(Integer.parseInt(stats.get(1).select("div").get(2).text()));
		card.setVisual(Integer.parseInt(stats.get(2).select("div").get(2).text()));

		doc = Jsoup
				.connect(masterUrl
						+ table.select("tr[data-field=member]").select("td").get(1).select("a").get(0).attr("href"))
				.userAgent("Chrome").timeout(60 * 1000).get();
		table = doc.select("table.table.about-table").get(0);
		card.setColor(table.select("tr[data-field=color]").select("td").get(1).select("span.text-muted").text());
		return card;
	}
}
