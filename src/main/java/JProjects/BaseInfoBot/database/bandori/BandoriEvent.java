package JProjects.BaseInfoBot.database.bandori;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class BandoriEvent {

	private int index = 0; // Only used in querying
	private int total = 0; // Only used in querying

	// Basic info
	private String name;
	private Calendar startTime;
	private Calendar endTime;
	// Advanced info
	private BandoriEventType type;
	private BandoriAttribute attribute;
	private ArrayList<BandoriMember> band;
	private String imageUrl;
	// Extra info
	private ArrayList<BandoriCard> cards;

	public BandoriEvent(String name, Calendar startTime, Calendar endTime) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public BandoriEvent(String name, Calendar startTime, Calendar endTime, BandoriEventType type,
			BandoriAttribute attribute, ArrayList<BandoriMember> band, String imageUrl) {
		this(name, startTime, endTime);
		this.type = type;
		this.attribute = attribute;
		this.band = band;
		this.imageUrl = imageUrl;
	}

	public BandoriEvent(String name, Calendar startTime, Calendar endTime, BandoriEventType type,
			BandoriAttribute attribute, ArrayList<BandoriMember> band, String imageUrl, ArrayList<BandoriCard> cards) {
		this(name, startTime, endTime, type, attribute, band, imageUrl);
		this.cards = cards;
	}

	public MessageEmbed getSimpleEmbededMessage() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor(this.getName());
		if (this.startTime == this.endTime)
			builder.setDescription("Start: UTC " + TimeFormatter.formatCalendar(this.getStartTime()));
		else
			builder.setDescription("UTC: " + TimeFormatter.formatCalendar(this.getStartTime()) + " - "
					+ TimeFormatter.formatCalendar(this.getEndTime()));

		StringBuilder sb = new StringBuilder();
		sb.append(TimeFormatter.getCountDown(this.getStartTime().getTimeInMillis(), this.getEndTime().getTimeInMillis(),
				System.currentTimeMillis(), false));
		builder.addField(
				new Field(this.getAttribute().getEmote() + "・" + this.getType().getEmote() + "・" + this.getBandEmote(),
						sb.toString(), false));
		builder.setFooter((index + 1) + "/" + (total == 0 ? index + 1 : total),
				"https://cdn.discordapp.com/emojis/432981158670630924.png");
		return builder.build();
	}

	public List<MessageEmbed> getEmbededMessages() {
		List<MessageEmbed> output = new ArrayList<MessageEmbed>();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor(this.getName());
		if (this.startTime == this.endTime)
			builder.setDescription("Start: UTC " + TimeFormatter.formatCalendar(this.getStartTime()));
		else
			builder.setDescription("UTC: " + TimeFormatter.formatCalendar(this.getStartTime()) + " - "
					+ TimeFormatter.formatCalendar(this.getEndTime()));
		builder.setImage(this.getImageUrl());

		StringBuilder sb = new StringBuilder();
		sb.append("Countdown: **" + this.getCountdown(false) + "**");
		sb.append("\nEvent Type: **" + this.getType().getEmote() + " " + this.getType().getDisplayName() + "**");
		sb.append(
				"\nAttribute: **" + this.getAttribute().getEmote() + " " + this.getAttribute().getDisplayName() + "**");
		sb.append("\nPref Band: **");
		for (BandoriMember member : this.band)
			sb.append(member.getEmote());
		sb.append("**");
		builder.addField(new Field("Event Information:", sb.toString(), false));
		output.add(builder.build());

		for (BandoriCard card : this.cards)
			output.add(card.getEmbededMessage(false));
		return output;
	}

	private String getBandEmote() {
		StringBuilder sb = new StringBuilder();
		for (BandoriMember member : this.band)
			sb.append(member.getEmote());
		return sb.toString();
	}

	public String getCountdown(boolean exact) {
		return TimeFormatter.getCountDown(this.getStartTime().getTimeInMillis(), this.getEndTime().getTimeInMillis(),
				System.currentTimeMillis(), exact);
	}

	// GS
	public String getName() {
		return name;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public BandoriEventType getType() {
		return type;
	}

	public BandoriAttribute getAttribute() {
		return attribute;
	}

	public ArrayList<BandoriMember> getBand() {
		return band;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public ArrayList<BandoriCard> getCards() {
		return cards;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public void setType(BandoriEventType type) {
		this.type = type;
	}

	public void setAttribute(BandoriAttribute attribute) {
		this.attribute = attribute;
	}

	public void setBand(ArrayList<BandoriMember> band) {
		this.band = band;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setCards(ArrayList<BandoriCard> cards) {
		this.cards = cards;
	}

}
