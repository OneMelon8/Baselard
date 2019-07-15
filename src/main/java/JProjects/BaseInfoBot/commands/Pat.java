package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Pat extends Command {

	public Pat(BaseInfoBot bot) {
		super(bot, "pat", new String[] { "headpat", "patpat" }, "Pat base");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.sendMessage("Fuwa fwah~", channel);
			return;
		}
		bot.sendMessage("Ah! pats from mwaster! Fwaaaah~", channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Pat Template");
		builder.setDescription("Use the following template to pat");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		return builder.build();
	}
}
