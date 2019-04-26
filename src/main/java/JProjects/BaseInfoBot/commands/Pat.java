package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Pat extends Command {

	public Pat(Bot bot) {
		super(bot, "pat", new String[] { "headpat" }, "Check if the bot is currently active");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		User author = e.getAuthor();
		MessageChannel ch = e.getChannel();
		if (!Bot.admins.contains(author.getId())) {
			bot.sendMessage("Fwah fwah~ Zzz", ch);
			return;
		}
		bot.sendMessage("Ah! pats from mwaster! " + Emotes.rodyBeat, ch);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Ping Template");
		builder.setDescription("Use the following template to check my heartbeat");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "ping" + "```", false));
		return builder.build();
	}
}
