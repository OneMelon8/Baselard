package JProjects.BaseInfoBot.spider.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import JProjects.BaseInfoBot.database.BotConfig;
import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.database.bandori.BandoriMember;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriEventSpider {

	private static final String masterUrl = "https://bandori.party";
	private static final String queryCurrentUrl = "/events/?version=EN&status=current&ordering=start_date";
	private static final String querySearchUrl = "/events/?version=EN&search=";
	private static final String queryAllUrl = "/events/?version=EN&ordering=start_date";
//	private static final String queryTrackerUrl = "https://bestdori.com/tool/eventtracker";

	// General query function
	public static ArrayList<String> queryEventList(boolean current) throws IOException {
		int page = 1;
		ArrayList<String> events = new ArrayList<String>();
		while (true) {
			Document doc = Jsoup.connect(masterUrl + (current ? queryCurrentUrl : queryAllUrl) + "&page=" + page)
					.userAgent("Chrome").timeout(20 * 1000).get();
			Elements rows = doc.select("div.collection-page-wrapper.as-container").get(0).select("div.row.items");
			if (rows.size() == 0)
				break;
			for (Element row : rows)
				for (Element event : row.select("a"))
					events.add(event.attr("data-ajax-title"));
			page++;
		}
		return events;
	}

	// Specific event
	public static List<MessageEmbed> queryEvent(String name) throws IOException {
		List<MessageEmbed> output = new ArrayList<MessageEmbed>();

		Document doc = Jsoup.connect(masterUrl + querySearchUrl + name.replace(" ", "+")).userAgent("Chrome")
				.timeout(20 * 1000).get();
		doc = Jsoup
				.connect(masterUrl + doc.select("div.collection-page-wrapper.as-container").get(0)
						.select("div.row.items").select("a").get(0).attr("href"))
				.userAgent("Chrome").timeout(20 * 1000).get();
		Element wrapper = doc.select("div.event-info").get(0);
		Element table = wrapper.select("table.table.about-table").get(0);

		EmbedBuilder eventBuilder = new EmbedBuilder();
		eventBuilder.setColor(BotConfig.COLOR_MISC);
		eventBuilder.setAuthor(table.select("tr[data-field=name]").text().replace("Title", "").trim());
		eventBuilder.setImage("https:" + wrapper.select("img.event-image").attr("src"));

		String start = table.select("tr[data-field=english_start_date]").select("span.datetime").text()
				.replace(" +0000", "");
		String end = table.select("tr[data-field=english_end_date]").select("span.datetime").text().replace(" +0000",
				"");
		eventBuilder.setDescription("UTC: " + start + " - " + end);

		StringBuilder sb = new StringBuilder();
		sb.append("Countdown: **"
				+ TimeFormatter.getCountDown(TimeFormatter.getDateFromBandoriString(start).getTimeInMillis(),
						TimeFormatter.getDateFromBandoriString(end).getTimeInMillis(), System.currentTimeMillis())
				+ "**");
		sb.append("\nEvent Type: **" + table.select("tr[data-field=type]").select("td").get(1).text() + "**");
		sb.append("\nAttribute: **" + BandoriAttribute
				.fromString(table.select("tr[data-field=boost_attribute]").select("td").get(1).text()).getEmote()
				+ "**");

		sb.append("\nPref Band: **");
		for (Element aElement : table.select("tr[data-field=boost_members]").select("td").get(1).select("a")) {
			String[] info = aElement.attr("data-ajax-title").split(" ");
			sb.append(BandoriMember.valueOf((info[0] + "_" + info[1]).toUpperCase()).getEmote());
		}
		sb.append("**");

		eventBuilder.addField(new Field("Event Information:", sb.toString(), false));
		output.add(eventBuilder.build());

		for (Element aElement : table.select("tr[data-field=cards]").select("td").get(1).select("a")) {
			String[] cardData = aElement.attr("data-ajax-title").split(" ");
			BandoriCard card = BandoriCardSpider
					.queryCard(String.join(" ", Arrays.asList(cardData).subList(6, cardData.length)));
			output.add(card.getEmbededMessage());
		}
		return output;
	}

	public static MessageEmbed queryEventTracking() {
		EmbedBuilder b = new EmbedBuilder();
		b.setColor(BotConfig.COLOR_MISC);
		b.setAuthor("Work in Progress...");
		b.setDescription("This feature is currently work in progress");
		return b.build();
	}
}
