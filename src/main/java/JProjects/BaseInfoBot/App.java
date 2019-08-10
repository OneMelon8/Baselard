package JProjects.BaseInfoBot;

import java.io.IOException;

import JProjects.BaseInfoBot.commands.fun.Pat;
import JProjects.BaseInfoBot.commands.helpers.ChatEventHandler;
import JProjects.BaseInfoBot.google.GTranslate;
import JProjects.BaseInfoBot.google.GVision;

public class App {
	public static BaseInfoBot bot;

	public static void main(String[] args) {
		try {
			System.out.println("Hello, Happy World!");
			bot = new BaseInfoBot("3.3.0.1");
			bot.addListener(new ChatEventHandler());

			// initGoogle();
			initShutDown();
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

	public static void initGoogle() throws IOException {
		GVision.init();
		GTranslate.init();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				GVision.close();
			}
		}));
	}
}
