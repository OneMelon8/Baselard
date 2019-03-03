package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Test extends Command {

	public Test(Bot bot) {
		super(bot, "test", "Developer testing stuff :p");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.sendMessage(e.getAuthor().getAsMention() + " permission denied :x:", e.getChannel());
			return;
		}
		bot.sendMessage(e.getMessage().getContentRaw().replace(Messages.prefix + "test ", ""), e.getChannel());
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Test template");
		builder.setDescription("You sure you need a template for this?");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "test hello world" + "```", false));
		return builder.build();
	}
}
