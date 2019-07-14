package JProjects.BaseInfoBot.commands.moe;

import java.util.HashMap;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.CacheFileEditor;
import JProjects.BaseInfoBot.database.files.HangarFileEditor;
import JProjects.BaseInfoBot.database.files.SuitFileEditor;
import JProjects.BaseInfoBot.database.moe.MoeGrade;
import JProjects.BaseInfoBot.database.moe.MoeSuit;
import JProjects.BaseInfoBot.database.moe.MoeSuitType;
import JProjects.BaseInfoBot.spider.MoeSpider;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MoeSuitStats extends Command {

	public MoeSuitStats(BaseInfoBot bot) {
		super(bot, "stats", new String[] { "su", "st", "suit", "stat", "stats" }, "Query for suit statistics");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args.length != 2 && args.length != 3 && args.length != 4) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}
		String suitName = args[1].toLowerCase();

		try {
			Object[] objArr = SuitFileEditor.suggestSuit(suitName);
			String url = (String) objArr[0];
//			double sim = (Double) objArr[1];
//			if (sim < 0.6) {
//				bot.sendMessage(e.getAuthor().getAsMention() + " Hmm, I don't recognise that suit. Do you mean by **"
//						+ Suit.rebuildName(url).replace("_", " ") + "**?", e.getChannel());
//				return;
//			}
			MoeSuitType type = SuitFileEditor.getSuitType(url.toLowerCase());
			if (type == null)
				type = MoeSuitType.ASSAULT;
			url = MoeSuit.rebuildName(url);
			MoeSuit suit;

			int level = 51;
			if (args.length >= 3) {
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
			suit = CacheFileEditor.getSuit(cache, e.getAuthor());
			if (suit == null) {
				HashMap<String, String> results = MoeSpider.query(url, type, MoeGrade.US);
				suit = new MoeSuit(e.getAuthor().getName(), e.getAuthor().getId(), results);
				suit.levelChange(level);
				CacheFileEditor.write(suit.clone());
			} else
				suit.levelChange(level);

			bot.sendMessage(suit.toEmbededMessage(false, null), e.getChannel());

			if (args.length == 4 && args[3].equalsIgnoreCase("y")) {
				// Save suit
				HangarFileEditor.write(e.getAuthor().getId(), suit.toString());
				bot.sendMessage(e.getAuthor().getAsMention() + " Successfully saved the suit in the hangar!",
						e.getChannel());
			}
		} catch (Exception ex) {
			bot.sendMessage(e.getAuthor().getAsMention()
					+ " Seems like I cannot get the statistics right now. (I can only get the statistics of suits whose grade are US or higher)",
					e.getChannel());
			GeneralTools.logError(ex);
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Suit Statistics Query Template");
		builder.setDescription("Use the following template to run the suit query and potentially save the suit");
		builder.addField(new Field("Copy & Paste:",
				"```" + Messages.PREFIX + command + " <name> [lv = 1/31/41/51] [save? Y/N]" + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.PREFIX + command + " Zenka 31 y```", false));
		return builder.build();
	}
}
