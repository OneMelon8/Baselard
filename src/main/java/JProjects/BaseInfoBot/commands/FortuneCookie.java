package JProjects.BaseInfoBot.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.BotConfig;
import JProjects.BaseInfoBot.spider.HttpRequester;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class FortuneCookie extends Command {

	public FortuneCookie(BaseInfoBot bot) {
		super(bot, "cookie", new String[] { "fortune", "cookies" }, "Get a fortune cookie!");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendMessage("Let me get you a fortune cookie...", channel);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor(author.getName() + "'s Fortune Cookie");
		builder.setDescription("What will the cookie monster say to " + author.getName() + "?");

		StringBuilder sb = new StringBuilder("```");
		try {
			JsonObject json = new JsonParser().parse(HttpRequester.get("http://yerkee.com/api/fortune"))
					.getAsJsonObject();
			sb.append(json.get("fortune").getAsString().replaceAll("\\\n\"", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sb.length() == 3)
			sb.append("Happy! Lucky! Smile! Yay!");
		sb.append("```");
		builder.addField(new Field("There is a message inside the fortune cookie:", sb.toString(), false));
		bot.sendMessage(builder.build(), channel);
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
