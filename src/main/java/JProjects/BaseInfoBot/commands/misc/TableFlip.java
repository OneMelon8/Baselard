package JProjects.BaseInfoBot.commands.misc;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class TableFlip extends Command {

	public TableFlip(BaseInfoBot bot) {
		super(bot, "tableflip", "Express your anger by flipping the table (╯°□°）╯︵ ┻━┻");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel) {
		bot.sendMessage("(╯°□°）╯︵ ┻━┻", channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Tableflip Template");
		builder.setDescription("Use the following template to flip a table");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		return builder.build();
	}
}
