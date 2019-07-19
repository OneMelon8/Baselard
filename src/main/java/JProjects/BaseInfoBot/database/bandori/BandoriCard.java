package JProjects.BaseInfoBot.database.bandori;

import java.awt.Color;

import JProjects.BaseInfoBot.database.Emotes;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriCard {

	private int index = 0; // Only used in card querying
	private int total = 0; // Only used in card querying
	private String notes = "Happy! Lucky! Smile! Yay!"; // Only used in card querying

	private String name;
	private BandoriAttribute attr;
	private BandoriMember member;
	private Color color;
	private int rarity;
	private String versions;
	private String skillName;
	private String skillDesc;
	private String iconUrl;
	private String artUrl;

	private int performance;
	private int technique;
	private int visual;
	private int overall;

	public BandoriCard(String name, BandoriAttribute attr, BandoriMember member, int rarity, String versions,
			String skillName, String skillDesc, String iconUrl, String artUrl) {
		this.name = name;
		this.attr = attr;
		this.member = member;
		this.rarity = rarity;
		this.versions = versions;
		this.skillName = skillName;
		this.skillDesc = skillDesc;
		this.iconUrl = iconUrl;
		this.artUrl = artUrl;

		this.performance = 0;
		this.technique = 0;
		this.visual = 0;
		this.overall = this.performance + this.technique + this.visual;

		this.notes = this.member.getCatchPhrase();
	}

	public MessageEmbed getEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(this.getColor());
		builder.setAuthor(this.getName());
		builder.setThumbnail(this.getIconUrl());
//		builder.setImage(this.getArtUrl());

		StringBuilder sb = new StringBuilder();
//		sb.append("Member: **" + this.getMember().getEmote() + "**");
//		sb.append("\nRarity: **" + this.getRarityStars() + "**");
//		sb.append("\nAttribute: **" + this.getAttr().getEmote() + "**");
//		builder.addField(new Field("**Information:**", sb.toString(), false));

//		sb = new StringBuilder();
		sb.append("Versions: " + this.getVersionsEmotes());
		sb.append("\nSkill: **" + this.getSkillName() + "**");
		sb.append("\nDetail: **" + this.getSkillDesc() + "**");
//		builder.addField(new Field("**Skill Info:**", sb.toString(), false));
		builder.addField(
				new Field(this.getMember().getEmote() + "・" + this.getAttr().getEmote() + "・" + this.getRarityStars(),
						sb.toString(), false));

//		sb = new StringBuilder();
//		sb.append("Performance: **" + this.getPerformance() + "**");
//		sb.append("\nTechnique: **" + this.getTechnique() + "**");
//		sb.append("\nVisual: **" + this.getVisual() + "**");
//		sb.append("\nOverall: **" + this.getOverall() + "**");
//		builder.addField(new Field("**Statistics:**", sb.toString(), false));

		builder.setFooter((index + 1) + "/" + (total == 0 ? index + 1 : total) + " - " + notes,
				"https://cdn.discordapp.com/emojis/432981158670630924.png");
		return builder.build();
	}

	public String getRarityStars() {
		StringBuilder sb = new StringBuilder();
		if (this.rarity >= 3)
			for (int a = 0; a < this.rarity; a++)
				sb.append(Emotes.BANDORI_STAR);
		else
			for (int a = 0; a < this.rarity; a++)
				sb.append(Emotes.BANDORI_STAR2);
		return sb.toString();
	}

	// Default Generated
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

	public String getSkillDesc() {
		return skillDesc;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public String getArtUrl() {
		return artUrl;
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
	}

	public void setColor(String color) {
		color = color.replace("#", "");
		this.color = new Color(Integer.valueOf(color.substring(0, 2), 16), Integer.valueOf(color.substring(2, 4), 16),
				Integer.valueOf(color.substring(4, 6), 16));
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
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public void setArtUrl(String artUrl) {
		this.artUrl = artUrl;
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
