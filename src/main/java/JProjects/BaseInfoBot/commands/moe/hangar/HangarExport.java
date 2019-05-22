package JProjects.BaseInfoBot.commands.moe.hangar;

import org.json.simple.JSONObject;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.HangarFileEditor;
import JProjects.BaseInfoBot.database.moe.MoeSuit;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HangarExport {
	/*
	 * TODO: get user name with ID!!!
	 */

	public static void fire(MessageReceivedEvent e, Bot bot) {
		User author = e.getAuthor();
		String[] args = e.getMessage().getContentRaw().split(" ");
		try {
			JSONObject obj = HangarFileEditor.read();
			if (!obj.containsKey(author.getId())) {
				bot.sendMessage(author.getAsMention() + " there's nothing to export!", e.getChannel());
				return;
			}
			MoeSuit suit = MoeSuit.fromString((String) obj.get(author.getId()));

			// Wiki export
			if (args.length == 3 && args[2].equals("wiki")) {
				bot.sendMessage(buildWikiExportEmbeded(WikiExportFormatter.format(suit)), e.getChannel());
				return;
			}
			bot.sendMessage(buildExportEmbeded(suit.toString()), e.getChannel());
		} catch (Exception ex) {
			GeneralTools.logError(ex);
			bot.sendMessage(author.getAsMention() + " I cannot get your suit information right now, try again later.",
					e.getChannel());
		}
	}

	private static MessageEmbed buildExportEmbeded(String msg) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Suit Export");
		builder.setDescription("Your suit data is exported successfully, you can import it any time with "
				+ Messages.prefix + "import");
		builder.addField(new Field("Copy & Paste:", "```" + msg + "```", false));
		return builder.build();
	}

	private static MessageEmbed buildWikiExportEmbeded(String msg) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Suit Export - Wiki Format");
		builder.setDescription("Your suit data is exported successfully as WIKI format (such export much wiki)");
		builder.addField(new Field("Copy & Paste:", "```" + msg + "```", false));
		return builder.build();
	}
}
