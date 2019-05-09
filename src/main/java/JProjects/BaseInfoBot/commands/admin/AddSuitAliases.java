package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.Suit;
import JProjects.BaseInfoBot.database.files.SuitAliasesFileEditor;
import JProjects.BaseInfoBot.database.files.SuitFileEditor;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AddSuitAliases extends Command {

	public AddSuitAliases(Bot bot) {
		super(bot, "alias", new String[] { "aliases" }, "Add an alias to a suit");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.reactCross(e.getMessage());
			return;
		}
		if (args.length != 3) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}

		String suitName = args[1];
		String suitAlias = args[2];
		if (!SuitFileEditor.isSuitValid(suitName)) {
			bot.sendMessage(author.getAsMention() + " The suit you entered is invalid. Check your data and try again.",
					e.getChannel());
			return;
		}
		try {
			SuitAliasesFileEditor.write(suitAlias, suitName);
			bot.sendMessage(author.getAsMention() + " Successfully added **" + suitAlias + "** as an aliase for **"
					+ Suit.rebuildNameReadable(suitName) + "**!", e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(
					author.getAsMention()
							+ " Seems like I cannot register the new alias right now. Check your data and try again.",
					e.getChannel());
			GeneralTools.logError(ex);
			return;
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Add Suit Aliases Template");
		builder.setDescription("Use the following template to add aliases to a suit (usable in queries)");
		builder.addField(
				new Field("Copy & Paste:", "```" + Messages.prefix + command + " <original> <alias>" + "```", false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + command + " Hellmaster_Blader HMB```", false));
		return builder.build();
	}
}
