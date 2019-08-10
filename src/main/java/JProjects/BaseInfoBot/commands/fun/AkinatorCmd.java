package JProjects.BaseInfoBot.commands.fun;

import java.io.IOException;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.Akinator;
import JProjects.BaseInfoBot.database.config.AkinatorConfig;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.EmbededUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

public class AkinatorCmd extends Command implements ReactionEvent {

	private static Akinator akinator;
	private static Message message;
	private static long lastActiveTimestamp = 0;

	public AkinatorCmd(BaseInfoBot bot) {
		super(bot, "akinator", new String[] { "aki" }, "Play with the famous Akinator!");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (akinator != null && akinator.isActive()) {
			bot.reactWait(message);
			return;
		}
		bot.sendThinkingPacket(channel);
		try {
			akinator = new Akinator(author);
			lastActiveTimestamp = System.currentTimeMillis();

			AkinatorCmd.message = bot.sendMessage(akinator.getQuestionEmbeded(), channel);
			bot.addReaction(AkinatorCmd.message, "1⃣", "2⃣", "3⃣", "4⃣", "5⃣");

			EmoteDispatcher.register(AkinatorCmd.message, this, "1⃣", "2⃣", "3⃣", "4⃣", "5⃣");
			EmoteDispatcher.registerCleanUp(AkinatorCmd.message);
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage(EmbededUtil.getErrorEmbeded("The Genie is not available now, try again later"), channel);
		}
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message msg, MessageChannel channel, Guild guild) {
		if (!akinator.isActive())
			return;
		if (msg.getEmbeds() == null || msg.getEmbeds().size() == 0)
			return;
		bot.removeAllReactions(msg);
		lastActiveTimestamp = System.currentTimeMillis();
		String emoteName = emote.getName();
		// 1⃣ 2⃣ 3⃣ 4⃣ 5⃣ ❌ ✔
		int answer = -1;
		if (emoteName.equals("1⃣"))
			answer = 0;
		else if (emoteName.equals("2⃣"))
			answer = 1;
		else if (emoteName.equals("3⃣"))
			answer = 2;
		else if (emoteName.equals("4⃣"))
			answer = 3;
		else if (emoteName.equals("5⃣"))
			answer = 4;
		else if (emoteName.equals("❌"))
			answer = 5;
		else if (emoteName.equals("✔"))
			answer = 6;
		else
			return;

		try {
			message = bot.editMessage(msg, akinator.getAnswerEmbeded(answer));

			MessageEmbed embeded = akinator.next(answer);
			message = bot.editMessage(message, embeded);

			if (akinator.isActive())
				if (akinator.isGuessing()) {
					bot.addReaction(message, "❌", "✔");
					EmoteDispatcher.register(message, this, "❌", "✔");
				} else {
					bot.addReaction(message, "1⃣", "2⃣", "3⃣", "4⃣", "5⃣");
					EmoteDispatcher.register(message, this, "1⃣", "2⃣", "3⃣", "4⃣", "5⃣");
				}

			EmoteDispatcher.registerCleanUp(message);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.editMessage(msg, EmbededUtil.getErrorEmbeded(ex));
		}
	}

	public static void timerTick() {
		if (akinator == null || !akinator.isActive())
			return;
		if (lastActiveTimestamp + AkinatorConfig.MAX_IDLE_TIME > System.currentTimeMillis())
			return;
		akinator.setActive(false);
		App.bot.editMessage(message, akinator.getTimeOutEmbeded());
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Akinator Template");
		builder.setDescription("Use the following template to play a game with the Akinator");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		return builder.build();
	}
}
