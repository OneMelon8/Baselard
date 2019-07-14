package JProjects.BaseInfoBot.tools;

import java.awt.Color;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.spider.HttpRequester;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

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

}
