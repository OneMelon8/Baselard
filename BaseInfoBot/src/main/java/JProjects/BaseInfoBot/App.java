package JProjects.BaseInfoBot;

import javax.security.auth.login.LoginException;

import JProjects.BaseInfoBot.commands.helpers.ChatEventHandler;

public class App {
	public static Bot bot;

	public static void main(String[] args) {
		try {
			System.out.println("Hello World!");
			bot = new Bot();
			bot.addListener(new ChatEventHandler());
			bot.registerCommands();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
}
