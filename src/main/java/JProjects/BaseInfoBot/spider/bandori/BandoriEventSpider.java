package JProjects.BaseInfoBot.spider.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.database.bandori.BandoriEvent;
import JProjects.BaseInfoBot.database.bandori.BandoriEventType;
import JProjects.BaseInfoBot.database.bandori.BandoriMember;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.StringTools;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BandoriEventSpider {

	private static final String masterUrl = "https://bandori.party";
//	private static final String queryCurrentUrl = "/events/?version=EN&status=current&ordering=start_date";
	private static final String querySearchUrl = "/events/?version=EN&search=";
	private static final String queryAllUrl = "/events/?version=EN&ordering=english_start_date&reverse_order=on";
//	private static final String queryTrackerUrl = "https://bestdori.com/tool/eventtracker";

	// General query function
	public static ArrayList<BandoriEvent> queryEventList() throws IOException {
		return queryEventList(1);
	}

	public static ArrayList<BandoriEvent> queryEventList(int page) throws IOException {
		ArrayList<BandoriEvent> events = new ArrayList<BandoriEvent>();
		Document doc = Jsoup.connect(masterUrl + queryAllUrl + "&page=" + page).userAgent("Chrome").timeout(60 * 1000)
				.get();
		Elements rows = doc.select("div.collection-page-wrapper.as-container").get(0).select("div.row.items");
		if (rows.size() == 0)
			return events;
		for (Element row : rows)
			for (Element eventWrapper : row.select("div.col-md-6")) {
				Element topWrapper = eventWrapper.select("div.text-center.top-item").first();
				String name = topWrapper.select("a").first().attr("data-ajax-title");

				Element bodyWrapper = eventWrapper.select("table.table.about-table").first();
				Calendar start = TimeFormatter
						.getDateFromBandoriString(bodyWrapper.select("tr[data-field=english_start_date]")
								.select("span.datetime").first().text().replace(" +0000", ""));
				events.add(new BandoriEvent(name, start, start));
			}
		return events;
	}

	// Latest
	public static BandoriEvent queryLatest() throws IOException {
		return queryEventList().get(0);
	}

	// Specific event
	public static BandoriEvent queryEvent(String name) throws IOException {
		Document doc = Jsoup.connect(masterUrl + querySearchUrl + name.replace(" ", "+")).userAgent("Chrome")
				.timeout(60 * 1000).get();
		doc = Jsoup
				.connect(masterUrl + doc.select("div.collection-page-wrapper.as-container").get(0)
						.select("div.row.items").select("a").get(0).attr("href"))
				.userAgent("Chrome").timeout(60 * 1000).get();
		Element wrapper = doc.select("div.event-info").get(0);
		Element table = wrapper.select("table.table.about-table").get(0);

		String eventName = StringTools
				.smartReplaceNonEnglish(table.select("tr[data-field=name]").text().replace("Title", "").trim());
		String imageUrl = "https:" + wrapper.select("img.event-image").attr("src");

		Calendar start = TimeFormatter.getDateFromBandoriString(
				table.select("tr[data-field=english_start_date]").select("span.datetime").text().replace(" +0000", ""));
		Calendar end = TimeFormatter.getDateFromBandoriString(
				table.select("tr[data-field=english_end_date]").select("span.datetime").text().replace(" +0000", ""));

		BandoriEvent event = new BandoriEvent(eventName, start, end);
		event.setType(BandoriEventType.fromString(table.select("tr[data-field=type]").select("td").get(1).text()));
		event.setAttribute(
				BandoriAttribute.fromString(table.select("tr[data-field=boost_attribute]").select("td").get(1).text()));
		ArrayList<BandoriMember> band = new ArrayList<BandoriMember>();
		for (Element aElement : table.select("tr[data-field=boost_members]").select("td").get(1).select("a")) {
			String[] info = aElement.attr("data-ajax-title").split(" ");
			band.add(BandoriMember.valueOf((info[0] + "_" + info[1]).toUpperCase()));
		}
		event.setBand(band);
		event.setImageUrl(imageUrl);
		ArrayList<BandoriCard> cards = new ArrayList<BandoriCard>();
		for (Element aElement : table.select("tr[data-field=cards]").select("td").get(1).select("a")) {
			String[] cardData = aElement.attr("data-ajax-title").split(" ");
			cards.add(
					BandoriCardSpider.queryCard(String.join(" ", Arrays.asList(cardData).subList(6, cardData.length))));
		}
		event.setCards(cards);

		return event;
	}

	public static MessageEmbed queryEventTracking() {
		EmbedBuilder b = new EmbedBuilder();
		b.setColor(BotConfig.COLOR_MISC);
		b.setAuthor("Work in Progress...");
		b.setDescription("This feature is currently work in progress");
		return b.build();
	}
}
