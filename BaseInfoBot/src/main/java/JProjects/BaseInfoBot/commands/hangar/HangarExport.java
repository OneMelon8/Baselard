package JProjects.BaseInfoBot.commands.hangar;

import org.json.simple.JSONObject;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.Suit;
import JProjects.BaseInfoBot.database.files.HangarFileEditor;
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
		try {
			JSONObject obj = HangarFileEditor.read();
			if (!obj.containsKey(author.getId())) {
				bot.sendMessage(author.getAsMention() + " there's nothing to export!", e.getChannel());
				return;
			}
			Suit suit = Suit.fromString((String) obj.get(author.getId()));
			bot.sendMessage(buildExportEmbeded(author, suit.toString()), e.getChannel());
		} catch (Exception ex) {
			GeneralTools.logError(ex);
			bot.sendMessage(author.getAsMention() + " I cannot get your suit information right now, try again later.",
					e.getChannel());
		}
	}

	private static MessageEmbed buildExportEmbeded(User author, String msg) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Suit Export");
		builder.setDescription("Your suit data is exported successfully, you can import it any time with "
				+ Messages.prefix + "import");
		builder.addField(new Field("Copy & Paste:", "```" + msg + "```", false));
		return builder.build();
	}
}
