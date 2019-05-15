package JProjects.BaseInfoBot.database;

public class Emotes {
	public static final String RODY_BEAT = "<:rodybeat:552017882905575424>";
	public static final String MINECRAFT_GRASS = "<:mc_grass:575908022346579969>";
	public static final String MINECRAFT_COMMAND_BLOCK = "<:mc_command:575908475888992266>";
	public static final String MINECRAFT_COBBLESTONE = "<:mc_cobble:575908019783598130>";
	public static final String MINECRAFT_GLASS = "<:mc_glass:575908022526672916>";
	public static final String ATTR_PURE = "<:attr_pure:578337887146213382>";
	public static final String ATTR_POWER = "<:attr_power:578337887112658954>";
	public static final String ATTR_HAPPY = "<:attr_happy:578337887238488074>";
	public static final String ATTR_COOL = "<:attr_cool:578337887142281216>";

	public static final long MINECRAFT_GRASS_ID = 575908022346579969L;
	public static final long MINECRAFT_COMMAND_BLOCK_ID = 575908475888992266L;
	public static final long MINECRAFT_COBBLESTONE_ID = 575908019783598130L;
	public static final long MINECRAFT_GLASS_ID = 575908022526672916L;

	public static String getAttribute(String attr) {
		attr = attr.toLowerCase();
		switch (attr) {
		case "pure":
			return ATTR_PURE;
		case "power":
			return ATTR_POWER;
		case "happy":
			return ATTR_HAPPY;
		case "cool":
			return ATTR_COOL;
		default:
			System.out.println(attr);
			return null;
		}
	}
}
