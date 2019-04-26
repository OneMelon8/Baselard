package JProjects.BaseInfoBot;

import JProjects.BaseInfoBot.commands.helpers.ChatEventHandler;
import JProjects.BaseInfoBot.google.GTranslate;
import JProjects.BaseInfoBot.google.GVision;

public class App {
	public static Bot bot;
	public static boolean ready = false;

	public static void main(String[] args) {
		try {
			System.out.println("Hello World!");
			bot = new Bot();
			bot.addListener(new ChatEventHandler());
			bot.registerCommands();

			GVision.init();
			GTranslate.init();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					GVision.close();
				}
			}));
			ready = true;
		} catch (Exception e) {
			e.printStackTrace();
			ready = false;
			return;
		}
	}
}
