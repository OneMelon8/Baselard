package JProjects.BaseInfoBot.commands.fun;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.files.FileEditor;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

@SuppressWarnings("unchecked")
public class Pat extends Command implements ReactionEvent {
	private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

	private static Random r = new Random();

	private static LinkedHashMap<Object, Long> patRanking;
	long totalPats = 0;

	private static final String[] responses = new String[] { "Hehe~ Fuwa fwah~", "Hehe~ Thanks!",
			"Happy! Lucky! Smile! Yay! Thanks for the pat!", " " + Emotes.KOKORON_SPARKLE };

	public Pat(BaseInfoBot bot) {
		super(bot, "pat", new String[] { "pats", "headpat", "patpat" }, "Pat base");
	}

	private static final String fileName = "pats";

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		String id = author.getId();
		boolean isAdmin = bot.isAdmin(author);
		if (args.length == 0) {
			pat(author, id, channel, guild);
			return;
		}

		List<Member> mentioned = message.getMentionedMembers();
		String subCommand = args[0];
		if (subCommand.equals("r") || subCommand.equals("rank") || subCommand.equals("ranking")
				|| subCommand.equals("rankings"))
			patRank(id, 0, null, channel, guild);
		else if (subCommand.equals("export") && isAdmin)
			export(channel, message);
		else if (subCommand.equals("import") && isAdmin)
			try {
				FileEditor.write(fileName, String.join("", Arrays.copyOfRange(args, 1, args.length)));
				bot.sendMessage("Successfully imported pat data!", channel);
			} catch (IOException e) {
				e.printStackTrace();
				bot.reactError(message);
			} catch (ParseException e) {
				e.printStackTrace();
				bot.reactError(message);
			}
		else if (subCommand.equals("set") && mentioned.size() >= 1 && args.length == 3 && isAdmin)
			setPats(mentioned.get(0), args[2], guild.getId(), message, channel);
		else if (subCommand.equals("calc") && isAdmin)
			calcTotalPats(channel);
		else if (subCommand.equals("merge") && isAdmin)
			mergePats(String.join("", Arrays.copyOfRange(args, 1, args.length)), channel);
		else
			pat(author, id, channel, guild);
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
			return;
		bot.removeAllReactions(message);

		String emoteName = emote.getName();
		MessageEmbed msgEmbeded = message.getEmbeds().get(0);
		String[] info = msgEmbeded.getFooter().getText().split(" of ");
		int page = Integer.parseInt(info[0].replace("Page ", "")) - 1;
		if (emoteName.equals("▶"))
			patRank(guild.getId(), page + 1, message, channel, guild);
		else if (emoteName.equals("◀"))
			patRank(guild.getId(), page - 1, message, channel, guild);
	}

	private void setPats(Member user, String count, String serverId, Message message, MessageChannel channel) {
		JSONObject patData = null;
		try {
			patData = FileEditor.read(fileName);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Check console: IOException READ", channel);
		} catch (ParseException ex) {
			ex.printStackTrace();
			bot.sendMessage("Check console: ParseException READ", channel);
		}
		if (patData == null)
			patData = new JSONObject();

		long totalPats = patData.containsKey("total_pats") ? Long.valueOf(patData.get("total_pats").toString()) : 0;
		JSONObject serversPatData = patData.containsKey("server_pats") ? (JSONObject) patData.get("server_pats")
				: new JSONObject();
		JSONObject usersPatData = serversPatData.containsKey(serverId) ? (JSONObject) serversPatData.get(serverId)
				: new JSONObject();
		long userPats = Long.parseLong(count);

		usersPatData.put(user.getUser().getId(), userPats);
		serversPatData.put(serverId, usersPatData);
		patData.put("server_pats", serversPatData);
		patData.put("total_pats", totalPats + userPats);

		try {
			FileEditor.write(fileName, patData);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Check console: IOException WRITE", channel);
		}
		bot.sendMessage("Successfully set " + user.getUser().getName() + "'s pats to " + userPats + "!", channel);
		bot.deleteMessage(message);
	}

	public static void export() {
		try {
			System.out.println(FileEditor.read(fileName).toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void export(MessageChannel channel, Message message) {
		try {
			bot.sendMessage("Pats data sent to console", channel);
			System.out.println(FileEditor.read(fileName).toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
			bot.reactError(message);
		} catch (ParseException e) {
			e.printStackTrace();
			bot.reactError(message);
		}
	}

	private void patRank(String id, int page, Message message, MessageChannel channel, Guild guild) {
		String serverId = guild.getId();
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
		totalPats = Long.valueOf(patData.get("total_pats").toString());
		JSONObject serversPatData = (JSONObject) patData.get("server_pats");
		if (serversPatData == null || !serversPatData.containsKey(serverId)) {
			bot.sendMessage("Nobody has patted me yet " + Emotes.KOKORON_WUT_3, channel);
			return;
		}
		JSONObject usersPatData = (JSONObject) serversPatData.get(serverId);
		patRanking = GeneralTools.sortByValueLong(usersPatData);

		String[] userIds = patRanking.keySet().toArray(new String[] {});
		Long[] userPats = patRanking.values().toArray(new Long[] {});

		if (page < 0)
			page = (int) Math.ceil(patRanking.size() / 10);
		int startIndex = page * 10;
		if (startIndex > patRanking.size()) {
			page = 0;
			startIndex = 0;
		}
		StringBuilder sb = new StringBuilder("```");
		for (int a = startIndex; a < startIndex + 10; a++) {
			if (a >= patRanking.size())
				break;
			if (a > 0)
				sb.append("\n");
			User user = bot.getJDA().getUserById(userIds[a]);
			sb.append("#" + String.format("%02d", a + 1) + ": " + (user == null ? "Unknown" : user.getName()) + " - "
					+ userPats[a]);
		}
		sb.append("```");

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Patting Statistics");
		builder.setDescription("Who patted me the most? Use /pat to pat me!");
		builder.addField(new Field("I got a total of " + totalPats + " pats!", sb.toString(), false));
		builder.setFooter("Page " + (page + 1) + " of " + (int) (Math.ceil(patRanking.size() / 10) + 1),
				"https://cdn.discordapp.com/emojis/578806302864572427.png?v=1");
		Message msg;
		if (message == null)
			msg = bot.sendMessage(builder.build(), channel);
		else
			msg = bot.editMessage(message, builder.build());

		bot.reactPrev(msg);
		bot.reactNext(msg);

		EmoteDispatcher.register(msg, this, "◀", "▶");
		EmoteDispatcher.registerCleanUp(msg);
	}

	private void pat(User author, String id, MessageChannel channel, Guild guild) {
		String serverId = guild.getId();
		long msLeft = cooldown.containsKey(serverId) ? cooldown.get(serverId) - System.currentTimeMillis() : 0;
		if (msLeft > 0) {
			bot.sendMessage("Hey " + author.getAsMention() + ", pat me again in "
					+ TimeFormatter.getCountDownApproxToMinutes(msLeft), channel);
			return;
		}
		int cooldownTime = 5 * 60 * 1000 + r.nextInt(25 * 60 * 1000); // 5-60 minutes
		if (r.nextInt(10) == 0) {
			bot.sendMessage("Saaya: Thanks for the pat~ " + Emotes.SAAYA_MELT, channel);
			bot.sendMessage("Where's my pat? " + Emotes.KOKORON_WUT_3, channel);
			return;
		}
		cooldown.put(serverId, System.currentTimeMillis() + cooldownTime);

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
		JSONObject serversPatData = patData.containsKey("server_pats") ? (JSONObject) patData.get("server_pats")
				: new JSONObject();
		JSONObject usersPatData = serversPatData.containsKey(serverId) ? (JSONObject) serversPatData.get(serverId)
				: new JSONObject();
		long userPats = usersPatData.containsKey(id) ? Long.valueOf(usersPatData.get(id).toString()) : 0;

		usersPatData.put(id, userPats + 1);
		serversPatData.put(serverId, usersPatData);
		patData.put("server_pats", serversPatData);
		patData.put("total_pats", totalPats + 1);
		try {
			FileEditor.write(fileName, patData);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		bot.sendMessage(author.getAsMention() + " " + responses[r.nextInt(responses.length)], channel);
		if (r.nextInt(20) == 0) {
			if (author.getId().equals(BotConfig.ONE_ID)) {
				bot.sendMessage("\\*kisses " + author.getAsMention() + "\\*", channel);
			} else
				bot.sendMessage("\\*pats " + author.getAsMention() + " on the head too\\*", channel);
		}
	}

	private void mergePats(String jsonStr, MessageChannel channel) {
		JSONObject patData1 = null, patData2 = null, patDataTotal = new JSONObject();
		try {
			patData1 = FileEditor.read(fileName);
			patData2 = (JSONObject) new JSONParser().parse(jsonStr);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		if (patData1 == null)
			patData1 = new JSONObject();
		if (patData2 == null)
			patData2 = new JSONObject();

		// Server pats
		JSONObject serversPatData1 = patData1.containsKey("server_pats") ? (JSONObject) patData1.get("server_pats")
				: new JSONObject();
		JSONObject serversPatData2 = patData2.containsKey("server_pats") ? (JSONObject) patData2.get("server_pats")
				: new JSONObject();
		JSONObject serversPatDataTotal = new JSONObject();
		for (Object serverId : serversPatData1.keySet()) {
			JSONObject usersPatData1 = serversPatData1.containsKey(serverId)
					? (JSONObject) serversPatData1.get(serverId)
					: new JSONObject();
			JSONObject usersPatData2 = serversPatData2.containsKey(serverId)
					? (JSONObject) serversPatData2.get(serverId)
					: new JSONObject();
			for (Object userId : usersPatData2.keySet()) {
				if (usersPatData1.containsKey(userId))
					usersPatData1.put(userId, Long.parseLong(String.valueOf(usersPatData1.get(userId)))
							+ Long.parseLong(String.valueOf(usersPatData2.get(userId))));
				else
					usersPatData1.put(userId, usersPatData2.get(userId));
			}
			serversPatDataTotal.put(serverId, usersPatData1);
		}
		patDataTotal.put("server_pats", serversPatDataTotal);

		try {
			FileEditor.write(fileName, patDataTotal);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		calcTotalPats(null);
		bot.sendMessage("Success!", channel);
	}

	private void calcTotalPats(MessageChannel channel) {
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

		long totalPats = 0;
		JSONObject serversPatData = patData.containsKey("server_pats") ? (JSONObject) patData.get("server_pats")
				: new JSONObject();
		for (Object serverId : serversPatData.keySet()) {
			JSONObject usersPatData = (JSONObject) serversPatData.get(serverId);
			for (Object userId : usersPatData.keySet())
				totalPats += Long.parseLong(String.valueOf(usersPatData.get(userId)));
		}

		patData.put("total_pats", totalPats);
		try {
			FileEditor.write(fileName, patData);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (channel == null)
			return;
		bot.sendMessage("Success!", channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Pat Template");
		builder.setDescription("Use the following template to pat me =w=");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		return builder.build();
	}
}
