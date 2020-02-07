package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.bandori.BandoriPlay;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.config.ServerChannelConfig;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.EntityBuilder;

public class JdaEventListener extends ListenerAdapter {
	public static boolean mute = false;

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Baselard v" + BaseInfoBot.getVersion() + " is ready to roll!");
		Activity a = EntityBuilder.createActivity(BotConfig.PREFIX + "help | For bugs @One 🍉",
				"https://www.google.com", ActivityType.DEFAULT);
		event.getJDA().getPresence().setActivity(a);
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

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		BandoriPlay.onUserLeave(e.getEntity().getUser(), e.getChannelLeft(), e.getGuild());
	}

	private boolean channelCheck(MessageChannel channel, Guild guild) {
		if (!ServerChannelConfig.whitelistedChannels.containsKey(guild.getId()))
			return true;
		if (ServerChannelConfig.whitelistedChannels.get(guild.getId()).contains(channel.getId()))
			return true;
		return false;
	}
}
