package JProjects.BaseInfoBot;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import JProjects.BaseInfoBot.commands.Codex;
import JProjects.BaseInfoBot.commands.Help;
import JProjects.BaseInfoBot.commands.Pat;
import JProjects.BaseInfoBot.commands.Ping;
import JProjects.BaseInfoBot.commands.Register;
import JProjects.BaseInfoBot.commands.StatTop;
import JProjects.BaseInfoBot.commands.SuitComp;
import JProjects.BaseInfoBot.commands.SuitStats;
import JProjects.BaseInfoBot.commands.Version;
import JProjects.BaseInfoBot.commands.admin.AddSuitAliases;
import JProjects.BaseInfoBot.commands.admin.Gamemode;
import JProjects.BaseInfoBot.commands.admin.RegisterDB;
import JProjects.BaseInfoBot.commands.admin.Test;
import JProjects.BaseInfoBot.commands.admin.Toggle;
import JProjects.BaseInfoBot.commands.admin.Translate;
import JProjects.BaseInfoBot.commands.admin.TranslateImage;
import JProjects.BaseInfoBot.commands.fun.TableFlip;
import JProjects.BaseInfoBot.commands.hangar.Hangar;
import JProjects.BaseInfoBot.commands.helpers.CommandDispatcher;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class Bot {

	private JDA api;
	public static ArrayList<String> admins = new ArrayList<String>(); // A list of ID's
	private static String version;

	public Bot(String ver) throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(EnviroHandler.getBotToken());
		this.api = builder.build();
		version = ver;
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
		new Register(this);
		new Hangar(this);
		new SuitStats(this);
		new SuitComp(this);
		new StatTop(this);
		new Codex(this);
		// MOE Administration
		new AddSuitAliases(this);
		new RegisterDB(this);

		// Beta
		new TranslateImage(this);
		new Translate(this);

		// Fun
		new TableFlip(this);
		new Pat(this);
	}

	public void sendThinkingPacket(MessageChannel channel) {
		channel.sendTyping().complete();
	}

	public void sendMessage(String message, MessageChannel channel) {
		channel.sendMessage(message).queue();
	}

	public void sendMessage(MessageEmbed message, MessageChannel channel) {
		channel.sendMessage(message).queue();
	}

	public void addReaction(Message msg, String reaction) {
		msg.addReaction(reaction).queue();
	}

	public void reactCheck(Message msg) {
		addReaction(msg, "✅");
	}

	public void reactCross(Message msg) {
		addReaction(msg, "❌");
	}

	public void reactQuestion(Message msg) {
		addReaction(msg, "❔");
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
