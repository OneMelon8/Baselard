package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.database.BotConfig;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatEventHandler extends ListenerAdapter {
	public static boolean mute = false;

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Baselard v" + BaseInfoBot.getVersion() + " is ready to roll!");
		event.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, BotConfig.PREFIX + "help | For bugs @One 🍉"));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		CommandDispatcher.fire(e);
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (mute)
			return;
		EmoteDispatcher.fire(e.getUser(), e.getReactionEmote(), e.getMessageId(), e.getChannel(), e.getGuild());
	}
}
