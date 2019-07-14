package JProjects.BaseInfoBot.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

public class EmoteDispatcher {

	private static HashMap<ReactionEvent, ArrayList<String>> registeredPriorityListeners = new HashMap<ReactionEvent, ArrayList<String>>();
	private static HashMap<ReactionEvent, ArrayList<String>> registeredListeners = new HashMap<ReactionEvent, ArrayList<String>>();

	public static HashMap<Message, Long> purgeReactions = new HashMap<Message, Long>();

	public static void register(ReactionEvent event, String emoteName, boolean priority) {
		(priority ? registeredPriorityListeners : registeredListeners).put(event,
				new ArrayList<String>(Arrays.asList(emoteName)));
	}

	public static void register(ReactionEvent event, List<String> emoteNames, boolean priority) {
		(priority ? registeredPriorityListeners : registeredListeners).put(event, new ArrayList<String>(emoteNames));
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

	public static void fire(User user, ReactionEmote emo, String messageId, MessageChannel channel) {
		if (user.isBot())
			return;
		Message msg = channel.getMessageById(messageId).complete();

		List<MessageReaction> reactions = msg.getReactions();
		boolean botReacted = false;
		for (MessageReaction reaction : reactions) {
			if (!reaction.getReactionEmote().getName().equals(emo.getName()))
				continue;
			String botId = App.bot.getJDA().getSelfUser().getId();
			for (User u : reaction.getUsers()) {
				if (!u.getId().equals(botId))
					continue;
				botReacted = true;
				break;
			}
			break;
		}
		if (!botReacted)
			return;

		// If message was sent over 30 seconds ago, ignore reaction
		long time = msg.getCreationTime().toEpochSecond();
		if (msg.getEditedTime() != null)
			time = msg.getEditedTime().toEpochSecond();
		boolean over30Seconds = System.currentTimeMillis() / 1000 - time > 30;
		if (over30Seconds) {
			if (!purgeReactions.containsKey(msg))
				purgeReactions.put(msg, time);
			return;
		}

		// Priority listeners
		for (ReactionEvent event : registeredPriorityListeners.keySet()) {
			ArrayList<String> emoteNames = registeredPriorityListeners.get(event);
			if (!emoteNames.contains(emo.getName()))
				continue;
			event.onReact(user, emo, msg, channel);
			System.out.println(GeneralTools.getTime() + " >> [PRIORITY] " + user.getAsTag() + " reacted with "
					+ emo.getName() + " on message " + msg.getContentRaw());
		}

		// Process normal listeners if the bot sent the message
		User sender = msg.getAuthor();
		if (!sender.getId().equals(Messages.ID))
			return;
		System.out.println(GeneralTools.getTime() + " >> " + user.getAsTag() + " reacted with " + emo.getName()
				+ " on message " + msg.getContentRaw());
		for (ReactionEvent event : registeredListeners.keySet()) {
			ArrayList<String> emoteNames = registeredListeners.get(event);
			if (!emoteNames.contains(emo.getName()))
				continue;
			event.onReact(user, emo, msg, channel);
		}
	}

}
