package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.spider.bandori.BandoriCardSpider;
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
				bot.sendMessage(BandoriCardSpider.queryRandom().getEmbededMessage(), ch);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage(
						"Seems like I cannot get the information right now. Check your data and try again later.", ch);
			}
			return;
		}
		String query = String.join(" ", Arrays.asList(args).subList(1, args.length)).trim();
		if (query.length() < 5) {
			bot.sendMessage("Hey " + e.getAuthor().getAsMention()
					+ ", I need more information! Search terms should be at least 5 characters long!", ch);
			return;
		}

		bot.sendMessage("Okay~ I'm searching for \"" + query + "\" right now. It might take a while though >w<", ch);
		try {
			List<BandoriCard> cards = BandoriCardSpider.queryCard(query);
			if (cards.isEmpty())
				throw new IndexOutOfBoundsException("No results");
			bot.sendMessage("I found **" + cards.size() + "** results matching your search:", ch);
			for (BandoriCard card : cards)
				bot.sendMessage(card.getEmbededMessage(), ch);
			bot.sendMessage("That's all for \"" + query + "\"!", ch);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that card, maybe you spelled it wrong?", ch);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like I cannot get the information right now. Check your data and try again later.",
					ch);
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Bandori Card Query Template");
		builder.setDescription("Use the following template to run the Bandori card query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + command + " [search...]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + command + " (shows a random card)\n"
				+ Messages.prefix + command + " detective kokoro```", false));
		return builder.build();
	}
}
