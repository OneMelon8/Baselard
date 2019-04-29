package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Version extends Command {

	public Version(Bot bot) {
		super(bot, "ver", new String[] { "version" }, "Check the bot version");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		bot.sendMessage("Base ver "+Bot.getVersion(), e.getChannel());
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Version Template");
		builder.setDescription("Use the following template to get my version");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "version" + "```", false));
		return builder.build();
	}
}
