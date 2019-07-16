package JProjects.BaseInfoBot.commands;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.files.FileEditor;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

@SuppressWarnings("unchecked")
public class Pat extends Command {
	private static long cooldown = 0;

	public Pat(BaseInfoBot bot) {
		super(bot, "pat", new String[] { "pats", "headpat", "patpat" }, "Pat base");
	}

	private static final String fileName = "pats";

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel) {
		String id = author.getId();
		if (args.length == 0) {
			pat(id, channel);
			return;
		}

		String subCommand = args[0];
		if (subCommand.equals("r") || subCommand.equals("rank") || subCommand.equals("ranking")
				|| subCommand.equals("rankings"))
			patRank(id, channel);
		else
			pat(id, channel);
	}

	private void patRank(String id, MessageChannel channel) {
		JSONObject patData = null;
		try {
			patData = FileEditor.read(fileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		if (patData == null || !patData.containsKey("total_pats")) {
			bot.sendMessage("Nobody has patted me yet " + Emotes.KOKORON_WUT_3, channel);
			return;
		}
		long totalPats = Long.valueOf(patData.get("total_pats").toString());
		JSONObject userPatData = (JSONObject) patData.get("user_pats");
		LinkedHashMap<Object, Long> topPats = GeneralTools.sortByValueLong(userPatData);
		String[] userIds = topPats.keySet().toArray(new String[] {});
		Long[] userPats = topPats.values().toArray(new Long[] {});

		StringBuilder sb = new StringBuilder("```");
		for (int a = 0; a <= 10; a++) {
			if (a >= topPats.size())
				break;
			if (a > 0)
				sb.append("\n");
			sb.append("#" + (a + 1) + ": " + bot.getJDA().getUserById(userIds[a]).getName() + " - " + userPats[a]);
		}
		sb.append("```");

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Patting Statistics");
		builder.setDescription("Who patted me the most? Use /pat to pat me!");
		builder.addField(new Field("I got a total of " + totalPats + " pats!", sb.toString(), false));
		bot.sendMessage(builder.build(), channel);
	}

	private void pat(String id, MessageChannel channel) {
		id = "548973434437107751";
		int cooldownTime = 20 * 60 * 1000; // 30 minutes
		long msLeft = cooldown + cooldownTime - System.currentTimeMillis();
		if (msLeft > 0) {
			bot.sendMessage("[Auto Reply] Kokoro is currently not here, she'll be back in "
					+ TimeFormatter.getCountDownSimple(msLeft), channel);
			return;
		}
		cooldown = System.currentTimeMillis();

		JSONObject patData = null;
		try {
			patData = FileEditor.read(fileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		if (patData == null)
			patData = new JSONObject();

		long totalPats = patData.containsKey("total_pats") ? Long.valueOf(patData.get("total_pats").toString()) : 0;
		JSONObject userPatData = patData.containsKey("user_pats") ? (JSONObject) patData.get("user_pats")
				: new JSONObject();
		long userPats = userPatData.containsKey(id) ? Long.valueOf(userPatData.get(id).toString()) : 0;

		userPatData.put(id, userPats + 1);
		patData.put("user_pats", userPatData);
		patData.put("total_pats", totalPats + 1);
		try {
			FileEditor.write(fileName, patData);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		bot.sendMessage("Fuwa fwah~", channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Pat Template");
		builder.setDescription("Use the following template to pat");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + "```", false));
		return builder.build();
	}
}
