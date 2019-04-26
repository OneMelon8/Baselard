package JProjects.BaseInfoBot.commands;

import java.util.ArrayList;
import java.util.HashMap;

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

public class Codex extends Command {

	public Codex(Bot bot) {
		super(bot, "codex", new String[] { "cdx" }, "Get all suits");
	}

	@Override
	public void fire(MessageReceivedEvent e) {
		User author = e.getAuthor();
		String name = author.getName();
		String id = author.getId();
		bot.sendMessage("Okay, hold on a while, I'm getting a lot of suits~", e.getChannel());
		try {
			// Warning: this URL list has not been reformatted!!!
			SuitType[] types = new SuitType[] { SuitType.ASSAULT, SuitType.SUPPORT, SuitType.BOMBARDIER,
					SuitType.SNIPER };
			HashMap<ArrayList<Suit>, SuitType> suitList = new HashMap<>();
			for (SuitType type : types) {
				JSONArray urlList = SuitFileEditor.getSuitsFromType(type.toString().toLowerCase());
				ArrayList<Suit> typeSuits = new ArrayList<>();
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
					typeSuits.add(s);
				}
				suitList.put(typeSuits, type);
			}
			bot.sendMessage(getCodexEmbeded(suitList), e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(e.getAuthor().getAsMention() + " Seems like I cannot get the codex right now.",
					e.getChannel());
			GeneralTools.logError(ex);
		}
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Codex Query Template");
		builder.setDescription("Use the following template to view the MOE codex");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "codex```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.deleteCharAt(sb.length() - 1);
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		return builder.build();
	}

	public MessageEmbed getCodexEmbeded(HashMap<ArrayList<Suit>, SuitType> suitList) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Codex Results");
		builder.setDescription("Here is a list of all MOE suits in the database!");
		for (ArrayList<Suit> typeSuits : suitList.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (Suit suit : typeSuits)
				sb.append(suit.getDisplayName() + ", ");
			if (sb.length() > 2)
				sb.delete(sb.length() - 2, sb.length());
			builder.addField(
					new Field(suitList.get(typeSuits).toString() + " Class:", "```" + sb.toString() + "```", false));
		}
		return builder.build();
	}
}
