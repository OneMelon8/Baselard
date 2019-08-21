package JProjects.BaseInfoBot.commands.helpers;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public interface ChatIntentHandler {

	public void onIntentDetected(ChatIntent intent, User author, Message message, String messageRaw,
			MessageChannel channel, Guild guild);

}
