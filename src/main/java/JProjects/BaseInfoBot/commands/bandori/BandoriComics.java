package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.spider.bandori.BandoriComicSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BandoriComics extends Command {

	public BandoriComics(BaseInfoBot bot) {
		super(bot, "comic", new String[] { "comics" }, "Searches for a random comic in the database");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		MessageChannel ch = e.getChannel();
		try {
			bot.sendMessage(BandoriComicSpider.queryRandom(), ch);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like I cannot get the information right now. Check your data and try again later.",
					ch);
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Bandori Comic Query Template");
		builder.setDescription("Use the following template to run the Bandori comic query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.PREFIX + command + " (shows random comic)```", false));
		return builder.build();
	}
}
