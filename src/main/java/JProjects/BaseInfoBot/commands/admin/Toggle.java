package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Toggle extends Command {

	public Toggle(Bot bot) {
		super(bot, "toggle", "Administration toggle bot responses");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.reactCross(e.getMessage());
			return;
		}
		CommandDispatcher.mute = !CommandDispatcher.mute;
		bot.reactCheck(e.getMessage());
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Toggle Template");
		builder.setDescription("Use the following template to enable/disable the bot");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + "```", false));
		return builder.build();
	}
}
