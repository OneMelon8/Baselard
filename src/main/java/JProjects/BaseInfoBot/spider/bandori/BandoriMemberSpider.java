package JProjects.BaseInfoBot.spider.bandori;

import java.awt.Color;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.bandori.BandoriMember;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriMemberSpider {

	private static final String masterUrl = "https://bandori.party";
	private static final String querySearchUrl = "/members/?search=";

	public static MessageEmbed queryMember(String search, JDA jda) throws IOException {
		Document doc = Jsoup.connect(masterUrl + querySearchUrl + search.replace(" ", "+")).userAgent("Chrome")
				.timeout(60 * 1000).get();
		doc = Jsoup
				.connect(masterUrl + doc.select("div.collection-page-wrapper.as-container").get(0)
						.select("div.row.items").select("a").get(0).attr("href"))
				.userAgent("Chrome").timeout(60 * 1000).get();
		Element wrapper = doc.select("div.container.item-container").get(0);
		Element table = doc.select("table.table.about-table").get(0);

		EmbedBuilder builder = new EmbedBuilder();
		String colorStr = table.select("tr[data-field=color]").select("td").get(1).select("span.text-muted").text()
				.replace("#", "");
		builder.setColor(new Color(Integer.valueOf(colorStr.substring(0, 2), 16),
				Integer.valueOf(colorStr.substring(2, 4), 16), Integer.valueOf(colorStr.substring(4, 6), 16)));
		builder.setAuthor(table.select("tr[data-field=name]").text().replace("Name", "").trim());
		builder.setImage("https:" + wrapper.select("div.text-center.top-item").get(0).select("img").attr("src"));
		builder.setDescription("CV: " + table.select("tr[data-field=romaji_CV]").select("td").get(1).text().trim()
				+ " (" + table.select("tr[data-field=CV]").select("td").get(1).text().trim() + ")");

		Emote icon = jda.getEmoteById(BandoriMember.valueOf(table.select("tr[data-field=name]").select("td").get(1)
				.select("span").text().trim().toUpperCase().replace(" ", "_")).getEmote().replaceAll("[^\\d.]", ""));
		builder.setThumbnail(icon.getImageUrl());

		StringBuilder sb = new StringBuilder();
		String bandStr = table.select("tr[data-field=band]").select("td").get(1).select("a").attr("data-ajax-title")
				.trim();
		if (bandStr.equals("Poppin&#39;Party"))
			bandStr = "Poppin'Party";
		sb.append("Band: **" + Emotes.getBandEmote(bandStr) + " " + bandStr + "**\n");
		sb.append("School: **"
				+ table.select("tr[data-field=school]").select("td").get(1).text().replace("&#39;", "'").trim()
				+ "**\n");
		sb.append("Year: **" + table.select("tr[data-field=school_year]").select("td").get(1).text().trim() + "**\n");
		sb.append("Birthday: **" + table.select("tr[data-field=birthday]").select("td").get(1).text().trim() + "**\n");
		sb.append("Astro. Sign: **" + table.select("tr[data-field=astrological_sign]").select("td").get(1).text().trim()
				+ "**\n");
		sb.append("Height: **" + table.select("tr[data-field=height]").select("td").get(1).text().trim() + "**\n");
		sb.append(
				"Liked Food: **" + table.select("tr[data-field=food_like]").select("td").get(1).text().trim() + "**\n");
		sb.append("Disliked Food: **" + table.select("tr[data-field=food_dislike]").select("td").get(1).text().trim()
				+ "**\n");
		sb.append("Instrument: **" + table.select("tr[data-field=instrument]").select("td").get(1).text().trim()
				+ "**\n");
		sb.append("Hobbies: **" + table.select("tr[data-field=hobbies]").select("td").get(1).text().trim() + "**");
		builder.addField(new Field("**About Member:**", sb.toString(), false));

		sb = new StringBuilder();
		sb.append(
				table.select("tr[data-field=description]").select("td").get(1).select("div").text().toString().trim());
		builder.addField(new Field("**Description:**", sb.toString(), false));
		return builder.build();
	}
}
