package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Lookup extends Command {

	public Lookup(BaseInfoBot bot) {
		super(bot, "lookup", new String[] { "who", "whois" }, "Look up a user by Discord ID");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length != 1) {
			// Send general help commands
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
		String id = args[0];
		User result = bot.getJDA().getUserById(id);
		if (result == null) {
			bot.sendMessage("No users found!", channel);
			return;
		}
		bot.sendMessage("Found " + result.getAsMention(), channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Help Template");
		builder.setDescription("Use the following template to lookup a user");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " <Discord ID>```", false));
		return builder.build();
	}
}
