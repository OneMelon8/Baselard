package JProjects.BaseInfoBot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

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
import JProjects.BaseInfoBot.commands.bandori.BandoriPlay;
import JProjects.BaseInfoBot.commands.bandori.BandoriReady;
import JProjects.BaseInfoBot.commands.bandori.BandoriRole;
import JProjects.BaseInfoBot.commands.bandori.BandoriUserCards;
import JProjects.BaseInfoBot.commands.fun.AkinatorCmd;
import JProjects.BaseInfoBot.commands.fun.Fortune;
import JProjects.BaseInfoBot.commands.fun.Pat;
import JProjects.BaseInfoBot.commands.fun.tkon.TkonCmd;
import JProjects.BaseInfoBot.commands.helpers.JdaEventListener;
import JProjects.BaseInfoBot.commands.helpers.ReactionDispatcher;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.tkon.TkonSkillPool;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public class BaseInfoBot {

	private JDA api;
	public Timer timer;
	public AudioPlayerManager audioManager;

	public static ArrayList<String> admins = new ArrayList<String>(); // A list of ID's
	private static String version;

	public BaseInfoBot(String ver) throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(EnviroHandler.getBotToken());
		this.api = builder.build();
		this.timer = new Timer();
		this.audioManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(this.audioManager);

		version = ver;

		this.startTimer();
		this.registerHandlers();
		this.init();
	}

	public void teardown() {
		for (Guild guild : this.getJDA().getGuilds())
			getJDA().getDirectAudioController().disconnect(guild);
	}

	public void init() {
		TkonSkillPool.initSkillPool();
	}

	public void startTimer() {
		this.timer.scheduleAtFixedRate(new TimerTask() {
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
		this.timer.schedule(task, delay);
	}

	public void addListener(Object listener) {
		this.api.addEventListener(listener);
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
		new BandoriPlay(this);
		// new BandoriComics(this);

		// Beta
		new TkonCmd(this);

		// Fun
		new Pat(this);
		new Fortune(this);
		new AkinatorCmd(this);
	}

	public void sendThinkingPacket(MessageChannel channel) {
		channel.sendTyping().complete();
	}

	public void removeAllReactions(Message msg) {
		if (msg == null)
			return;
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
		return this.getJDA().getEmoteById(Emotes.getId(emote));
	}

	public void reactCheck(Message msg) {
		this.addReaction(msg, Emojis.CHECK);
	}

	public void reactCross(Message msg) {
		this.addReaction(msg, Emojis.CROSS);
	}

	public void reactQuestion(Message msg) {
		this.addReaction(msg, this.getJDA().getEmoteById(Emotes.getId(Emotes.KOKORO_WUT)));
	}

	public void reactWait(Message msg) {
		this.addReaction(msg, Emojis.HOUR_GLASS);
	}

	public void reactDetails(Message msg) {
		this.addReaction(msg, Emojis.MAGNIYFING_GLASS);
	}

	public void reactPrev(Message msg) {
		this.addReaction(msg, Emojis.ARROW_LEFT);
	}

	public void reactNext(Message msg) {
		this.addReaction(msg, Emojis.ARROW_RIGHT);
	}

	public void reactError(Message msg) {
		this.addReaction(msg, this.getJDA().getEmoteById(Emotes.getId(Emotes.KOKORO_ERROR)));
	}

	public Message sendFile(File file, MessageChannel channel) {
		return channel.sendFile(file).complete();
	}

	public Message sendFile(File file, String description, MessageChannel channel) {
		return channel.sendFile(file, description).complete();
	}

	public boolean isAdmin(User user) {
		return admins.contains(user.getId());
	}

	public User getUserById(String id) {
		return this.getJDA().getUserById(id);
	}

	public Guild getGuildById(String id) {
		return this.getJDA().getGuildById(id);
	}

	public String getUserDisplayName(Member member) {
		return member.getEffectiveName();
	}

	public String getUserDisplayName(User user, Guild guild) {
		return this.getUserDisplayName(guild.getMember(user));
	}

	public String getUserDisplayName(String id, Guild guild) {
		return this.getUserDisplayName(this.getUserById(id), guild);
	}

	public void setMuted(boolean mute) {
		JdaEventListener.mute = mute;
	}

	public JDA getJDA() {
		return api;
	}

	public static String getVersion() {
		return version;
	}
}
