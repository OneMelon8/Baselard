package JProjects.BaseInfoBot.spider;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.paste.Paste;
import com.besaba.revonline.pastebinapi.paste.PasteBuilder;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import com.besaba.revonline.pastebinapi.response.Response;

import JProjects.BaseInfoBot.tools.EnviroHandler;

public class PastebinSpider {
	private static final String key = EnviroHandler.getPastebinToken();
	private static final PastebinFactory factory = new PastebinFactory();
	private static final Pastebin pastebin = factory.createPastebin(key);

	public static String uploadText(String title, String message) {
		PasteBuilder pasteBuilder = factory.createPaste();
		pasteBuilder.setTitle(title);
		pasteBuilder.setRaw(message);
		pasteBuilder.setMachineFriendlyLanguage("markdown???");
		pasteBuilder.setVisiblity(PasteVisiblity.Unlisted);
		pasteBuilder.setExpire(PasteExpire.OneHour);

		final Paste paste = pasteBuilder.build();
		final Response<String> postResult = pastebin.post(paste);

		if (postResult.hasError()) {
			System.err.println(postResult.getError());
			return "https://discordapp.com/404";
		}
		return postResult.get();
	}

}
