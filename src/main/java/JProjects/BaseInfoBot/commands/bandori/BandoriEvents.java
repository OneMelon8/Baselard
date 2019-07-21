package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.ArrayList;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.BotConfig;
import JProjects.BaseInfoBot.spider.bandori.BandoriEventSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class BandoriEvents extends Command {

	public BandoriEvents(BaseInfoBot bot) {
		super(bot, "event", new String[] { "ev" }, "Shows the events that are now live and future events");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length == 0) {
			try {
				bot.sendMessage(getEventsEmbeded(BandoriEventSpider.queryEventList(false)), channel);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
			}
			return;
		}

		String eventName = String.join(" ", args).trim().toLowerCase();
		// If query current
		if (eventName.equals("c") || eventName.equals("curr") || eventName.equals("current")) {
			try {
				eventName = BandoriEventSpider.queryEventList(true).get(0);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
			}
		} else if (eventName.equals("t") || eventName.equals("track") || eventName.equals("tracker")) {
			MessageEmbed msg = BandoriEventSpider.queryEventTracking();
			bot.sendMessage(msg, channel);
			return;
		}

		// Show details on that event
		try {
			bot.sendMessage(BandoriEventSpider.queryEvent(eventName), channel);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that event, maybe you spelled it wrong?", channel);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
		}
	}

	public MessageEmbed getEventsEmbeded(ArrayList<String> events) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("All Events in Chronological Order");
		builder.setDescription("Global Server Event Count: " + events.size());
		for (int index = 0; index < events.size(); index += 20) {
			int max = index + 20 > events.size() ? events.size() : index + 20;
			StringBuilder sb = new StringBuilder("```json\n");
			for (int a = index; a < max; a++)
				sb.append("\"" + events.get(a) + "\", ");
			sb.delete(sb.length() - 2, sb.length());
			sb.append("```");
			builder.addField(new Field("Events " + (index + 1) + "-" + (max + 1) + ":", sb.toString(), false));
		}
		return builder.build();
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Events Query Template");
		builder.setDescription("Use the following template to run the Bandori event query");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " [name/c(urrent)]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " bushido```", false));
		return builder.build();
	}
}
