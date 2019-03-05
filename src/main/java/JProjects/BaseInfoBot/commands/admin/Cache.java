package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Cache extends Command {

	public Cache(Bot bot) {
		super(bot, "cache", "Cache main command");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.sendMessage(e.getAuthor().getAsMention() + " permission denied :x:", e.getChannel());
			return;
		}

		try {
			args.getClass();
		} catch (Exception ex) {
			bot.sendMessage(
					author.getAsMention()
							+ " It seems like one or more of your syntax is incorrect. Check your data and try again.",
					e.getChannel());
			GeneralTools.logError(ex);
			return;
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Cache Template");
		builder.setDescription("Use the following template to register your suit and enter the data after \"=\"");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "cache [view/clear]" + "```", false));
		return builder.build();
	}
}
