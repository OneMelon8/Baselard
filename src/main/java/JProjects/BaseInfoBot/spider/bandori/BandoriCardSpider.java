package JProjects.BaseInfoBot.spider.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.database.bandori.BandoriMember;

public class BandoriCardSpider {

	private static final String masterUrl = "https://bandori.party";
//	private static final String queryCurrentUrl = "/cards/?version=EN&status=current&ordering=start_date";
	private static final String querySearchUrl = "/cards/?search=";
	private static final String queryAllUrl = "/cards/?version=EN";

	// General query function
	public static ArrayList<String> queryCardList() throws IOException {
		int page = 1;
		ArrayList<String> cards = new ArrayList<String>();
		while (true) {
			Document doc = Jsoup.connect(masterUrl + queryAllUrl + "&page=" + page).userAgent("Chrome")
					.timeout(20 * 1000).get();
			Elements rows = doc.select("div.collection-page-wrapper.as-container").get(0).select("div.row.items");
			if (rows.size() == 0)
				break;
			for (Element row : rows)
				for (Element card : row.select("a"))
					cards.add(card.attr("data-ajax-title"));
			page++;
		}
		return cards;
	}

	// Specific card
	public static List<BandoriCard> queryCard(String name) throws IOException {
		List<BandoriCard> cards = new ArrayList<BandoriCard>();
		int page = 1;
		while (true) {
			Document doc = Jsoup.connect(masterUrl + querySearchUrl + name.replace(" ", "+") + "&page=" + page)
					.userAgent("Chrome").timeout(20 * 1000).get();
			Elements resultsRows = doc.select("div.collection-page-wrapper.as-container").get(0)
					.select("div.row.items");
			if (resultsRows.size() == 0)
				break;

			for (int a = 0; a < resultsRows.size(); a++) {
				Elements results = resultsRows.get(a).select("div.card-wrapper");
				for (int b = 0; b < results.size(); b++) {
					doc = Jsoup.connect(masterUrl + results.get(b).select("a").get(0).attr("href")).userAgent("Chrome")
							.timeout(20 * 1000).get();
					Element table = doc.select("table.table.about-table").get(0);

					BandoriCard card = new BandoriCard(
							table.select("tr[data-field=card_name]").select("td").get(1).text(),
							BandoriAttribute
									.fromString(table.select("tr[data-field=attribute]").select("td").get(1).text()),
							BandoriMember.valueOf(String
									.join("_",
											Arrays.asList(table.select("tr[data-field=member]").select("td").get(1)
													.select("span.text_with_link").text().split(" ")).subList(0, 2))
									.toUpperCase()),
							table.select("tr[data-field=rarity]").select("td").get(1).select("img").size(),
							table.select("tr[data-field=skill_name]").select("td").get(1).text(),
							table.select("tr[data-field=skill_type]").select("td").get(1).text(),
							"https:" + table.select("tr[data-field=images]").select("td").get(1).select("img").last()
									.attr("src"),
							"https:" + table.select("tr[data-field=arts]").select("td").get(1).select("a").get(0)
									.select("img").last().attr("src"));

					Elements statsWrapper = doc.select("div.card-statistics").select("div.tab-content")
							.select("div.tab-pane");
					Elements stats = statsWrapper.get(statsWrapper.size() - 1).select("div.row");
					card.setPerformance(Integer.parseInt(stats.get(0).select("div").get(2).text()));
					card.setTechnique(Integer.parseInt(stats.get(1).select("div").get(2).text()));
					card.setVisual(Integer.parseInt(stats.get(2).select("div").get(2).text()));

					doc = Jsoup.connect(masterUrl
							+ table.select("tr[data-field=member]").select("td").get(1).select("a").get(0).attr("href"))
							.userAgent("Chrome").timeout(20 * 1000).get();
					table = doc.select("table.table.about-table").get(0);
					card.setColor(
							table.select("tr[data-field=color]").select("td").get(1).select("span.text-muted").text());

					cards.add(card);
				}
			}

			page++;
		}
		return cards;
	}
}
