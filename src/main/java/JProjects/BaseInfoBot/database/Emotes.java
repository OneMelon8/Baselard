package JProjects.BaseInfoBot.database;

public class Emotes {
	public static final String RODY_BEAT = "<:rodybeat:552017882905575424>";
	public static final String KOKORON_WUT = "<:kokoron_wut:598387497155821579>";
	public static final String KOKORON_ERROR = "<:kokoron_error:598824542978441232>";

	public static final String MINECRAFT_GRASS = "<:mc_grass:575908022346579969>";
	public static final String MINECRAFT_COMMAND_BLOCK = "<:mc_command:575908475888992266>";
	public static final String MINECRAFT_COBBLESTONE = "<:mc_cobble:575908019783598130>";
	public static final String MINECRAFT_GLASS = "<:mc_glass:575908022526672916>";

	public static final String ATTR_PURE = "<:attr_pure:578337887146213382>";
	public static final String ATTR_POWER = "<:attr_power:578337887112658954>";
	public static final String ATTR_HAPPY = "<:attr_happy:578337887238488074>";
	public static final String ATTR_COOL = "<:attr_cool:578337887142281216>";
	public static final String BANDORI_STAR = "<:bandori_star:578806302864572427>";
	public static final String BANDORI_STAR2 = "<:bandori_star2:580847904508346378>";

	public static final String BANDORI_RARITY_1 = "<:bandori_rarity_1:599082647708237824>";
	public static final String BANDORI_RARITY_2 = "<:bandori_rarity_2:599082647699980298>";
	public static final String BANDORI_RARITY_3 = "<:bandori_rarity_3:599082647636934696>";
	public static final String BANDORI_RARITY_4 = "<:bandori_rarity_4:599085915205271582>";

	public static final String POPPIN_PARTY = "<:poppin_party:581601489290788864>";
	public static final String AFTERGLOW = "<:afterglow:581601488808312843>";
	public static final String PASTEL_PALETTES = "<:pastel_palettes:581601488485482507>";
	public static final String ROSELIA = "<:roselia:581601488577626125>";
	public static final String HELLO_HAPPY_WORLD = "<:hello_happy_world:581601488883941376>";

	// POPIPA
	public static final String KASUMI_TOYAMA = "<:kasumi_toyama:580848806057476106>";
	public static final String ARISA_ICHIGAYA = "<:arisa_ichigaya:580848805998493706>";
	public static final String TAE_HANAZONO = "<:tae_hanazono:580848806141362186>";
	public static final String SAAYA_YAMABUKI = "<:saaya_yamabuki:580848806166396928>";
	public static final String RIMI_USHIGOME = "<:rimi_ushigome:580848806086705173>";

	// AFTERGLOW
	public static final String RAN_MITAKE = "<:ran_mitake:580849036165382154>";
	public static final String MOCA_AOBA = "<:moca_aoba:580849036031164416>";
	public static final String TOMOE_UDAGAWA = "<:tomoe_udagawa:580849036387549195>";
	public static final String TSUGUMI_HAZAWA = "<:tsugumi_hazawa:580849036387549296>";
	public static final String HIMARI_UEHARA = "<:himari_uehara:580849036102467604>";

	// HAROHAPI
	public static final String KOKORO_TSURUMAKI = "<:kokoro_tsurumaki:580849364444905473>";
	public static final String HAGUMI_KITAZAWA = "<:hagumi_kitazawa:580849364105166918>";
	public static final String MISAKI_OKUSAWA = "<:misaki_okusawa:580849364570734592>";
	public static final String KANON_MATSUBARA = "<:kanon_matsubara:580849364755415094>";
	public static final String KAORU_SETA = "<:kaoru_seta:580849364407156948>";

	// P*P
	public static final String AYA_MARUYAMA = "<:aya_maruyama:580849820789506068>";
	public static final String CHISATO_SHIRASAGI = "<:chisato_shirasagi:580849821011935243>";
	public static final String MAYA_YAMATO = "<:maya_yamato:580849820647030785>";
	public static final String EVE_WAKAMIYA = "<:eve_wakamiya:580849820739043358>";
	public static final String HINA_HIKAWA = "<:hina_hikawa:580849820412149814>";

	// ROSELIA
	public static final String YUKINA_MINATO = "<:yukina_minato:580850110267785226>";
	public static final String LISA_IMAI = "<:lisa_imai:580850110557061120>";
	public static final String AKO_UDAGAWA = "<:ako_udagawa:580850110506729493>";
	public static final String SAYO_HIKAWA = "<:sayo_hikawa:580850110364385297>";
	public static final String RINKO_SHIROKANE = "<:rinko_shirokane:580850110590746626>";

	public static String getRarityEmote(int rarity) {
		switch (rarity) {
		case 1:
			return BANDORI_RARITY_1;
		case 2:
			return BANDORI_RARITY_2;
		case 3:
			return BANDORI_RARITY_3;
		case 4:
			return BANDORI_RARITY_4;
		default:
			return BANDORI_STAR;
		}
	}

	public static String getBandEmote(String band) {
		if (band.equalsIgnoreCase("Poppin&#39;Party") || band.equalsIgnoreCase("Poppin'Party"))
			return POPPIN_PARTY;
		else if (band.equalsIgnoreCase("Afterglow"))
			return AFTERGLOW;
		else if (band.equalsIgnoreCase("Pastel*Palettes"))
			return PASTEL_PALETTES;
		else if (band.equalsIgnoreCase("Roselia"))
			return ROSELIA;
		else if (band.equalsIgnoreCase("Hello, Happy World!"))
			return HELLO_HAPPY_WORLD;
		return null;
	}

	public static String getId(String emote) {
		emote = emote.replace(">", "");
		return emote.split(":")[2];
	}

	public static String getName(String emote) {
		emote = emote.replace(">", "");
		return emote.split(":")[1];
	}
}
