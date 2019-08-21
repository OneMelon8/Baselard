package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.tools.GeneralTools;

public enum ChatIntentPattern {

	BANDORI_CREATE_ROOM;

	public static ChatIntent getIntentFromMessage(String msg) {
		ChatIntent output = null;
		// Iterate one by one
		output = getBandoriCreateRoomIntent(msg);
		if (output != null)
			return output;

		// No intent found
		return null;
	}

	/**
	 * Intent: 5 consecutive numbers in a message <br>
	 * <br>
	 * <b>Intent Data:</b> <br>
	 * - ROOM_ID: [String] 5 digit room ID
	 */
	private static ChatIntent getBandoriCreateRoomIntent(String msg) {
		for (String word : msg.split(" ")) {
			word = word.replaceAll("\\D", "");
			if (word.length() != 5 || !GeneralTools.isInt(word))
				continue;
			ChatIntent intent = new ChatIntent(BANDORI_CREATE_ROOM);
			intent.put("ROOM_ID", word);
			return intent;
		}
		return null;
	}

}
