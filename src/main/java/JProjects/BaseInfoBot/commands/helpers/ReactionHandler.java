package JProjects.BaseInfoBot.commands.helpers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

public interface ReactionHandler {
	public void onReact(User user, ReactionEmote emote, Message msg, MessageChannel channel, Guild guild);
}
