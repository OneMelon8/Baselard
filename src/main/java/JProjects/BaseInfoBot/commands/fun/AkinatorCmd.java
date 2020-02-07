package JProjects.BaseInfoBot.commands.fun;

import java.io.IOException;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.commands.helpers.ReactionDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionHandler;
import JProjects.BaseInfoBot.database.Akinator;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.config.AkinatorConfig;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.EmbededUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

public class AkinatorCmd extends CommandHandler implements ReactionHandler {

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
			if (args.length == 2 && args[0].equals("show")) {
				String messageId = args[1];
				Message msg = channel.retrieveMessageById(messageId).complete();
				if (!msg.getAuthor().getId().equals(BotConfig.BOT_ID) || msg.getEmbeds().isEmpty())
					return;
				if (akinator.isGuessing())
					bot.addReaction(message, Emojis.CROSS, Emojis.CHECK);
				else
					bot.addReaction(msg, Emojis.NUMBER_1, Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4,
							Emojis.NUMBER_5);
				bot.addReaction(message, Emojis.CHECK);
				return;
			}

			bot.reactWait(message);
			return;
		}
		bot.sendThinkingPacket(channel);
		try {
			akinator = new Akinator(author);
			lastActiveTimestamp = System.currentTimeMillis();

			AkinatorCmd.message = bot.sendMessage(akinator.getQuestionEmbeded(), channel);
			akinator.setMessageId(AkinatorCmd.message.getId());
			bot.addReaction(AkinatorCmd.message, Emojis.NUMBER_1, Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4,
					Emojis.NUMBER_5);

			ReactionDispatcher.register(AkinatorCmd.message, this, AkinatorConfig.MAX_IDLE_TIME_SECONDS,
					Emojis.NUMBER_1, Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4, Emojis.NUMBER_5);
			ReactionDispatcher.registerCleanUp(AkinatorCmd.message, AkinatorConfig.MAX_IDLE_TIME_SECONDS);
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

		if (!user.getId().equals(akinator.getUser().getId())) {
			ReactionDispatcher.register(message, this, AkinatorConfig.MAX_IDLE_TIME_SECONDS, Emojis.NUMBER_1,
					Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4, Emojis.NUMBER_5, Emojis.CROSS, Emojis.CHECK);
			return;
		}

		bot.removeAllReactions(msg);
		lastActiveTimestamp = System.currentTimeMillis();
		String emoteName = emote.getName();
		// 1⃣ 2⃣ 3⃣ 4⃣ 5⃣ ❌ ✔
		int answer = -1;
		if (emoteName.equals(Emojis.NUMBER_1))
			answer = 0;
		else if (emoteName.equals(Emojis.NUMBER_2))
			answer = 1;
		else if (emoteName.equals(Emojis.NUMBER_3))
			answer = 2;
		else if (emoteName.equals(Emojis.NUMBER_4))
			answer = 3;
		else if (emoteName.equals(Emojis.NUMBER_5))
			answer = 4;
		else if (emoteName.equals(Emojis.CROSS))
			answer = 5;
		else if (emoteName.equals(Emojis.CHECK))
			answer = 6;
		else
			return;

		try {
			message = bot.editMessage(msg, akinator.getAnswerEmbeded(answer));
			akinator.setMessageId(message.getId());

			MessageEmbed embeded = akinator.next(answer);
			message = bot.editMessage(message, embeded);

			if (akinator.isActive())
				if (akinator.isGuessing()) {
					bot.addReaction(message, Emojis.CROSS, Emojis.CHECK);
					ReactionDispatcher.register(message, this, AkinatorConfig.MAX_IDLE_TIME_SECONDS, Emojis.CROSS,
							Emojis.CHECK);
				} else {
					bot.addReaction(message, Emojis.NUMBER_1, Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4,
							Emojis.NUMBER_5);
					ReactionDispatcher.register(message, this, AkinatorConfig.MAX_IDLE_TIME_SECONDS, Emojis.NUMBER_1,
							Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4, Emojis.NUMBER_5);
				}

			ReactionDispatcher.registerCleanUp(message, AkinatorConfig.MAX_IDLE_TIME_SECONDS);
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
