package JProjects.BaseInfoBot.commands.moe;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.CacheFileEditor;
import JProjects.BaseInfoBot.database.files.SuitFileEditor;
import JProjects.BaseInfoBot.database.moe.MoeGrade;
import JProjects.BaseInfoBot.database.moe.MoeSuit;
import JProjects.BaseInfoBot.database.moe.MoeSuitType;
import JProjects.BaseInfoBot.spider.MoeSpider;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MoeCodex extends Command {

	public MoeCodex(BaseInfoBot bot) {
		super(bot, "codex", new String[] { "cdx" }, "Get all suits");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		User author = e.getAuthor();
		String name = author.getName();
		String id = author.getId();
		try {
			// Warning: this URL list has not been reformatted!!!
			MoeSuitType[] types = new MoeSuitType[] { MoeSuitType.ASSAULT, MoeSuitType.SUPPORT, MoeSuitType.BOMBARDIER,
					MoeSuitType.SNIPER };
			HashMap<ArrayList<MoeSuit>, MoeSuitType> suitList = new HashMap<ArrayList<MoeSuit>, MoeSuitType>();
			for (MoeSuitType type : types) {
				JSONArray urlList = SuitFileEditor.getSuitsFromType(type.toString().toLowerCase());
				ArrayList<MoeSuit> typeSuits = new ArrayList<MoeSuit>();
				int level = 51;
				for (Object obj : urlList) {
					String url = MoeSuit.rebuildName((String) obj);
					MoeSuit s;
					s = CacheFileEditor.getSuit(url.toLowerCase(), author);
					if (s == null) {
						s = new MoeSuit(name, id, MoeSpider.query(url, type, MoeGrade.US));
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
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Codex Query Template");
		builder.setDescription("Use the following template to view the MOE codex");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		return builder.build();
	}

	public MessageEmbed getCodexEmbeded(HashMap<ArrayList<MoeSuit>, MoeSuitType> suitList) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Codex Results");
		builder.setDescription("Here is a list of all MOE suits in the database!");
		for (ArrayList<MoeSuit> typeSuits : suitList.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (MoeSuit suit : typeSuits)
				sb.append(suit.getDisplayName() + ", ");
			if (sb.length() > 2)
				sb.delete(sb.length() - 2, sb.length());
			builder.addField(
					new Field(suitList.get(typeSuits).toString() + " Class:", "```" + sb.toString() + "```", false));
		}
		return builder.build();
	}
}
