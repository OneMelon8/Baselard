package JProjects.BaseInfoBot.tools;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class KeyTyper {

	public static void main(String[] args) {
		try {
			final Robot bot = new Robot();
			new Timer().scheduleAtFixedRate(new TimerTask() {
				int count = 0;

				@Override
				public void run() {
					if (count < 10)
						type(bot, "$w\n");
					else if (count < 13)
						type(bot, "$wg\n");
					else {
						this.cancel();
						System.exit(0);
						return;
					}
					count++;
				}
			}, 0, 1200);
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void type(Robot bot, String msg) {
		for (char c : msg.toCharArray()) {
			int code = KeyEvent.getExtendedKeyCodeForChar(c);
			if (c == '$')
				code = KeyEvent.getExtendedKeyCodeForChar('4');
			if (Character.isUpperCase(c) || c == '$')
				bot.keyPress(KeyEvent.VK_SHIFT);
			bot.keyPress(code);
			bot.keyRelease(code);
			if (Character.isUpperCase(c) || c == '$')
				bot.keyRelease(KeyEvent.VK_SHIFT);
		}

	}
}
