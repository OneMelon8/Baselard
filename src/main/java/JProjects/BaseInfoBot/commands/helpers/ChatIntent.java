package JProjects.BaseInfoBot.commands.helpers;

import java.util.HashMap;

public class ChatIntent {

	private ChatIntentPattern pattern;
	private HashMap<String, Object> data;

	public ChatIntent(ChatIntentPattern pattern) {
		this.pattern = pattern;
		this.data = new HashMap<String, Object>();
	}

	public ChatIntent(ChatIntentPattern pattern, HashMap<String, Object> data) {
		this(pattern);
		this.data = data;
	}

	public Object getData(String key) {
		return this.data.get(key);
	}

	public void put(String key, Object data) {
		this.data.put(key, data);
	}

	public void remove(String key) {
		this.data.remove(key);
	}

	// Generated
	public ChatIntentPattern getPattern() {
		return pattern;
	}

	public HashMap<String, Object> getDataMap() {
		return data;
	}

	public void setPattern(ChatIntentPattern pattern) {
		this.pattern = pattern;
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}
}
