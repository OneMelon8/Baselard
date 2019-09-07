package JProjects.BaseInfoBot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import JProjects.BaseInfoBot.commands.Help;
import JProjects.BaseInfoBot.commands.Lookup;
import JProjects.BaseInfoBot.commands.Ping;
import JProjects.BaseInfoBot.commands.Version;
import JProjects.BaseInfoBot.commands.admin.Delete;
import JProjects.BaseInfoBot.commands.admin.Gamemode;
import JProjects.BaseInfoBot.commands.admin.Servers;
import JProjects.BaseInfoBot.commands.admin.Test;
import JProjects.BaseInfoBot.commands.admin.Toggle;
import JProjects.BaseInfoBot.commands.bandori.BandoriCards;
import JProjects.BaseInfoBot.commands.bandori.BandoriEvents;
import JProjects.BaseInfoBot.commands.bandori.BandoriMembers;
import JProjects.BaseInfoBot.commands.bandori.BandoriMultiLive;
import JProjects.BaseInfoBot.commands.bandori.BandoriReady;
import JProjects.BaseInfoBot.commands.bandori.BandoriRole;
import JProjects.BaseInfoBot.commands.bandori.BandoriUserCards;
import JProjects.BaseInfoBot.commands.fun.AkinatorCmd;
import JProjects.BaseInfoBot.commands.fun.Fortune;
import JProjects.BaseInfoBot.commands.fun.Pat;
import JProjects.BaseInfoBot.commands.helpers.ChatEventListener;
import JProjects.BaseInfoBot.commands.helpers.ReactionDispatcher;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

public class BaseInfoBot {

	private JDA api;
	public Timer timer;

	public static ArrayList<String> admins = new ArrayList<String>(); // A list of ID's
	private static String version;

	public BaseInfoBot(String ver) throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(EnviroHandler.getBotToken());
		this.api = builder.build();
		version = ver;

		this.startTimer();
		this.registerHandlers();
	}

	public void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ReactionDispatcher.cleanUp();
				AkinatorCmd.timerTick();
				BandoriMultiLive.autoDisband();
			}
		}, 0, 1000);
	}

	public void schedule(TimerTask task, long delay) {
		this.timer.schedule(task, delay);
	}

	public void scheduleDelayedTask(TimerTask task, long delay) {
		timer.schedule(task, delay);
	}

	public void addListener(Object listener) {
		api.addEventListener(listener);
	}

	private void registerHandlers() {
		// General Commands
		new Help(this);
		new Ping(this);
		new Version(this);
		new Lookup(this);
		// General Administration
		new Gamemode(this);
		new Test(this);
		new Toggle(this);
		new Servers(this);
		new Delete(this);

		// Bandori Commands
		new BandoriEvents(this);
		new BandoriCards(this);
		new BandoriMembers(this);
		new BandoriReady(this);
		new BandoriMultiLive(this);
		new BandoriRole(this);
		new BandoriUserCards(this);
		// new BandoriComics(this);

		// Beta

		// Fun
		new Pat(this);
		new Fortune(this);
		new AkinatorCmd(this);
	}

	public void sendThinkingPacket(MessageChannel channel) {
		channel.sendTyping().complete();
	}

	public void removeAllReactions(Message msg) {
		msg.clearReactions().queue();
	}

	public Message sendMessage(String message, MessageChannel channel) {
		return channel.sendMessage(message).complete();
	}

	public Message sendMessage(MessageEmbed message, MessageChannel channel) {
		return channel.sendMessage(message).complete();
	}

	public void sendMessage(List<MessageEmbed> messages, MessageChannel channel) {
		for (MessageEmbed message : messages)
			channel.sendMessage(message).queue();
	}

	public Message editMessage(Message msg, String msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public Message editMessage(Message msg, Message msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public Message editMessage(Message msg, MessageEmbed msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public void deleteMessage(Message msg) {
		msg.delete().queue();
	}

	public void deleteMessage(String id, MessageChannel channel) {
		channel.deleteMessageById(id).queue();
	}

	public void addReaction(Message msg, String reaction) {
		msg.addReaction(reaction).queue();
	}

	public void addReaction(Message msg, String... reactions) {
		for (String reaction : reactions)
			this.addReaction(msg, reaction);
	}

	public void addReaction(Message msg, Emote reaction) {
		msg.addReaction(reaction).queue();
	}

	public void addReaction(Message msg, Emote... reactions) {
		for (Emote reaction : reactions)
			this.addReaction(msg, reaction);
	}

	public void removeReaction(Message msg, String reaction) {
		for (MessageReaction r : msg.getReactions()) {
			if (!r.getReactionEmote().getName().equalsIgnoreCase(reaction))
				continue;
			r.removeReaction().queue();
			break;
		}
	}

	public Emote getEmote(String emote) {
		return getJDA().getEmoteById(Emotes.getId(emote));
	}

	public void reactCheck(Message msg) {
		addReaction(msg, Emojis.CHECK);
	}

	public void reactCross(Message msg) {
		addReaction(msg, Emojis.CROSS);
	}

	public void reactQuestion(Message msg) {
		addReaction(msg, getJDA().getEmoteById(Emotes.getId(Emotes.KOKORO_WUT)));
	}

	public void reactWait(Message msg) {
		addReaction(msg, Emojis.HOUR_GLASS);
	}

	public void reactDetails(Message msg) {
		addReaction(msg, Emojis.MAGNIYFING_GLASS);
	}

	public void reactPrev(Message msg) {
		addReaction(msg, Emojis.ARROW_LEFT);
	}

	public void reactNext(Message msg) {
		addReaction(msg, Emojis.ARROW_RIGHT);
	}

	public void reactError(Message msg) {
		addReaction(msg, getJDA().getEmoteById(Emotes.getId(Emotes.KOKORO_ERROR)));
	}

	public Message sendFile(File file, MessageChannel channel) {
		return channel.sendFile(file).complete();
	}

	public Message sendFile(File file, String description, MessageChannel channel) {
		return channel.sendFile(file, description).complete();
	}

	public Message sendFile(File file, Message message, MessageChannel channel) {
		return channel.sendFile(file, message).complete();
	}

	public boolean isAdmin(User user) {
		return admins.contains(user.getId());
	}

	public User getUserById(String id) {
		return getJDA().getUserById(id);
	}

	public String getUserDisplayName(String id, Guild guild) {
		Member m = guild.getMember(getUserById(id));
		return m.getEffectiveName();
	}

	public String getUserDisplayName(Member m) {
		return m.getEffectiveName();
	}

	public void setMuted(boolean mute) {
		ChatEventListener.mute = mute;
	}

	public JDA getJDA() {
		return api;
	}

	public static String getVersion() {
		return version;
	}
}
