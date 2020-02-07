package JProjects.BaseInfoBot.commands.fun;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class Fortune extends CommandHandler {

	public Fortune(BaseInfoBot bot) {
		super(bot, "cookie", new String[] { "fortune", "cookies" }, "Get a fortune cookie!");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor(author.getName() + "'s Fortune Cookie");

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(getCookie());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (sb.length() == 0)
			sb.append("Happy! Lucky! Smile! Yay!");
		builder.setDescription("*" + sb.toString() + "*");
		bot.sendMessage(builder.build(), channel);
	}

	private String getCookie() throws IOException {
		final String url = "http://www.fortunecookiemessage.com/";
		Document doc = Jsoup.connect(url).userAgent("Chrome").timeout(20 * 1000).get();
		Element wrapper = doc.getElementById("message").select("div.quote").first();
		return wrapper.text();
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Cookies Template");
		builder.setDescription("Use the following template to get a fortune cookie");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		return builder.build();
	}
}
