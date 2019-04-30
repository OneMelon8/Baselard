package JProjects.BaseInfoBot.commands.admin;

import java.util.HashMap;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.Suit;
import JProjects.BaseInfoBot.database.files.CacheFileEditor;
import JProjects.BaseInfoBot.database.files.HangarFileEditor;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RegisterDB extends Command {

	public RegisterDB(Bot bot) {
		super(bot, "aregister", new String[] { "areg", "aregist" }, "Register a suit into the **permanent** database");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		User author = e.getAuthor();
		if (!Bot.admins.contains(author.getId())) {
			bot.reactCross(e.getMessage());
			return;
		}
		if (args.length != 17) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}
		HashMap<String, String> info = new HashMap<String, String>();
		// Starts at 1 since 0 is the command
		// Parse the command into a map of data
		for (int a = 1; a < args.length; a++) {
			String[] data = args[a].split("=");
			info.put(data[0], data[1]);
		}
		try {
			Suit suit = new Suit(author.getName(), author.getId(), info);
			String suitStr = suit.toString();
			HangarFileEditor.write(author.getId(), suitStr);
			CacheFileEditor.writePerm(suit);
			bot.sendMessage("Successfully saved information into local database for 5 years!", e.getChannel());
			bot.sendMessage(suit.toEmbededMessage(true, author.getAvatarUrl()), e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(
					author.getAsMention()
							+ " It seems like one or more of your syntax is incorrect. Check your data and try again.",
					e.getChannel());
			GeneralTools.logError(ex);
			return;
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Suit Registration Template");
		builder.setDescription("Use the following template to register your suit and enter the data after \"=\"");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix
				+ "aregister type= name= grade= level= hp= atk= def= acc= eva= cntr= crt%= crit= stn= frz= sil= acd="
				+ "```", false));
		return builder.build();
	}
}
