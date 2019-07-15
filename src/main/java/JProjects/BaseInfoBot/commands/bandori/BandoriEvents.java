package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.spider.bandori.BandoriEventSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BandoriEvents extends Command {

	public BandoriEvents(BaseInfoBot bot) {
		super(bot, "event", new String[] { "ev" }, "Shows the events that are now live and future events");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		MessageChannel ch = e.getChannel();
		if (args.length <= 1) {
			try {
				bot.sendMessage(getEventsEmbeded(BandoriEventSpider.queryEventList(false)), ch);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage(
						"Seems like I cannot get the information right now. Check your data and try again later.", ch);
			}
			return;
		}

		String eventName = String.join(" ", Arrays.asList(args).subList(1, args.length)).trim().toLowerCase();
		// If query current
		if (eventName.equals("c") || eventName.equals("curr") || eventName.equals("current")) {
			try {
				eventName = BandoriEventSpider.queryEventList(true).get(0);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage(
						"Seems like I cannot get the information right now. Check your data and try again later.", ch);
			}
		} else if (eventName.equals("t") || eventName.equals("track") || eventName.equals("tracker")) {
			MessageEmbed msg = BandoriEventSpider.queryEventTracking();
			bot.sendMessage(msg, ch);
			return;
		}

		// Show details on that event
		try {
			bot.sendMessage(BandoriEventSpider.queryEvent(eventName), ch);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that event, maybe you spelled it wrong?", ch);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like I cannot get the information right now. Check your data and try again later.",
					ch);
		}
	}

	public MessageEmbed getEventsEmbeded(ArrayList<String> events) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
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
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Bandori Events Query Template");
		builder.setDescription("Use the following template to run the Bandori event query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " [name/c(urrent)]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.PREFIX + command + " bushido```", false));
		return builder.build();
	}
}
