package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

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
//		TextChannel chn = bot.getJDA().getTextChannelById("617346677908439088");
//		chn.getManager().setName("tk-o'-lantern").complete();
//		System.out.println("Complete!!");
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
