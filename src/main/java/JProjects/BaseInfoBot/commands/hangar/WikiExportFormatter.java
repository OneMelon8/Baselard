package JProjects.BaseInfoBot.commands.hangar;

import java.util.ArrayList;
import java.util.Arrays;

import JProjects.BaseInfoBot.database.Suit;

public class WikiExportFormatter {

	private static String[] wikiFormatGlobal = new String[] { "| CRT% = ", "| CRIT = ", "| CNTR = ", "| STNΩ = ",
			"| FRZΩ = ", "| SILΩ = ", "| ACDΩ = " };
	private static String[] wikiFormatLv1 = new String[] { "<!-- Rank US -->\n| HP1 = ", "| MP1 = 100\n| ATK1 = ",
			"| DEF1 = ", "| ACC1 = ", "| EVA1 = ", "| HP30 = ", "| MP30 = 100\n| ATK30 = ", "| DEF30 = ", "| ACC30 = ",
			"| EVA30 = " };
	private static String[] wikiFormatLv31 = new String[] { "<!-- Rank US+1 -->\n| HP31 = ", "| MP31 = 100\n| ATK31 = ",
			"| DEF31 = ", "| ACC31 = ", "| EVA31 = ", "| HP40 = ", "| MP40 = 100\n| ATK40 = ", "| DEF40 = ",
			"| ACC40 = ", "| EVA40 = " };
	private static String[] wikiFormatLv41 = new String[] { "<!-- Rank US+2 -->\n| HP41 = ", "| MP41 = 100\n| ATK41 = ",
			"| DEF41 = ", "| ACC41 = ", "| EVA41 = ", "| HP50 = ", "| MP50 = 100\n| ATK50 = ", "| DEF50 = ",
			"| ACC50 = ", "| EVA50 = " };
	private static String[] wikiFormatLv51 = new String[] { "<!-- Rank US+3 -->\n| HP51 = ", "| MP51 = \n| ATK51 = ",
			"| DEF51 = ", "| ACC51 = ", "| EVA51 = ", "| HP60 = ", "| MP60 = \n| ATK60 = ", "| DEF60 = ", "| ACC60 = ",
			"| EVA60 = " };

	public static String format(Suit suit) {
		StringBuilder builder = new StringBuilder();

		// Global Information
		double[] globalInfo = new double[] { suit.getCriticalChance(), suit.getCriticalDamage(),
				suit.getCounterChance(), suit.getStunResistance(), suit.getFreezeResistance(),
				suit.getSilenceResistance(), suit.getAcidResistance() };
		for (int a = 0; a < globalInfo.length; a++)
			builder.append(wikiFormatGlobal[a] + globalInfo[a] + "\n");

		int[] levels = new int[] { 1, 31, 41, 51 };
		ArrayList<String[]> formats = new ArrayList<String[]>(
				Arrays.asList(wikiFormatLv1, wikiFormatLv31, wikiFormatLv41, wikiFormatLv51));
		for (int a = 0; a < levels.length; a++) {
			suit.levelChange(levels[a]);
			int[] lvInfo = new int[] { suit.getHP(), suit.getAttack(), suit.getDefense(), suit.getAccuracy(),
					suit.getEvasion() };
			int len = lvInfo.length;
			String[] format = formats.get(a);
			for (int b = 0; b < format.length; b++)
				builder.append(format[b] + (b < len ? lvInfo[b] : "") + "\n");
		}
		builder.append("<!-- Exported by Baselard -->");
		return builder.toString();
	}
}
