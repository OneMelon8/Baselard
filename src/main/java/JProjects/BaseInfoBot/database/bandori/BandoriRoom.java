package JProjects.BaseInfoBot.database.bandori;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.ImageTools;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class BandoriRoom {

	private static final BaseInfoBot bot = App.bot;

	private String id;
	private User creator;
	private long creationTime;
	private ArrayList<String> participants;

	private final int capacity = 5;

	public BandoriRoom(String id, User creator) {
		this.id = id;
		this.creator = creator;
		this.creationTime = System.currentTimeMillis();

		this.participants = new ArrayList<String>();
		this.participants.add(creator.getId());
	}

	public void sendEmbededMessage(MessageChannel channel) {
		MessageBuilder mb = new MessageBuilder();
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
		builder.addField(new Field("**Participants (" + this.participants.size() + "):**", sb.toString(), false));

		builder.addField(new Field("",
				"Type `/multi join " + this.id + "` or react with " + Emotes.LIVE_BOOST + " to join", false));

		builder.setImage("attachment://temp.png");

		mb.setEmbed(builder.build());
		Message msg = App.bot.sendFile(getRoomImage(), mb.build(), channel);
		bot.addReaction(msg, bot.getJDA().getEmoteById(Emotes.getId(Emotes.LIVE_BOOST)));
	}

	public File getRoomImage() {
		try {
			return ImageTools.mergeBandoriRoom(this.participants);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void join(User user, Message message) {
		if (this.participants.contains(user.getId()))
			return;
		this.participants.add(user.getId());
		// TODO: edit message
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

	public ArrayList<String> getWaiting() {
		return participants;
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

	public void setWaiting(ArrayList<String> waiting) {
		this.participants = waiting;
	}
}
