package JProjects.BaseInfoBot.commands.hangar;

import org.json.simple.JSONObject;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.database.Suit;
import JProjects.BaseInfoBot.database.files.HangarFileEditor;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HangarDisplay {

	public static void fire(MessageReceivedEvent e, Bot bot) {
		User author = e.getAuthor();
		try {
			JSONObject obj = HangarFileEditor.read();
			if (!obj.containsKey(author.getId())) {
				bot.sendMessage(author.getAsMention() + " you have no suits in the hangar!", e.getChannel());
				return;
			}
			Suit suit = Suit.fromString((String) obj.get(author.getId()));
			bot.sendMessage(suit.toEmbededMessage(false, null), e.getChannel());
		} catch (Exception ex) {
			GeneralTools.logError(ex);
			bot.sendMessage(author.getAsMention() + " I cannot get your suit information right now, try again later.",
					e.getChannel());
		}
	}
}
