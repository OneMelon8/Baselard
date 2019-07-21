package JProjects.BaseInfoBot.commands;

import java.util.Random;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.BotConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Cookies extends Command {

	private static final String[] responses = new String[] { "Thanks for the cookies!!", "Nom num nom...",
			"My mwaster has been taking good care of me, I'm glad to be back!", "Happy! Lucky! Smile! Cookies!",
			"Hmph! These aren't home made! Just kidding~ Thanks for the cookies!" };
	private static Random r = new Random();

	public Cookies(BaseInfoBot bot) {
		super(bot, "cookie", new String[] { "cookies" }, "Get a fortune cookie!");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendMessage(author.getAsMention() + " " + responses[r.nextInt(responses.length)], channel);
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
