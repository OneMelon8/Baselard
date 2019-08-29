package JProjects.BaseInfoBot;

import JProjects.BaseInfoBot.commands.fun.Pat;
import JProjects.BaseInfoBot.commands.helpers.ChatEventListener;
import JProjects.BaseInfoBot.database.config.ServerChannelConfig;

public class App {
	public static BaseInfoBot bot;

	public static void main(String[] args) {
		try {
			System.out.println("Hello, Happy World!");
			bot = new BaseInfoBot("3.3.2.3.1.18");
			bot.addListener(new ChatEventListener());

			initShutDown();
			ServerChannelConfig.init();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private static void initShutDown() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				Pat.export();
			}
		}));
	}
}
