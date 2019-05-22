package JProjects.BaseInfoBot.database.bandori;

import JProjects.BaseInfoBot.database.Emotes;

public enum BandoriMember {
	KASUMI_TOYAMA, TAE_HANAZONO, RIMI_USHIGOME, SAAYA_YAMABUKI, ARISA_ICHIGAYA, RAN_MITAKE, MOCA_AOBA, HIMARI_UEHARA,
	TOMOE_UDAGAWA, TSUGUMI_HAZAWA, KOKORO_TSURUMAKI, KAORU_SETA, HAGUMI_KITAZAWA, KANON_MATSUBARA, MISAKI_OKUSAWA,
	AYA_MARUYAMA, HINA_HIKAWA, CHISATO_SHIRASAGI, MAYA_YAMATO, EVE_WAKAMIYA, YUKINA_MINATO, SAYO_HIKAWA, LISA_IMAI,
	AKO_UDAGAWA, RINKO_SHIROKANE;

	public String getEmote() {
		switch (this) {
		case KASUMI_TOYAMA:
			return Emotes.KASUMI_TOYAMA;
		case TAE_HANAZONO:
			return Emotes.TAE_HANAZONO;
		case RIMI_USHIGOME:
			return Emotes.RIMI_USHIGOME;
		case SAAYA_YAMABUKI:
			return Emotes.SAAYA_YAMABUKI;
		case ARISA_ICHIGAYA:
			return Emotes.ARISA_ICHIGAYA;
		case RAN_MITAKE:
			return Emotes.RAN_MITAKE;
		case MOCA_AOBA:
			return Emotes.MOCA_AOBA;
		case HIMARI_UEHARA:
			return Emotes.HIMARI_UEHARA;
		case TOMOE_UDAGAWA:
			return Emotes.TOMOE_UDAGAWA;
		case TSUGUMI_HAZAWA:
			return Emotes.TSUGUMI_HAZAWA;
		case KOKORO_TSURUMAKI:
			return Emotes.KOKORO_TSURUMAKI;
		case KAORU_SETA:
			return Emotes.KAORU_SETA;
		case HAGUMI_KITAZAWA:
			return Emotes.HAGUMI_KITAZAWA;
		case KANON_MATSUBARA:
			return Emotes.KANON_MATSUBARA;
		case MISAKI_OKUSAWA:
			return Emotes.MISAKI_OKUSAWA;
		case AYA_MARUYAMA:
			return Emotes.AYA_MARUYAMA;
		case HINA_HIKAWA:
			return Emotes.HINA_HIKAWA;
		case CHISATO_SHIRASAGI:
			return Emotes.CHISATO_SHIRASAGI;
		case MAYA_YAMATO:
			return Emotes.MAYA_YAMATO;
		case EVE_WAKAMIYA:
			return Emotes.EVE_WAKAMIYA;
		case YUKINA_MINATO:
			return Emotes.YUKINA_MINATO;
		case SAYO_HIKAWA:
			return Emotes.SAYO_HIKAWA;
		case LISA_IMAI:
			return Emotes.LISA_IMAI;
		case AKO_UDAGAWA:
			return Emotes.AKO_UDAGAWA;
		case RINKO_SHIROKANE:
			return Emotes.RINKO_SHIROKANE;
		default:
			return null;
		}
	}

	public String getDisplayName() {
		String[] nameParts = this.toString().toLowerCase().split("_");
		return nameParts[0].substring(0, 1).toUpperCase() + nameParts[0].substring(1) + " "
				+ nameParts[1].substring(0, 1).toUpperCase() + nameParts[1].substring(1);
	}
}
