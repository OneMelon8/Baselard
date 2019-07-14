package JProjects.BaseInfoBot.commands.moe;

import java.io.IOException;
import java.util.HashMap;

import org.json.simple.parser.ParseException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.CacheFileEditor;
import JProjects.BaseInfoBot.database.files.SuitFileEditor;
import JProjects.BaseInfoBot.database.moe.MoeGrade;
import JProjects.BaseInfoBot.database.moe.MoeSuit;
import JProjects.BaseInfoBot.spider.MoeSpider;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MoeSuitComp extends Command {

	public MoeSuitComp(BaseInfoBot bot) {
		super(bot, "sc", new String[] { "comp", "compare", "suitcomp" }, "Compare two different suits' statstics");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args.length != 3 && args.length != 4) {
			bot.sendMessage(getHelpEmbeded(), e.getChannel());
			return;
		}
		String suit1Name = args[1].toLowerCase();
		String suit2Name = args[2].toLowerCase();

		try {
			String url1 = suggestName(suit1Name), url2 = suggestName(suit2Name);
			if (url1 == null || url2 == null) {
				bot.sendMessage(e.getAuthor().getAsMention()
						+ " Hmm, I don't recognise that suit. Check your data and try again", e.getChannel());
				return;
			}

			MoeSuit suit1, suit2;
			suit1 = CacheFileEditor.getSuit(url1.toLowerCase(), e.getAuthor());
			suit2 = CacheFileEditor.getSuit(url2.toLowerCase(), e.getAuthor());

			if (suit1 == null) {
				HashMap<String, String> resultsS1 = MoeSpider.query(url1, SuitFileEditor.getSuitType(url1.toLowerCase()),
						MoeGrade.US);
				suit1 = new MoeSuit(e.getAuthor().getName(), e.getAuthor().getId(), resultsS1);
				CacheFileEditor.write(suit1);
			}
			if (suit2 == null) {
				HashMap<String, String> resultsS2 = MoeSpider.query(url2, SuitFileEditor.getSuitType(url2.toLowerCase()),
						MoeGrade.US);
				suit2 = new MoeSuit(e.getAuthor().getName(), e.getAuthor().getId(), resultsS2);
				CacheFileEditor.write(suit2);
			}
			bot.sendMessage(getsuitcompEmbeded(suit1, suit2), e.getChannel());
		} catch (Exception ex) {
			bot.sendMessage(
					e.getAuthor().getAsMention()
							+ " Seems like I cannot get the statistics right now. Check your data and try again later.",
					e.getChannel());
			GeneralTools.logError(ex);
		}

	}

	private String suggestName(String suitName) throws IOException, ParseException {
		Object[] objArr = SuitFileEditor.suggestSuit(suitName);
		String suggestion = (String) objArr[0];
//		double sim = (Double) objArr[1];
//		if (sim < 0.6)
//			return null;
		return suggestion;
	}

	private MessageEmbed getsuitcompEmbeded(MoeSuit suit1, MoeSuit suit2) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Suit Comparison: " + suit1.getName() + " vs " + suit2.getName());
		final int level = 51;
		builder.setDescription(
				"Comparison based on both suits' statistics at " + MoeGrade.getUsGradeFromString(level) + " Lv. " + level);
		suit1.levelChange(level);
		suit2.levelChange(level);
		Field suit1Field = new Field(
				suit1.getName() + "'s Perspective: Firepower = " + GeneralTools.round(suit1.getExpectedDamage(), 2),
				suit1.makeComparisonTable(suit2), false);
		builder.addField(suit1Field);
		Field suit2Field = new Field(
				suit2.getName() + "'s Perspective: Firepower = " + GeneralTools.round(suit2.getExpectedDamage(), 2),
				suit2.makeComparisonTable(suit1), false);
		builder.addField(suit2Field);
		return builder.build();
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Suit Statistics Comparison Query Template");
		builder.setDescription("Use the following template to run the suit comparison query");
		builder.addField(
				new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " <name> <name2>" + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.PREFIX + command + " Pygma Zenka```", false));
		Field edNote = new Field("Notes about Expected Damage:",
				"This is a value that calculates the raw DPS of the suit"
						+ "\n```ED = ATK × CRT% × CRIT + ATK × (1-CRT%)```",
				false);
		builder.addField(edNote);
		return builder.build();
	}
}
