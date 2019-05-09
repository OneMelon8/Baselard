package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Ping extends Command {

	public Ping(Bot bot) {
		super(bot, "ping", "Check if the bot is currently active");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		bot.sendMessage(Emotes.RODY_BEAT + " Heartbeat " + Math.round(e.getJDA().getPing()) + "ms", e.getChannel());
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Ping Template");
		builder.setDescription("Use the following template to check my heartbeat");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + "```", false));
		return builder.build();
	}
}
