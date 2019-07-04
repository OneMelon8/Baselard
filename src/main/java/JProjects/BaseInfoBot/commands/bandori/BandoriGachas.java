package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.spider.bandori.BandoriEventSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BandoriGachas extends Command {

	public BandoriGachas(Bot bot) {
		super(bot, "gacha", new String[] { "gachas" }, "Shows the gachas that are now live and future gachas");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		MessageChannel ch = e.getChannel();
		if (args.length <= 1) {
			try {
				bot.sendMessage(getEventsEmbeded(BandoriEventSpider.queryEventList(true)), ch);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage(
						"Seems like I cannot get the information right now. Check your data and try again later.", ch);
			}
			return;
		}
		String eventName = String.join(" ", Arrays.asList(args).subList(1, args.length));
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
		builder.setColor(Messages.colorMisc);
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
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Bandori Gacha Query Template");
		builder.setDescription("Use the following template to run the Bandori event query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + " [name]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + command + " bushido```", false));
		return builder.build();
	}
}
