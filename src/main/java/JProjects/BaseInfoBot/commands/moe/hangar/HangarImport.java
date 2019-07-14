package JProjects.BaseInfoBot.commands.moe.hangar;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.HangarFileEditor;
import JProjects.BaseInfoBot.database.moe.MoeSuit;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HangarImport {

	public static void fire(MessageReceivedEvent e, BaseInfoBot bot) {
		User author = e.getAuthor();
		String message = e.getMessage().getContentRaw().replace(Messages.PREFIX + "hangar import {data}", "");
		if (message == null || message.isEmpty()) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}
		try {
			MoeSuit suit = MoeSuit.fromString(message);
			if (!suit.getOwnerId().equals(author.getId()))
				suit.changeOwner(author);
			HangarFileEditor.write(author.getId(), suit.toString());
			bot.sendMessage(suit.toEmbededMessage(true, author.getAvatarUrl()), e.getChannel());
		} catch (Exception ex) {
			GeneralTools.logError(ex);
			bot.sendMessage(author.getAsMention()
					+ " It seems like the import string is invalid. The import string is the message that is exported when using **"
					+ Messages.PREFIX + "export**", e.getChannel());
		}
	}

	public static MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Suit Import Template");
		builder.setDescription("Use the following template to import your suit");
		builder.addField(
				new Field("Copy & Paste:", "```" + Messages.PREFIX + "import <exported string>" + "```", false));
		return builder.build();
	}
}
