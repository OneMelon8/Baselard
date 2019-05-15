package JProjects.BaseInfoBot.spider.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriEventSpider {

	private static final String masterUrl = "https://bandori.party";
//	private static final String queryCurrentUrl = "/events/?version=EN&status=current&ordering=start_date";
	private static final String querySearchUrl = "/events/?version=EN&search=";
	private static final String queryAllUrl = "/events/?version=EN&ordering=start_date";

	// General query function
	public static ArrayList<String> queryEventList() throws IOException {
		int page = 1;
		ArrayList<String> events = new ArrayList<String>();
		while (true) {
			Document doc = Jsoup.connect(masterUrl + queryAllUrl + "&page=" + page).userAgent("Chrome")
					.timeout(20 * 1000).get();
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
		eventBuilder.setColor(Messages.colorMisc);
		eventBuilder.setAuthor(table.select("tr[data-field=name]").select("strong").get(0).text());
		eventBuilder.setImage("https:" + wrapper.select("img.event-image").attr("src"));
		eventBuilder.setDescription("UTC: "
				+ table.select("tr[data-field=english_start_date]").select("span.datetime").text().replace(" +0000", "")
				+ " - "
				+ table.select("tr[data-field=english_end_date]").select("span.datetime").text().replace(" +0000", ""));

		StringBuilder sb = new StringBuilder();
		sb.append("Event Type: **" + table.select("tr[data-field=type]").select("td").get(1).text() + "**");
		sb.append("\nAttribute: **"
				+ Emotes.getAttribute(table.select("tr[data-field=boost_attribute]").select("td").get(1).text())
				+ "**");

		sb.append("\nPref Band: **");
		for (Element aElement : table.select("tr[data-field=boost_members]").select("td").get(1).select("a"))
			sb.append(aElement.attr("data-ajax-title").split(" ")[0] + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("**");

		eventBuilder.addField(new Field("Event Information:", sb.toString(), false));
		output.add(eventBuilder.build());

		for (Element aElement : table.select("tr[data-field=cards]").select("td").get(1).select("a")) {
			EmbedBuilder cardBuilder = new EmbedBuilder();
			cardBuilder.setColor(Messages.colorMisc);
			cardBuilder.setAuthor("Event Card:");
			cardBuilder.setThumbnail("https:" + aElement.select("img").attr("src"));

			StringBuilder csb = new StringBuilder();
			String[] cardData = aElement.attr("data-ajax-title").split(" ");
			csb.append("Name: **" + cardData[1] + " " + cardData[2] + "**");
			csb.append("\nRarity: **" + cardData[0] + "**");
			csb.append("\nAttribute: **" + Emotes.getAttribute(cardData[4]) + "**");
			cardBuilder.addField(new Field(String.join(" ", Arrays.asList(cardData).subList(6, cardData.length)),
					csb.toString(), false));
			output.add(cardBuilder.build());
		}
		return output;
	}
}
