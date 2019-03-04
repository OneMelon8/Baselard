package JProjects.BaseInfoBot.commands.hangar;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Hangar extends Command {

	public Hangar(Bot bot) {
		super(bot, "hangar", "Your very own mini hangar!");
	}

	// hangar = view suit
	// hangar <import/export/view/clear/enhance>

	@Override
	public void fire(MessageReceivedEvent e) {
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.sendMessage(
					e.getAuthor().getAsMention()
							+ " permission denied :x: (This is beta feature, it will be released soon)",
					e.getChannel());
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args.length == 1
				|| (args.length == 2 && (args[1].equalsIgnoreCase("view") || args[1].equalsIgnoreCase("display")))) {
			// Display suit
			HangarDisplay.fire(e, bot);
		} else if (args.length >= 2 && args[1].equalsIgnoreCase("import")) {
			// Import
			HangarImport.fire(e, bot);
		} else if (args.length >= 2 && args[1].equalsIgnoreCase("export")) {
			// Export
			HangarExport.fire(e, bot);
		} else {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
		}
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Hangar Template");
		builder.setDescription("Use the following template to navigate in your hangar! (such hangar much wow)");
		builder.addField(new Field("Copy & Paste:",
				"```" + Messages.prefix + "hangar [import/export/view] [data]" + "```", false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + "hangar export" + "```", false));
		return builder.build();
	}
}
