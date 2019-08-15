package JProjects.BaseInfoBot.database.bandori;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.bandori.BandoriMultiLive;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.spider.ImgbbSpider;
import JProjects.BaseInfoBot.tools.ImageTools;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class BandoriRoom {

	private static final BaseInfoBot bot = App.bot;

	private String id;
	private User creator;
	private long creationTime, lastActiveTime;
	private ArrayList<String> participants;

	private final int capacity = 5;

	public BandoriRoom(String id, User creator) {
		this.id = id;
		this.creator = creator;
		this.creationTime = this.lastActiveTime = System.currentTimeMillis();

		this.participants = new ArrayList<String>();
		this.participants.add(creator.getId());
	}

	public MessageEmbed getEmbededMessage() {
		updateLastActiveTime();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor(this.creator.getName() + "'s Multi-Live Room");
		builder.setDescription("Who is ready for a round of BanG Dream multi-live?");
		builder.setThumbnail("https://cdn.discordapp.com/emojis/578806302864572427.png?v=1");

		StringBuilder sb = new StringBuilder();
		sb.append("Room ID: **" + this.id + "**");
		sb.append("\nCreator: **" + this.creator.getAsTag() + "**");
		sb.append("\nTime: **" + TimeFormatter.getCountDownSimple(System.currentTimeMillis() - this.creationTime)
				+ " ago**");
		builder.addField(new Field("**Room Information:**", sb.toString(), false));

		sb = new StringBuilder("```");
		for (String userId : this.participants) {
			User user = bot.getUserById(userId);
			sb.append(user.getName() + ", ");
		}
		if (sb.length() != 3)
			sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("**Participants (" + this.getParticipantsCount() + "):**", sb.toString(), false));
		if (this.getParticipantsCount() < this.getCapacity())
			builder.addField(new Field("",
					"Type `/multi join " + this.id + "` or react with " + Emotes.LIVE_BOOST + " to join", false));
		builder.setImage(ImgbbSpider.uploadImage(getRoomImage()));
		builder.setFooter(String.valueOf(this.getId()), "https://cdn.discordapp.com/emojis/432981158670630924.png");
		return builder.build();
	}

	public BufferedImage getRoomImage() {
		updateLastActiveTime();
		try {
			return ImageTools.mergeBandoriRoom(this.participants);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean join(User user, Message message, MessageChannel channel, BandoriMultiLive listener) {
		updateLastActiveTime();
		if (this.participants.contains(user.getId()) || this.getParticipantsCount() + 1 > this.getCapacity())
			return false;
		this.participants.add(user.getId());

		if (message == null) {
			bot.sendMessage(user.getAsMention() + " Successfully joined " + this.creator.getName() + "'s room ("
					+ this.getParticipantsCountDisplay() + ")", channel);
			return true;
		}
		// bot.removeAllReactions(message);

		message = bot.editMessage(message, this.getEmbededMessage());
		EmoteDispatcher.register(message, listener, "live_boost");
		return true;
	}

	public boolean leave(User user, MessageChannel channel) {
		updateLastActiveTime();
		if (!this.participants.contains(user.getId()))
			return false;

		this.participants.remove(user.getId());
		return true;
	}

	public boolean transfer(String fromId, String toId) {
		updateLastActiveTime();
		if (!this.participants.contains(toId))
			return false;
		this.setCreator(bot.getUserById(toId));
		Collections.swap(this.participants, 0, this.participants.indexOf(toId));
		return true;
	}

	public String getPingMessage(User pinger) {
		updateLastActiveTime();
		StringBuilder sb = new StringBuilder();
		for (String id : this.getParticipants()) {
			User user = bot.getUserById(id);
			sb.append(user.getAsMention() + " ");
		}
		if (sb.length() != 0)
			sb.deleteCharAt(sb.length() - 1);
		return "Room ping by " + pinger.getName() + ":\n" + sb.toString();
	}

	public void updateLastActiveTime() {
		this.lastActiveTime = System.currentTimeMillis();
	}

	/*
	 * Getters and Setters
	 */
	public String getId() {
		return id;
	}

	public User getCreator() {
		return creator;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public long getLastActiveTime() {
		return lastActiveTime;
	}

	public ArrayList<String> getParticipants() {
		return participants;
	}

	public int getParticipantsCount() {
		return participants.size();
	}

	public String getParticipantsCountDisplay() {
		return this.getParticipantsCount() == this.capacity ? "Full" : getParticipantsCount() + "/" + getCapacity();
	}

	public String getParticipantsDisplay(Guild guild) {
		StringBuilder sb = new StringBuilder();
		for (String id : this.participants)
			sb.append(bot.getUserDisplayName(id, guild) + ", ");
		if (sb.length() != 0)
			sb.delete(sb.length() - 2, sb.length());
		else
			sb.append("Empty");
		return sb.toString();
	}

	public String getParticipantsDisplayTaskBar(Guild guild) {
		StringBuilder sb = new StringBuilder();
		if (this.getParticipantsCount() == this.getCapacity())
			return "full";
		for (String id : this.participants)
			sb.append(bot.getUserDisplayName(id, guild) + ", ");
		if (sb.length() != 0)
			sb.delete(sb.length() - 2, sb.length());
		else
			sb.append("Empty");
		return sb.toString();
	}

	public int getCapacity() {
		return capacity;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

}
