package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.spider.bandori.BandoriCardSpider;
import JProjects.BaseInfoBot.spider.bandori.BandoriEventSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BandoriCards extends Command {

	public BandoriCards(Bot bot) {
		super(bot, "card", new String[] { "cards" }, "Search for cards on bandori.party");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		MessageChannel ch = e.getChannel();
		if (args.length <= 1) {
			try {
				bot.sendMessage(getCardsEmbeded(BandoriEventSpider.queryEventList()), ch);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage(
						"Seems like I cannot get the information right now. Check your data and try again later.", ch);
			}
			return;
		}
		String query = String.join(" ", Arrays.asList(args).subList(1, args.length));
		try {
			bot.sendMessage(BandoriCardSpider.queryCard(query).getEmbededMessage(), ch);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot found information on that card, maybe you spelled it wrong?", ch);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like I cannot get the information right now. Check your data and try again later.",
					ch);
		}
	}

	public MessageEmbed getCardsEmbeded(ArrayList<String> events) {
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
		builder.setAuthor("Bandori Card Query Template");
		builder.setDescription("Use the following template to run the Bandori event query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + " [search...]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.deleteCharAt(sb.length() - 1);
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + command + " detective kokoro```", false));
		return builder.build();
	}
}
