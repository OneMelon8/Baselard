package JProjects.BaseInfoBot.commands.helpers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public interface ChatIntentHandler {

	public void onIntentDetected(ChatIntent intent, User author, Message message, String messageRaw,
			MessageChannel channel, Guild guild);

}
