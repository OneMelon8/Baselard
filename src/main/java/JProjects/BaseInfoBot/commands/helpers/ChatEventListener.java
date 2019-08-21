package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.config.ServerChannelConfig;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatEventListener extends ListenerAdapter {
	public static boolean mute = false;

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Baselard v" + BaseInfoBot.getVersion() + " is ready to roll!");
		event.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, BotConfig.PREFIX + "help | For bugs @One 🍉"));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!channelCheck(e.getChannel(), e.getGuild()) || e.getAuthor().isBot())
			return;
		boolean isCommand = e.getMessage().getContentRaw().startsWith(BotConfig.PREFIX);
		if (!isCommand && !mute) {
			ChatIntentDispatcher.fire(e);
			return;
		}

		// Don't need to mute check here, cause /toggle command
		CommandDispatcher.fire(e);
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (mute || !channelCheck(e.getChannel(), e.getGuild()) || e.getUser().isBot())
			return;
		ReactionDispatcher.fire(e.getUser(), e.getReactionEmote(), e.getMessageId(), e.getChannel(), e.getGuild());
	}

	private boolean channelCheck(MessageChannel channel, Guild guild) {
		if (!ServerChannelConfig.whitelistedChannels.containsKey(guild.getId()))
			return true;
		if (ServerChannelConfig.whitelistedChannels.get(guild.getId()).contains(channel.getId()))
			return true;
		return false;
	}
}
