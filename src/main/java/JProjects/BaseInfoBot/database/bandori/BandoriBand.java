package JProjects.BaseInfoBot.database.bandori;

import java.awt.image.BufferedImage;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.files.assets.ImageAssets;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public enum BandoriBand {
	POPPIN_PARTY, AFTERGLOW, PASTEL_PALETTES, ROSELIA, HELLO_HAPPY_WORLD, RAISE_A_SUILEN;

	public static BandoriBand fromString(String band) {
		band = band.toLowerCase();
		if (band.equals("poppin' party") || band.equals("poppin'party") || band.equals("Poppin&#39;Party")
				|| band.equals("popipa"))
			return POPPIN_PARTY;
		else if (band.equals("afterglow"))
			return AFTERGLOW;
		else if (band.equals("pastel✽palettes") || band.equals("pastel*palettes") || band.equals("pastel palettes"))
			return PASTEL_PALETTES;
		else if (band.equals("roselia"))
			return ROSELIA;
		else if (band.equals("hello, happy world!") || band.equals("hello happy world") || band.equals("hhw"))
			return HELLO_HAPPY_WORLD;
		else if (band.equals("raise a suilen") || band.equals("ras"))
			return RAISE_A_SUILEN;
		return null;
	}

	public static BandoriBand fromIndex(int index) {
		switch (index) {
		case 0:
			return POPPIN_PARTY;
		case 1:
			return AFTERGLOW;
		case 2:
			return PASTEL_PALETTES;
		case 3:
			return ROSELIA;
		case 4:
			return HELLO_HAPPY_WORLD;
		case 5:
			return RAISE_A_SUILEN;
		default:
			return null;
		}
	}

	public String getDisplayName() {
		switch (this) {
		case POPPIN_PARTY:
			return "Poppin' Party";
		case AFTERGLOW:
			return "Afterglow";
		case PASTEL_PALETTES:
			return "Pastel✽Palettes";
		case ROSELIA:
			return "Roselia";
		case HELLO_HAPPY_WORLD:
			return "Hello, Happy World!";
		case RAISE_A_SUILEN:
			return "Raise A Suilen";
		default:
			return null;
		}
	}

	public String getEmote() {
		switch (this) {
		case POPPIN_PARTY:
			return Emotes.POPPIN_PARTY;
		case AFTERGLOW:
			return Emotes.AFTERGLOW;
		case PASTEL_PALETTES:
			return Emotes.PASTEL_PALETTES;
		case ROSELIA:
			return Emotes.ROSELIA;
		case HELLO_HAPPY_WORLD:
			return Emotes.HELLO_HAPPY_WORLD;
		case RAISE_A_SUILEN:
			return Emotes.RAISE_A_SUILEN;
		default:
			return null;
		}
	}

	public BandoriMember[] getMembers() {
		switch (this) {
		case POPPIN_PARTY:
			return new BandoriMember[] { BandoriMember.KASUMI_TOYAMA, BandoriMember.TAE_HANAZONO,
					BandoriMember.RIMI_USHIGOME, BandoriMember.ARISA_ICHIGAYA, BandoriMember.SAAYA_YAMABUKI };
		case AFTERGLOW:
			return new BandoriMember[] { BandoriMember.RAN_MITAKE, BandoriMember.MOCA_AOBA, BandoriMember.HIMARI_UEHARA,
					BandoriMember.TSUGUMI_HAZAWA, BandoriMember.TOMOE_UDAGAWA };
		case PASTEL_PALETTES:
			return new BandoriMember[] { BandoriMember.AYA_MARUYAMA, BandoriMember.HINA_HIKAWA,
					BandoriMember.CHISATO_SHIRASAGI, BandoriMember.EVE_WAKAMIYA, BandoriMember.MAYA_YAMATO };
		case ROSELIA:
			return new BandoriMember[] { BandoriMember.YUKINA_MINATO, BandoriMember.SAYO_HIKAWA,
					BandoriMember.LISA_IMAI, BandoriMember.RINKO_SHIROKANE, BandoriMember.AKO_UDAGAWA };
		case HELLO_HAPPY_WORLD:
			return new BandoriMember[] { BandoriMember.KOKORO_TSURUMAKI, BandoriMember.KAORU_SETA,
					BandoriMember.HAGUMI_KITAZAWA, BandoriMember.MISAKI_OKUSAWA, BandoriMember.KANON_MATSUBARA };
		case RAISE_A_SUILEN:
			return new BandoriMember[] { BandoriMember.KASUMI_TOYAMA, BandoriMember.KASUMI_TOYAMA,
					BandoriMember.KASUMI_TOYAMA, BandoriMember.KASUMI_TOYAMA, BandoriMember.KASUMI_TOYAMA };
		default:
			return null;
		}
	}

	public String getMembersDisplay() {
		BandoriMember[] members = this.getMembers();
		StringBuilder sb = new StringBuilder();
		for (BandoriMember member : members)
			sb.append(member.getDisplayName() + ", ");
		if (sb.length() > 0)
			sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public Field getBandMembersField() {
		return new Field("**" + this.getEmote() + " " + this.getDisplayName() + ":**", this.getMembersDisplay(), false);
	}

	public BufferedImage getBufferedImage() {
		String name = null;
		switch (this) {
		case POPPIN_PARTY:
			name = ImageAssets.LOGO_POPPIN_PARTY;
			break;
		case AFTERGLOW:
			name = ImageAssets.LOGO_AFTERGLOW;
			break;
		case PASTEL_PALETTES:
			name = ImageAssets.LOGO_PASTEL_PALETTES;
			break;
		case ROSELIA:
			name = ImageAssets.LOGO_ROSELIA;
			break;
		case HELLO_HAPPY_WORLD:
			name = ImageAssets.LOGO_HELLO_HAPPY_WORLD;
			break;
		case RAISE_A_SUILEN:
			name = ImageAssets.LOGO_RAISE_A_SUILEN;
			break;
		}
		return ImageAssets.getImage(name);
	}
}
