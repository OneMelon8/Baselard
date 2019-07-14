package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Pat extends Command {

	public Pat(BaseInfoBot bot) {
		super(bot, "pat", new String[] { "headpat", "patpat" }, "Pat base");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		User author = e.getAuthor();
		MessageChannel ch = e.getChannel();
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.sendMessage("Fwah fwah~", ch);
			return;
		}
		bot.sendMessage("Ah! pats from mwaster! Fwaaaah~", ch);
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
