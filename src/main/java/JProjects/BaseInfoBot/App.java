package JProjects.BaseInfoBot;

import java.util.HashMap;

import JProjects.BaseInfoBot.commands.fun.Pat;
import JProjects.BaseInfoBot.commands.fun.tkon.TkonCmd;
import JProjects.BaseInfoBot.commands.helpers.JdaEventListener;
import JProjects.BaseInfoBot.database.config.ServerChannelConfig;

public class App {
	public static BaseInfoBot bot;

	public static void main(String[] args) {
		try {
			System.out.println("Hello, Happy World!");
			bot = new BaseInfoBot("3.3.2.3.2");
			bot.addListener(new JdaEventListener());
			
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
				bot.teardown();
				Pat.export();
				TkonCmd.saveTkons();
			}
		}));
	}
}
