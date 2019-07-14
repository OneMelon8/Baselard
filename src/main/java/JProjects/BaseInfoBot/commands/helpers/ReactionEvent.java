package JProjects.BaseInfoBot.commands.helpers;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

public interface ReactionEvent {

	public void onReact(User user, ReactionEmote emote, Message msg, MessageChannel channel);

}
