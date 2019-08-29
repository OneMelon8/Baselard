package JProjects.BaseInfoBot.database.bandori;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.config.BandoriConfig;
import JProjects.BaseInfoBot.spider.ImgbbSpider;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.ImageTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriCard {

	private int index = 0; // Only used in card querying
	private int total = 0; // Only used in card querying
	private String notes = "Happy! Lucky!"; // Only used in card querying

	private String name;
	private BandoriAttribute attr;
	private BandoriMember member;
	private Color color;
	private int rarity;
	private String versions;
	private String skillName;
	private BandoriSkillType skillType;
	private String skillDesc;
	private String iconUrl;
	private String iconUrl2;
	private String artUrl;
	private String artUrl2;
	private String chibiUrl;
	private String chibiUrl2;
	private String chibiUrl3;

	private int performance;
	private int technique;
	private int visual;
	private int overall;

	public BandoriCard() {
		this.notes = "Happy! Lucky! Smile! Yay!";
	}

	public BandoriCard(String name, BandoriAttribute attr, BandoriMember member, int rarity) {
		this();
		this.name = name;
		this.attr = attr;
		this.member = member;
		this.rarity = rarity;

		this.notes = this.member.getCatchPhrase();
	}

	public BandoriCard(String name, BandoriAttribute attr, BandoriMember member, int rarity, String versions,
			String skillName, String skillDesc, String iconUrl, String artUrl) {
		this(name, attr, member, rarity);
		this.versions = versions;
		this.skillName = skillName;
		this.skillDesc = skillDesc;
		this.iconUrl = iconUrl;
		this.artUrl = artUrl;

		this.performance = 0;
		this.technique = 0;
		this.visual = 0;
		this.overall = this.performance + this.technique + this.visual;
	}

	public MessageEmbed getEmbededMessage(boolean showFooter) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getColor());
		builder.setAuthor(this.getName());
		builder.setThumbnail(this.getIconUrl());

		StringBuilder sb = new StringBuilder();
		sb.append("Versions: " + this.getVersionsEmotes());
		sb.append("\nSkill: **" + this.getSkillName() + "**");
		sb.append("\nDetail: **" + this.getSkillType().getEmote() + " "
				+ (this.getMember() == null ? this.getSkillDescRaw() : this.getSkillDesc()) + "**");
		builder.addField(new Field((this.getMember() == null ? "" : this.getMember().getEmote() + "・")
				+ this.getAttr().getEmote() + "・" + this.getRarityStars(), sb.toString(), false));
		if (showFooter)
			builder.setFooter((index + 1) + "/" + (total == 0 ? index + 1 : total) + " - " + notes,
					BandoriConfig.URL_CRAFT_EGG);
		return builder.build();
	}

	public MessageEmbed getDetailedEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getColor());
		builder.setAuthor(this.getName());
		builder.setThumbnail(this.getIconUrl());

		StringBuilder sb = new StringBuilder();
		sb.append("Versions: " + this.getVersionsEmotes());
		if (this.getMember() != null)
			sb.append("\nMember: **" + this.getMember().getEmote() + " " + this.getMember().getDisplayName() + "**");
		sb.append("\nRarity: **" + this.getRarityStars() + "**");
		sb.append("\nAttribute: **" + this.getAttr().getEmote() + " " + this.getAttr().getDisplayName() + "**");
		builder.addField(new Field("**Information:**", sb.toString(), false));

		sb = new StringBuilder();
		sb.append("Type: **" + this.getSkillType().getEmote() + " " + this.getSkillType().getDisplayName() + "**");
		sb.append("\nName: **" + this.getSkillName() + "**");
		sb.append("\nDesc: **" + (this.getMember() == null ? this.getSkillDescRaw() : this.getSkillDesc()) + "**");
		builder.addField(new Field("**Skill Info:**", sb.toString(), false));

		sb = new StringBuilder();
		sb.append("Performance: **"
				+ (this.getPerformance() >= BandoriConfig.PERFORMANCE_MAX ? "MAX" : this.getPerformance()) + "**");
		sb.append("\nTechnique: **" + (this.getTechnique() >= BandoriConfig.TECHNIQUE_MAX ? "MAX" : this.getTechnique())
				+ "**");
		sb.append("\nVisual: **" + (this.getVisual() >= BandoriConfig.VISUAL_MAX ? "MAX" : this.getVisual()) + "**");
		sb.append(
				"\nOverall: **" + (this.getOverall() >= BandoriConfig.OVERALL_MAX ? "MAX" : this.getOverall()) + "**");
		builder.addField(new Field("**Statistics:**", sb.toString(), true));

		sb = new StringBuilder();
		sb.append("`"
				+ GeneralTools.getBar(this.getPerformance(), BandoriConfig.PERFORMANCE_MAX, BandoriConfig.BAR_LENGTH)
				+ "`");
		sb.append(
				"\n`" + GeneralTools.getBar(this.getTechnique(), BandoriConfig.TECHNIQUE_MAX, BandoriConfig.BAR_LENGTH)
						+ "`");
		sb.append("\n`" + GeneralTools.getBar(this.getVisual(), BandoriConfig.VISUAL_MAX, BandoriConfig.BAR_LENGTH)
				+ "`");
		sb.append("\n`" + GeneralTools.getBar(this.getOverall(), BandoriConfig.OVERALL_MAX, BandoriConfig.BAR_LENGTH)
				+ "`");
		builder.addField(new Field("**Graph:**", sb.toString(), true));

		builder.setFooter((index + 1) + "/" + (total == 0 ? index + 1 : total) + " - " + notes,
				BandoriConfig.URL_CRAFT_EGG);
		return builder.build();
	}

	public MessageEmbed getArtworksEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getColor());
		builder.setAuthor(this.getName());
		builder.setDescription("Artworks for " + this.getName());
		String url = ImgbbSpider.uploadImage(getArtworks());
		if (url != null)
			builder.setImage(url);
		else
			builder.setDescription("There are no artworks for " + this.getName());
		builder.setFooter((index + 1) + "/" + (total == 0 ? index + 1 : total) + " - " + notes,
				BandoriConfig.URL_CRAFT_EGG);
		return builder.build();
	}

	public MessageEmbed getChibisEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getColor());
		builder.setAuthor(this.getName());
		builder.setDescription("Stage outfits for " + this.getName());
		String url = ImgbbSpider.uploadImage(getChibis());
		if (url != null)
			builder.setImage(url);
		else
			builder.setDescription("There are no stage outfits for " + this.getName());
		builder.setFooter((index + 1) + "/" + (total == 0 ? index + 1 : total) + " - " + notes,
				BandoriConfig.URL_CRAFT_EGG);
		return builder.build();
	}

	public BufferedImage getArtworks() {
		try {
			return ImageTools.mergeHoriz(this.artUrl, this.artUrl2);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public BufferedImage getChibis() {
		try {
			return ImageTools.mergeHoriz(this.chibiUrl, this.chibiUrl2, this.chibiUrl3);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public String getRarityStars() {
		StringBuilder sb = new StringBuilder();
		if (this.rarity >= 3)
			for (int a = 0; a < this.rarity; a++)
				sb.append(Emotes.BANDORI_STAR_PREMIUM);
		else
			for (int a = 0; a < this.rarity; a++)
				sb.append(Emotes.BANDORI_STAR);
		return sb.toString();
	}

	public int getIndex() {
		return index;
	}

	public int getTotal() {
		return total;
	}

	public String getNotes() {
		return notes;
	}

	public String getName() {
		return name;
	}

	public BandoriAttribute getAttr() {
		return attr;
	}

	public BandoriMember getMember() {
		return member;
	}

	public Color getColor() {
		return color;
	}

	public int getRarity() {
		return rarity;
	}

	public String getVersions() {
		return versions;
	}

	public String getVersionsEmotes() {
		StringBuilder sb = new StringBuilder();
		if (versions.contains("Japanese"))
			sb.append("🇯🇵 ");
		if (versions.contains("English"))
			sb.append("🇺🇸 ");
		if (versions.contains("Taiwanese"))
			sb.append("🇹🇼 ");
		if (versions.contains("Korean"))
			sb.append("🇰🇷 ");
		if (sb.length() > 1)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public String getSkillName() {
		return skillName;
	}

	public BandoriSkillType getSkillType() {
		return skillType;
	}

	public String getSkillDesc() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String s : this.skillDesc.replace("(Score up)", "").split(" ")) {
			if (count++ < 2)
				continue;
			boolean ital = false;
			if (s.matches(".*\\d.*") || s.contains("PERFECT") || s.contains("GREAT") || s.contains("GOOD")
					|| s.contains("BAD") || s.contains("MISS"))
				ital = true;
			sb.append((ital ? "*" + s + "*" : s) + " ");
		}
		return sb.toString().trim();
	}

	public String getSkillDescRaw() {
		return this.skillDesc;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public String getIconUrl2() {
		return iconUrl2;
	}

	public String getArtUrl() {
		return artUrl;
	}

	public String getArtUrl2() {
		return artUrl2;
	}

	public String getChibiUrl() {
		return chibiUrl;
	}

	public String getChibiUrl2() {
		return chibiUrl2;
	}

	public String getChibiUrl3() {
		return chibiUrl3;
	}

	public int getPerformance() {
		return performance;
	}

	public int getTechnique() {
		return technique;
	}

	public int getVisual() {
		return visual;
	}

	public int getOverall() {
		return overall;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAttr(BandoriAttribute attr) {
		this.attr = attr;
	}

	public void setMember(BandoriMember member) {
		this.member = member;
		this.notes = this.member.getCatchPhrase();
	}

	public void setColor(String color) {
		color = color.replace("#", "");
		this.color = new Color(Integer.valueOf(color.substring(0, 2), 16), Integer.valueOf(color.substring(2, 4), 16),
				Integer.valueOf(color.substring(4, 6), 16));
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setRarity(int rarity) {
		this.rarity = rarity;
	}

	public void setVersions(String versions) {
		this.versions = versions;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public void setSkillDesc(String skillDesc) {
		this.skillDesc = skillDesc;
		this.skillType = BandoriSkillType.fromClutterString(skillDesc);
	}

	public void setSkillType(BandoriSkillType type) {
		this.skillType = type;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public void setIconUrl2(String iconUrl2) {
		this.iconUrl2 = iconUrl2;
	}

	public void setArtUrl(String artUrl) {
		this.artUrl = artUrl;
	}

	public void setArtUrl2(String artUrl2) {
		this.artUrl2 = artUrl2;
	}

	public void setChibiUrl(String chibiUrl) {
		this.chibiUrl = chibiUrl;
	}

	public void setChibiUrl2(String chibiUrl2) {
		this.chibiUrl2 = chibiUrl2;
	}

	public void setChibiUrl3(String chibiUrl3) {
		this.chibiUrl3 = chibiUrl3;
	}

	public void setPerformance(int performance) {
		this.performance = performance;
		this.overall = this.performance + this.technique + this.visual;
	}

	public void setTechnique(int technique) {
		this.technique = technique;
		this.overall = this.performance + this.technique + this.visual;
	}

	public void setVisual(int visual) {
		this.visual = visual;
		this.overall = this.performance + this.technique + this.visual;
	}
}
