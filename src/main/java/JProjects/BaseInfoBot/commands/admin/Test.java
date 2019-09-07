package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Test extends CommandHandler {

	public Test(BaseInfoBot bot) {
		super(bot, "test", "Developer testing stuff :p");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(message);
			return;
		}
		bot.sendThinkingPacket(channel);
		String msg = String.join(" ", args);
		if (msg.isEmpty())
			msg = "Ehehee, what are you trying to test?";
		bot.sendMessage(msg, channel);
		System.out.println(msg);
		bot.deleteMessage(message);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Test template");
		builder.setDescription("You sure you need a template for this?");
		builder.addField(
				new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " hello world" + "```", false));
		return builder.build();
	}
}
