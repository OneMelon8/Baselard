package JProjects.BaseInfoBot.tools;

import java.awt.Color;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.spider.HttpRequester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class EmbededUtil {

	public static MessageEmbed getThinkingEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.YELLOW);
		builder.setAuthor("Suit people are surfing the interweb...");
		builder.setDescription("Please standby while the data is being queried from the internet");

		String info = Emotes.KOKORO_TSURUMAKI + " Misaki, where's Michelle?\n" + Emotes.MISAKI_OKUSAWA
				+ " Somewhere in this world. In fact, right in front of you.";
		try {
			JsonObject json = new JsonParser()
					.parse(HttpRequester.get("https://official-joke-api.appspot.com/random_joke")).getAsJsonObject();
			info = Emotes.KOKORO_TSURUMAKI + " " + json.get("setup").getAsString() + "\n" + Emotes.MISAKI_OKUSAWA + " "
					+ json.get("punchline").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		builder.addField(new Field("While you wait:", info, false));
		return builder.build();
	}

	public static MessageEmbed getErrorEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("Communication with the suits has been interrupted... ");
		builder.setDescription("An unexpected error has occured, please try again later or contact @One");

		StringBuilder sb = new StringBuilder("```");
		sb.append("[10:30] We saw a wild Michelle in the woods!!" + "\n[11:00] We have lost track of the Michelle!"
				+ "\n[11:30] We found some footsteps, it's Michelle's!"
				+ "\n[12:00] We found the Michelle! But where are we?");
		sb.append("```");
		builder.addField(new Field("Last known information:", sb.toString(), false));
		return builder.build();
	}

	public static MessageEmbed getErrorEmbeded(Exception ex) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("Communication with the suits has been interrupted... ");
		builder.setDescription("An unexpected error has occured, please try again later or contact @One");

		StringBuilder sb = new StringBuilder("```");
		sb.append(ex.getClass().getName() + "\n" + ex.getMessage());
		sb.append("```");
		builder.addField(new Field("Last known information:", sb.toString(), false));
		return builder.build();
	}

	public static MessageEmbed getErrorEmbeded(String errorMessage) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("Communication with the suits has been interrupted");
		builder.setDescription("An unexpected error has occured, please try again later or contact @One");
		builder.addField(new Field("Last known information:", "```" + errorMessage + "```", false));
		return builder.build();
	}

	public static MessageEmbed getNotSupportedEmbeded(String errorMessage) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("The suits' firmware need an upgrade");
		builder.setDescription("This operation is currently not supported, contact @One for further information");
		builder.addField(new Field("Information:", "```" + errorMessage + "```", false));
		return builder.build();
	}

}
