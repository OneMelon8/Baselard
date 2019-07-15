package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Toggle extends Command {

	public Toggle(BaseInfoBot bot) {
		super(bot, "toggle", "Administration toggle bot responses");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(message);
			return;
		}
		CommandDispatcher.mute = !CommandDispatcher.mute;
		bot.reactCheck(message);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Toggle Template");
		builder.setDescription("Use the following template to enable/disable the bot");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		return builder.build();
	}
}
