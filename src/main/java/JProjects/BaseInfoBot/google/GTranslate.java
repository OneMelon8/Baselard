package JProjects.BaseInfoBot.google;

import java.util.ArrayList;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;

public class GTranslate {
	private static Translate t;

	public static void init() {
		t = TranslateOptions.getDefaultInstance().getService();
	}

	public static ArrayList<String> translate(ArrayList<String> messages, String fromLang, String toLang) {
		ArrayList<String> output = new ArrayList<>();
		for (String msg : messages)
			output.add(translate(msg, fromLang, toLang));
		return output;
	}

	public static String translate(String msg, String toLang) {
		return t.translate(msg, TranslateOption.targetLanguage(toLang)).getTranslatedText();
	}

	public static String translate(String msg, String fromLang, String toLang) {
		return t.translate(msg, TranslateOption.sourceLanguage(fromLang), TranslateOption.targetLanguage(toLang))
				.getTranslatedText();
	}
}