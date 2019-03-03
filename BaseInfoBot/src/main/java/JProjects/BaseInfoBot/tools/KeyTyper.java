package JProjects.BaseInfoBot.tools;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyTyper {

	private Robot bot;

	public KeyTyper() {
		try {
			bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}
	}

	public void type(String msg) {
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

	public void dummy() {
	}
}
