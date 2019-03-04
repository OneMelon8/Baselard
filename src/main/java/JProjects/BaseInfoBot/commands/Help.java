package JProjects.BaseInfoBot.commands;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Help extends Command {

	public Help(Bot bot) {
		super(bot, "help", new String[] { "?" }, "List of commands and usage");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
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
					+ Messages.prefix + "help**", e.getChannel());
			return;
		}
		bot.sendMessage(cmdHandler.getHelpEmbeded(), e.getChannel());
	}

	public MessageEmbed getAllHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Commands List for Baselard"); // v1.0 Beta
		builder.setDescription("Here's how to use my command: `" + Messages.prefix + "<command> [arguments...]`");

		StringBuilder general = new StringBuilder("```");
		general.append(String.format("%-8s >> %s", "help", "Show this message\n"));
		general.append(String.format("%-8s >> %s", "ping", "Check my heartbeat\n"));
		general.append("```");
		builder.addField(new Field("General Commands:", general.toString(), false));

		StringBuilder stats = new StringBuilder("```");
		stats.append(String.format("%-8s >> %s", "suitstat", "Display statistics for a suit\n"));
		stats.append(String.format("%-8s >> %s", "suitcomp", "Compare the stats for two different suits\n"));
		stats.append(String.format("%-8s >> %s", "stattop", "List the top suits for certain stats\n"));
		stats.append("```");
		builder.addField(new Field("Suit Statistics Command:", stats.toString(), false));
		builder.addField(
				new Field("", "For more information, check out `" + Messages.prefix + "help [command]`", false));
		return builder.build();
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Help Template");
		builder.setDescription("Do you seriously need a template for this?");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "help" + "```", false));
		return builder.build();
	}
}
