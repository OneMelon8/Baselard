package JProjects.BaseInfoBot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;

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
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatTop extends Command {

	public StatTop(Bot bot) {
		super(bot, "stattop", new String[] { "st", "stop", "statstop" },
				"Get the top suit in a class for a certain aspect");
	}

	private ArrayList<String> availableAspects = new ArrayList<String>(Arrays.asList("hp", "atk", "def", "acc", "eva",
			"cntr", "crt%", "crit", "acd", "stn", "frz", "sil", "rank"));

	@Override
	public void fire(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args.length != 3 && args.length != 4) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}
		String aspect = args[1].toLowerCase();
		SuitType type = SuitType.fromString(args[2].toLowerCase());

		boolean rankQuery = false;
		String suitName = null;
		if (!availableAspects.contains(aspect)) {
			bot.sendMessage(getAspectsEmbeded(), e.getChannel());
			return;
		}
		if (args[1].toLowerCase().equals("rank")) {
			rankQuery = true;
			try {
				Object[] objArr = SuitFileEditor.suggestSuit(args[2]);
				String suggestion = (String) objArr[0];
//				double sim = (Double) objArr[1];
//				if (sim < 0.6) {
//					bot.sendMessage(
//							e.getAuthor().getAsMention() + " Hmm, I don't recognise that suit. Do you mean by **"
//									+ Suit.rebuildName(suggestion).replace("_", " ") + "**?",
//							e.getChannel());
//					return;
//				}
				type = SuitFileEditor.getSuitType(suggestion.toLowerCase());
				suitName = suggestion;
			} catch (Exception ex) {
				bot.sendMessage(e.getAuthor().getAsMention()
						+ " Seems like I cannot get the statistics right now. Check your data and try again later.",
						e.getChannel());
				GeneralTools.logError(ex);
			}
		} else if (type == null) {
			bot.sendMessage(getTypeEmbeded(), e.getChannel());
			return;
		}

		User author = e.getAuthor();
		String name = author.getName();
		String id = author.getId();

		try {
			// Warning: this URL list has not been reformatted!!!
			JSONArray urlList = SuitFileEditor.getSuitsFromType(type.toString().toLowerCase());
			ArrayList<Suit> suitList = new ArrayList<Suit>();
			int level = 51;
			for (Object obj : urlList) {
				String url = Suit.rebuildName((String) obj);
				Suit s;
				s = CacheFileEditor.getSuit(url.toLowerCase(), author);
				if (s == null) {
					s = new Suit(name, id, Spider.query(url, type, Grade.US));
					CacheFileEditor.write(s);
				}
				s.levelChange(level);
				suitList.add(s);
			}

			if (rankQuery) {
				Suit target = null;
				for (Suit suit : suitList)
					if (suit.getCodeName().equalsIgnoreCase(suitName))
						target = suit;
				bot.sendMessage(getSuitRankingsEmbeded(suitList, target), e.getChannel());
				return;
			}

			int topCount = suitList.size();
			if (args.length == 4) {
				if (args[3].equalsIgnoreCase("all"))
					topCount = suitList.size();
				else {
					try {
						topCount = Integer.parseInt(args[3]);
					} catch (NumberFormatException ex) {
						bot.sendMessage("Invalid number of suits, defaulting the suit count to **ALL**",
								e.getChannel());
						topCount = suitList.size();
					}
				}
			}
			LinkedHashMap<Object, Double> topSuits = getRankedSuitsFromAspect(suitList, aspect);
			bot.sendMessage(getTopSuitsEmbeded(topSuits, aspect.toUpperCase(), type.toString(), level, topCount),
					e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(
					e.getAuthor().getAsMention()
							+ " Seems like I cannot get the statistics right now. Check your data and try again later.",
					e.getChannel());
			GeneralTools.logError(ex);
		}
	}

	private MessageEmbed getSuitRankingsEmbeded(ArrayList<Suit> suits, Suit suit) {
		String[] aspects = new String[] { "hp", "atk", "def", "acc", "eva", "cntr", "crt%", "crit", "acd", "stn", "frz",
				"sil" };
		StringBuilder sb = new StringBuilder("```java\n");
		int len = suits.size();
		for (String aspect : aspects) {
			LinkedHashMap<Object, Double> rankings = getRankedSuitsFromAspect(suits, aspect);
			ArrayList<Suit> keys = new ArrayList<Suit>();
			keys.addAll(Arrays.asList(rankings.keySet().toArray(new Suit[] {})));
			sb.append(String.format("%-6s", aspect.toUpperCase() + ": ") + "#"
					+ String.format("%02d", len - keys.indexOf(suit)) + " >> " + suit.getAspect(aspect) + "\n");
		}
		sb.append("```");

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Suit Aspects Ranking");
		builder.setDescription("The suits of " + suit.getSuitType().toString() + " class at Lv. " + suit.getLevel());
		builder.addField(new Field("Ranking of " + suit.getDisplayName() + " across all " + suits.size() + " "
				+ suit.getSuitType().toString() + " suits", sb.toString(), false));
		return builder.build();
	}

	private LinkedHashMap<Object, Double> getRankedSuitsFromAspect(ArrayList<Suit> suits, String aspect) {
		HashMap<Object, Double> aspects = new HashMap<Object, Double>();
		for (Suit s : suits)
			aspects.put(s, s.getAspect(aspect));
		LinkedHashMap<Object, Double> sortedSuits = GeneralTools.sortByValue(aspects);
		return sortedSuits;
	}

	public MessageEmbed getTopSuitsEmbeded(LinkedHashMap<Object, Double> suits, String aspect, String type, int level,
			final int count) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("List of Top Suits");
		builder.setDescription("The suits of " + type + " class at Lv. " + level);

		ArrayList<Suit> keys = new ArrayList<Suit>();
		keys.addAll(Arrays.asList(suits.keySet().toArray(new Suit[] {})));
		ArrayList<Double> vals = new ArrayList<Double>(suits.values());

		int len = keys.size();
		StringBuilder sb = new StringBuilder("```java\n");
		for (int a = 0; a < count; a++)
			sb.append("Rank #" + String.format("%02d", a + 1) + ": " + keys.get(len - 1 - a).getDisplayName() + " >> "
					+ GeneralTools.getNumeric(vals.get(len - 1 - a)) + "\n");
		sb.append("```");

		Field rankingField = new Field("Top " + count + " suits in all " + keys.size() + " " + type
				+ " suits ranked by " + aspect.toUpperCase() + " aspect (such rankings much wow)", sb.toString(),
				false);
		builder.addField(rankingField);
		return builder.build();
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Top Suit Statistics Query Template");
		builder.setDescription("Use the following template to run the top statistics query"
				+ "\nWarning: this command might take a while to perform");
		builder.addField(new Field("Copy & Paste:",
				"```" + Messages.prefix + "stattop <aspect> <class> [top # to display or \"all\"]" + "```", false));
		builder.addField(new Field("Available Aspects:",
				"```hp, atk, def, acc, eva, cntr, crt%, crit, acd, stn, frz, sil or rank```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.deleteCharAt(sb.length() - 1);
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:",
				"```" + Messages.prefix + "stattop atk sniper\n" + Messages.prefix + "stattop rank Zenka```", false));
		return builder.build();
	}

	public MessageEmbed getAspectsEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("List of Aspects");
		builder.setDescription("These are the only usable aspects in the query, CaSe is ignored");
		builder.addField(new Field("Copy & Paste:",
				"```hp, atk, def, acc, eva, cntr, crt%, crit, acd, stn, frz, sil, rank```", false));
		return builder.build();
	}

	public MessageEmbed getTypeEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("List of Suit Classes");
		builder.setDescription("These are the only usable suit classes in the query, CaSe is ignored");
		builder.addField(new Field("Copy & Paste:",
				"```Assault: ass, assault, assaults\nSupport: sup, supp, support, supports\nBombardier: bomb, bomber, bombers, bombardier, bombardiers"
						+ "\nSniper: sni, snip, sniper, snipers```",
				false));
		return builder.build();
	}

	public MessageEmbed getRankTypeEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Rank Query Template");
		builder.setDescription("Rank query is a bit different than stats query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "stattop rank <name>```", false));
		builder.addField(new Field("Example:", "```" + Messages.prefix + "stattop rank Atropos```", false));
		return builder.build();
	}
}
