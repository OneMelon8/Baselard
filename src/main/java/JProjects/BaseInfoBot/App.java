package JProjects.BaseInfoBot;

import JProjects.BaseInfoBot.commands.helpers.ChatEventHandler;
import JProjects.BaseInfoBot.google.GTranslate;
import JProjects.BaseInfoBot.google.GVision;

public class App {
	public static Bot bot;

	public static void main(String[] args) {
		try {
			System.out.println("Hello World!");
			bot = new Bot("2.6.3.1");
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
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
