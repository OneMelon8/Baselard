package JProjects.BaseInfoBot.commands;

import java.util.HashMap;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Grade;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.Suit;
import JProjects.BaseInfoBot.database.SuitType;
import JProjects.BaseInfoBot.database.files.CacheFileEditor;
import JProjects.BaseInfoBot.database.files.SuitFileEditor;
import JProjects.BaseInfoBot.spider.Spider;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SuitStats extends Command {

	public SuitStats(Bot bot) {
		super(bot, "suitstat", new String[] { "ss", "stst", "suitstats" }, "Query for suit statistics");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args.length != 2 && args.length != 3) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}
		String suitName = args[1].toLowerCase();

		try {
			Object[] objArr = SuitFileEditor.suggestSuit(suitName);
			String url = (String) objArr[0];
			double sim = (Double) objArr[1];
			if (sim < 0.6) {
				bot.sendMessage(e.getAuthor().getAsMention() + " Hmm, I don't recognise that suit. Do you mean by **"
						+ Suit.rebuildName(url).replace("_", " ") + "**?", e.getChannel());
				return;
			}
			SuitType type = SuitFileEditor.getSuitType(url.toLowerCase());
			if (type == null)
				type = SuitType.ASSAULT;
			url = Suit.rebuildName(url);
			Suit suit;

			int level = 41;
			if (args.length == 3) {
				try {
					level = Integer.parseInt(args[2]);
					if (level != 1 && level != 31 && level != 41 && level != 51)
						throw new NumberFormatException("Incorrect leveling!");
				} catch (NumberFormatException ex) {
					level = 1;
					bot.sendMessage("Invalid level, defaulting the suit statistics to US Lv.1", e.getChannel());
				}
			}

			String cache = url.toLowerCase();
			suit = CacheFileEditor.getSuit(cache);
			if (suit == null) {
				HashMap<String, String> results = Spider.query(url, type, Grade.US);
				suit = new Suit(e.getAuthor().getName(), e.getAuthor().getId(), results);
				suit.levelChange(level);
				CacheFileEditor.write(suit.clone());
			} else
				suit.levelChange(level);

			bot.sendMessage(suit.toEmbededMessage(false, null), e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(e.getAuthor().getAsMention()
					+ " Seems like I cannot get the statistics right now. (I can only get the statistics of suits whose grade are US or higher)",
					e.getChannel());
			GeneralTools.logError(ex);
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Suit Statistics Query Template");
		builder.setDescription("Use the following template to run the suit query and potentially save the suit");
		builder.addField(new Field("Copy & Paste:",
				"```" + Messages.prefix + "suitstats <name> [lv = 1/31/41/51]" + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.deleteCharAt(sb.length() - 1);
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + "suitstat Zenka 31```", false));
		return builder.build();
	}
}
