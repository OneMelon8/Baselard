package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Help extends Command implements ReactionEvent {

	public Help(BaseInfoBot bot) {
		super(bot, "help", new String[] { "?" }, "List of commands and usage");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		MessageChannel ch = e.getChannel();
		if (args.length != 2) {
			// Send general help commands
			bot.sendMessage(getAllHelpEmbeded(), ch);
			return;
		}
		String sub = args[1];
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
			bot.sendMessage(e.getAuthor().getAsMention()
					+ " Hmm, I'm not sure what that means. Check out a list of available commands with **"
					+ Messages.PREFIX + "help**", e.getChannel());
			return;
		}
		bot.sendMessage(cmdHandler.getHelpEmbeded(), e.getChannel());
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message msg, MessageChannel channel) {
		// PRIORITY REACTION!!!
		bot.removeAllReactions(msg);
		bot.sendMessage(getAllHelpEmbeded(), channel);
	}

	public MessageEmbed getAllHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Commands List for Baselard");
		builder.setDescription("Here's how to use my command: `" + Messages.PREFIX + "<command> [arguments...]`");

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "help", "Show this message\n"));
		sb.append(String.format("%-8s >> %s", "ping", "Check my heartbeat\n"));
		sb.append(String.format("%-8s >> %s", "ver", "Check my version info\n"));
		sb.append("```");
		builder.addField(new Field("**General Commands:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "stat", "Display statistics for a suit\n"));
		sb.append(String.format("%-8s >> %s", "sc", "Compare the stats for two different suits\n"));
		sb.append(String.format("%-8s >> %s", "top", "List the top suits for certain stats\n"));
		sb.append(String.format("%-8s >> %s", "codex", "List all suits in the database\n"));
		sb.append("```");
		builder.addField(new Field("**Suit Statistics Commands:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "ev", "Display event list or a specific event\n"));
		sb.append(String.format("%-8s >> %s", "card", "Query for a random or specific card\n"));
		sb.append("```");
		builder.addField(new Field("**Bandori Commands:**", sb.toString(), false));

		builder.addField(
				new Field("", "For more information, check out `" + Messages.PREFIX + command + " [command]`", false));
		return builder.build();
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Help Template");
		builder.setDescription("Do you seriously need a template for this?");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		return builder.build();
	}
}
