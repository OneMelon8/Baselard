package JProjects.BaseInfoBot.commands.admin;

import java.util.List;

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

public class Servers extends Command {

	public Servers(BaseInfoBot bot) {
		super(bot, "servers", "Show the list of servers that has the bot");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(message);
			return;
		}
		List<Guild> servers = bot.getJDA().getGuilds();
		StringBuilder sb = new StringBuilder("Servers: ");
		for (Guild server : servers)
			sb.append(server.getName() + ", ");
		sb.delete(sb.length() - 2, sb.length());
		bot.sendMessage(sb.toString(), channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Servers Template");
		builder.setDescription("Use the following template to see the list of servers");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		return builder.build();
	}
}
