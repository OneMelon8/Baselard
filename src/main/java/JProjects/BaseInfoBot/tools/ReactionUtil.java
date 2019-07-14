package JProjects.BaseInfoBot.tools;

import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;

public class ReactionUtil {

	public static boolean hasReaction(Message msg, String reactionName) {
		List<MessageReaction> reactions = msg.getReactions();
		for (MessageReaction reaction : reactions) {
			if (!reaction.getReactionEmote().getName().equalsIgnoreCase(reactionName))
				continue;
			return true;
		}
		return false;
	}

}
