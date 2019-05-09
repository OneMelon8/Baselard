package JProjects.BaseInfoBot.commands.fun;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TableFlip extends Command {

	public TableFlip(Bot bot) {
		super(bot, "tableflip", "Express your anger by flipping the table (╯°□°）╯︵ ┻━┻");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		bot.sendMessage("(╯°□°）╯︵ ┻━┻", e.getChannel());
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Tableflip Template");
		builder.setDescription("Use the following template to flip a table");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + "```", false));
		return builder.build();
	}
}
