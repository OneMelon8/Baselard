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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriGachaSpider {

	private static final String masterUrl = "https://bandori.party";
//	private static final String queryCurrentUrl = "/gachas/?version=EN&status=current&ordering=start_date";
	private static final String querySearchUrl = "/gachas/?version=EN&search=";
	private static final String queryAllUrl = "/gachas/?version=EN&ordering=start_date";

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
		Element wrapper = doc.select("div.container.item-container").get(0);
		Element table = wrapper.select("table.table.about-table").get(0);

		EmbedBuilder gachaBuilder = new EmbedBuilder();
		gachaBuilder.setColor(BotConfig.COLOR_MISC);
		gachaBuilder.setAuthor(table.select("tr[data-field=name]").text().replace("Title", "").trim());
		gachaBuilder
				.setImage("https:" + wrapper.select("tr.english_image").select("td").get(0).select("img").attr("src"));
		gachaBuilder.setDescription("UTC: "
				+ table.select("tr[data-field=english_start_date]").select("span.datetime").text().replace(" +0000", "")
				+ " - "
				+ table.select("tr[data-field=english_end_date]").select("span.datetime").text().replace(" +0000", ""));

		StringBuilder sb = new StringBuilder();
		sb.append("Event Type: **" + table.select("tr[data-field=limited]").select("td").get(1).text() + "**");
		sb.append("\nAttribute: **" + BandoriAttribute
				.fromString(table.select("tr[data-field=boost_attribute]").select("td").get(1).text()).getEmote()
				+ "**");

		sb.append("\nPref Band: **");
		for (Element aElement : table.select("tr[data-field=boost_members]").select("td").get(1).select("a"))
			sb.append(aElement.attr("data-ajax-title").split(" ")[0] + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("**");

		gachaBuilder.addField(new Field("Event Information:", sb.toString(), false));
		output.add(gachaBuilder.build());

		for (Element aElement : table.select("tr[data-field=cards]").select("td").get(1).select("a")) {
			EmbedBuilder cardBuilder = new EmbedBuilder();
			cardBuilder.setColor(BotConfig.COLOR_MISC);
			cardBuilder.setAuthor("Event Card:");
			cardBuilder.setThumbnail("https:" + aElement.select("img").attr("src"));

			StringBuilder csb = new StringBuilder();
			String[] cardData = aElement.attr("data-ajax-title").split(" ");
			csb.append("Name: **" + cardData[1] + " " + cardData[2] + "**");
			csb.append("\nRarity: **" + cardData[0] + "**");
			csb.append("\nAttribute: **" + BandoriAttribute.fromString(cardData[4]).getEmote() + "**");
			cardBuilder.addField(new Field(String.join(" ", Arrays.asList(cardData).subList(6, cardData.length)),
					csb.toString(), false));
			output.add(cardBuilder.build());
		}
		return output;
	}

}
