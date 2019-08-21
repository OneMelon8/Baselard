package JProjects.BaseInfoBot.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChatIntentDispatcher {

	/**
	 * PROTOTYPE CLASS!! <br>
	 * If Base actually got a neural network, this class will be enhanced
	 */

	// One intent can have multiple handlers
	public static HashMap<ChatIntentPattern, ArrayList<ChatIntentHandler>> registeredHandlers = new HashMap<ChatIntentPattern, ArrayList<ChatIntentHandler>>();

	public static void fire(MessageReceivedEvent e) {
		String messageRaw = e.getMessage().getContentRaw();
		ChatIntent intent = ChatIntentPattern.getIntentFromMessage(messageRaw);
		if (intent == null)
			return;
		ArrayList<ChatIntentHandler> listeners = registeredHandlers.get(intent.getPattern());
		if (listeners == null || listeners.isEmpty())
			return;

		System.out.println(GeneralTools.getTime() + " >> " + e.getAuthor().getAsTag() + " triggered intent "
				+ intent.getPattern().toString());

		for (ChatIntentHandler handler : listeners)
			handler.onIntentDetected(intent, e.getAuthor(), e.getMessage(), messageRaw, e.getChannel(), e.getGuild());
	}

	public static void register(ChatIntentPattern pattern, ChatIntentHandler handler) {
		if (registeredHandlers.containsKey(pattern))
			registeredHandlers.get(pattern).add(handler);
		else
			registeredHandlers.put(pattern, new ArrayList<ChatIntentHandler>(Arrays.asList(handler)));
	}
}
