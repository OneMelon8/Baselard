package JProjects.BaseInfoBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import JProjects.BaseInfoBot.commands.Help;
import JProjects.BaseInfoBot.commands.Pat;
import JProjects.BaseInfoBot.commands.Ping;
import JProjects.BaseInfoBot.commands.Version;
import JProjects.BaseInfoBot.commands.admin.Gamemode;
import JProjects.BaseInfoBot.commands.admin.Test;
import JProjects.BaseInfoBot.commands.admin.Toggle;
import JProjects.BaseInfoBot.commands.bandori.BandoriCards;
import JProjects.BaseInfoBot.commands.bandori.BandoriEvents;
import JProjects.BaseInfoBot.commands.bandori.BandoriMembers;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.commands.misc.TableFlip;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;

public class BaseInfoBot {

	private JDA api;
	private Timer timer;

	public static ArrayList<String> admins = new ArrayList<String>(); // A list of ID's
	private static String version;

	public BaseInfoBot(String ver) throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(EnviroHandler.getBotToken());
		this.api = builder.build();
		version = ver;

		this.startTimer();
		this.registerCommands();
		this.registerReactions();

	}

	public void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				EmoteDispatcher.cleanUp();
			}
		}, 0, 1000);
	}

	public void addListener(Object listener) {
		api.addEventListener(listener);
	}

	public void registerCommands() {
		// General Commands
		new Help(this);
		new Ping(this);
		new Version(this);
		// General Administration
		new Gamemode(this);
		new Test(this);
		new Toggle(this);

		// MOE Commands
		// new Hangar(this);
		// new MoeSuitStats(this);
		// new MoeSuitComp(this);
		// new MoeStatTop(this);
		// new MoeCodex(this);
		// MOE Combat

		// MOE Administration
		// new AddSuitAliases(this);
		// new RegisterDB(this);

		// Bandori Commands
		new BandoriEvents(this);
		new BandoriCards(this);
		new BandoriMembers(this);
		// new BandoriComics(this);

		// Beta
		// new TranslateImage(this);
		// new Translate(this);

		// Fun
		new TableFlip(this);
		new Pat(this);
	}

	public void registerReactions() {
		EmoteDispatcher.register(new Help(this), "kokoron_wut", true);
		EmoteDispatcher.register(new BandoriCards(this), Arrays.asList("▶", "◀", "attr_power", "attr_pure", "attr_cool",
				"attr_happy", "bandori_rarity_1", "bandori_rarity_2", "bandori_rarity_3", "bandori_rarity_4"), false);
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

	public Message editMessage(Message msg, MessageEmbed msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public void deleteMessage(Message msg) {
		msg.delete().queue();
	}

	public void addReaction(Message msg, String reaction) {
		msg.addReaction(reaction).queue();
	}

	public void addReaction(Message msg, Emote reaction) {
		msg.addReaction(reaction).queue();
	}

	public void removeReaction(Message msg, String reaction) {
		for (MessageReaction r : msg.getReactions()) {
			if (!r.getReactionEmote().getName().equals(reaction))
				continue;
			r.removeReaction().queue();
			break;
		}
	}

	public void reactCheck(Message msg) {
		addReaction(msg, "✅");
	}

	public void reactCross(Message msg) {
		addReaction(msg, "❌");
	}

	public void reactQuestion(Message msg) {
		addReaction(msg, getJDA().getEmoteById("598387497155821579"));
	}

	public void reactClock(Message msg) {
		addReaction(msg, "⏱");
	}

	public void reactPrev(Message msg) {
		addReaction(msg, "◀");
	}

	public void reactNext(Message msg) {
		addReaction(msg, "▶");
	}

	public void reactError(Message msg) {
		addReaction(msg, getJDA().getEmoteById(Emotes.getId(Emotes.KOKORON_ERROR)));
	}

	public void setMuted(boolean mute) {
		CommandDispatcher.mute = mute;
	}

	public JDA getJDA() {
		return api;
	}

	public static String getVersion() {
		return version;
	}
}
