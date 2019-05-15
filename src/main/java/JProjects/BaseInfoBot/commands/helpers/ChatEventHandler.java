package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.database.Messages;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatEventHandler extends ListenerAdapter {
	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Baselard v" + Bot.getVersion() + " is ready to roll!");
		event.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, Messages.prefix + "help | For bugs @One 🍉"));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		CommandDispatcher.onCommand(e);
	}
}
