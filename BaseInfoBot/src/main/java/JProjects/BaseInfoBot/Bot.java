package JProjects.BaseInfoBot;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import JProjects.BaseInfoBot.commands.Help;
import JProjects.BaseInfoBot.commands.Ping;
import JProjects.BaseInfoBot.commands.StatTop;
import JProjects.BaseInfoBot.commands.SuitComp;
import JProjects.BaseInfoBot.commands.SuitStats;
import JProjects.BaseInfoBot.commands.admin.AddSuitAliases;
import JProjects.BaseInfoBot.commands.admin.Gamemode;
import JProjects.BaseInfoBot.commands.admin.RegisterDB;
import JProjects.BaseInfoBot.commands.admin.Test;
import JProjects.BaseInfoBot.commands.admin.Update;
import JProjects.BaseInfoBot.tools.EnviroHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class Bot {

	private JDA api;
	public static ArrayList<String> admins = new ArrayList<String>(); // A list of ID's

	public Bot() throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(EnviroHandler.getBotToken());
		this.api = builder.build();
	}

	public void addListener(Object listener) {
		api.addEventListener(listener);
	}

	public void registerCommands() {
		new Help(this);
		new Ping(this);

		/*
		 * part calculator? predict estimated damage and or estimated damage taken
		 */

//		new Register(this);
//		new Hangar(this);

		new SuitStats(this);
		new SuitComp(this);
		new StatTop(this);

		// Administration
		new Gamemode(this);
		new Test(this);
		new Update(this);
		new AddSuitAliases(this);
		new RegisterDB(this);

		// Fun
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

	public JDA getJDA() {
		return api;
	}
}
