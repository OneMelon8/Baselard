package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

public class Help extends Command implements ReactionEvent {

	public Help(BaseInfoBot bot) {
		super(bot, "help", new String[] { "?" }, "List of commands and usage");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		if (args.length != 1) {
			// Send general help commands
			bot.sendMessage(getAllHelpEmbeded(), channel);
			return;
		}
		String sub = args[0];
		Command cmdHandler = CommandDispatcher.registeredListeners.get(sub);
		if (cmdHandler == null)
			for (String key : CommandDispatcher.registeredCommands.keySet()) {
				String[] lstAliases = CommandDispatcher.registeredCommands.get(key);
				for (String aliase : lstAliases)
					if (sub.equalsIgnoreCase(aliase))
						sub = key;
			}
		cmdHandler = CommandDispatcher.registeredListeners.get(sub);
		if (cmdHandler == null) {
			bot.sendMessage(author.getAsMention()
					+ " Hmm, I'm not sure what that means. Check out a list of available commands with **"
					+ BotConfig.PREFIX + "help**", channel);
			return;
		}
		bot.sendMessage(cmdHandler.getHelpEmbeded(), channel);
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message msg, MessageChannel channel, Guild guild) {
		bot.removeAllReactions(msg);
		bot.sendMessage(getAllHelpEmbeded(), channel);
	}

	public MessageEmbed getAllHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Commands List for Kokoro");
		builder.setDescription("Here's how to use my command: `" + BotConfig.PREFIX + "<command> [arguments...]`");

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "help", "Show this message\n"));
		sb.append(String.format("%-8s >> %s", "ping", "Check my heartbeat\n"));
		sb.append(String.format("%-8s >> %s", "ver", "Check my version info\n"));
		sb.append("```");
		builder.addField(new Field("**General Commands:**", sb.toString(), false));

//		sb = new StringBuilder("```");
//		sb.append(String.format("%-8s >> %s", "stat", "Display statistics for a suit\n"));
//		sb.append(String.format("%-8s >> %s", "sc", "Compare the stats for two different suits\n"));
//		sb.append(String.format("%-8s >> %s", "top", "List the top suits for certain stats\n"));
//		sb.append(String.format("%-8s >> %s", "codex", "List all suits in the database\n"));
//		sb.append("```");
//		builder.addField(new Field("**Suit Statistics Commands:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "event", "Show event list or a specific event\n"));
		sb.append(String.format("%-8s >> %s", "card", "Search for a specific (or random) card\n"));
		sb.append(String.format("%-8s >> %s", "member", "Show member list or a specific member\n"));
		sb.append(String.format("%-8s >> %s", "multi", "Show help for the multi-live command\n"));
		sb.append("```");
		builder.addField(new Field("**Bandori Commands:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "pat", "Give me some head pats =w=\n"));
		sb.append(String.format("%-8s >> %s", "fortune", "Get a fortune cookie\n"));
		sb.append("```");
		builder.addField(new Field("**Fun Commands:**", sb.toString(), false));

		builder.addField(
				new Field("", "For more information, check out `" + BotConfig.PREFIX + command + " [command]`", false));
		return builder.build();
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Help Template");
		builder.setDescription("Do you seriously need a template for this?");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		return builder.build();
	}
}
