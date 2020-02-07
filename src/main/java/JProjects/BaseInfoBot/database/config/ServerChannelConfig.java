package JProjects.BaseInfoBot.database.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerChannelConfig {
	public static HashMap<String, ArrayList<String>> whitelistedChannels = new HashMap<String, ArrayList<String>>();

	public static void init() {
		// Master of Eternity Global Server
		whitelistedChannels.put("423512363765989378", new ArrayList<String>(Arrays.asList("453918676022722561",
				"439773791023792130", "562766797032652800", "605289350447759403", "617346677908439088")));
	}
}
