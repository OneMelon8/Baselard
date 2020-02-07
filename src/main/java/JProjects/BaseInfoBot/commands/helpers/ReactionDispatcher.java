package JProjects.BaseInfoBot.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

public class ReactionDispatcher {

	private static ConcurrentHashMap<Object[], ReactionHandler> dynamicRegisteredListeners = new ConcurrentHashMap<Object[], ReactionHandler>();
	public static HashMap<Message, Long> purgeReactions = new HashMap<Message, Long>();

	public static void register(Message msg, ReactionHandler event, String... emoteName) {
		register(msg, event, Arrays.asList(emoteName), BotConfig.REACTION_TIME_OUT);
	}

	public static void register(Message msg, ReactionHandler event, long timeoutInSeconds, String... emoteName) {
		register(msg, event, Arrays.asList(emoteName), timeoutInSeconds);
	}

	public static void register(Message msg, ReactionHandler event, List<String> emoteNames, long timeoutInSeconds) {
		final Object[] info = new Object[] { msg, new ArrayList<String>(emoteNames) };
		dynamicRegisteredListeners.put(info, event);
		App.bot.scheduleDelayedTask(new TimerTask() {
			@Override
			public void run() {
				if (!dynamicRegisteredListeners.containsKey(info))
					return;
				dynamicRegisteredListeners.remove(info);
			}
		}, timeoutInSeconds * 1000);
	}

	public static void registerCleanUp(Message message) {
		purgeReactions.put(message, System.currentTimeMillis() / 1000 + BotConfig.REACTION_TIME_OUT);
	}

	public static void registerCleanUp(Message message, long delayInSeconds) {
		purgeReactions.put(message, System.currentTimeMillis() / 1000 + delayInSeconds);
	}

	public static void cleanUp() {
		long now = System.currentTimeMillis() / 1000;
		Message[] messages = (Message[]) purgeReactions.keySet().toArray(new Message[0]);
		for (Message m : messages) {
			if (m == null) {
				purgeReactions.remove(m);
				continue;
			}
			long purgeTime = purgeReactions.get(m);
			if (now < purgeTime)
				continue;
			App.bot.removeAllReactions(m);
			purgeReactions.remove(m);
		}
	}

	@SuppressWarnings("unchecked")
	public static void fire(User user, ReactionEmote emo, String messageId, MessageChannel channel, Guild guild) {
		if (user.isBot())
			return;
		Message msg = channel.retrieveMessageById(messageId).complete();
		List<MessageReaction> reactions = msg.getReactions();
		boolean botReacted = false;
		for (MessageReaction reaction : reactions) {
			if (!reaction.getReactionEmote().getName().equals(emo.getName()))
				continue;
			botReacted = reaction.isSelf();
			break;
		}
		if (!botReacted)
			return;

		long time = msg.getTimeCreated().toEpochSecond();
		if (msg.getTimeEdited() != null)
			time = msg.getTimeEdited().toEpochSecond();
		boolean timedOut = System.currentTimeMillis() / 1000 - time > BotConfig.REACTION_TIME_OUT;
		if (timedOut)
			return;

		String emoteName = emo.getName();
		System.out.println(GeneralTools.getTime() + " >> " + user.getAsTag() + " reacted with " + emo.getName()
				+ " on message " + msg.getContentRaw());

		Iterator<Map.Entry<Object[], ReactionHandler>> iter = dynamicRegisteredListeners.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Object[], ReactionHandler> entry = iter.next();
			Object[] info = entry.getKey();
			Message storedMessage = (Message) info[0];
			ArrayList<String> emoteMatchers = (ArrayList<String>) info[1];
			if (!storedMessage.getId().equals(messageId) || !emoteMatchers.contains(emoteName))
				continue;
			ReactionHandler event = entry.getValue();
			event.onReact(user, emo, msg, channel, guild);
			iter.remove();
		}
	}
}
