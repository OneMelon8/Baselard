package JProjects.BaseInfoBot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import JProjects.BaseInfoBot.App;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class QueuedAudioTrack {

	private String ownerId;
	private AudioTrack track;
	private String guildId;

	public QueuedAudioTrack(String ownerId, AudioTrack track, String guildId) {
		this.ownerId = ownerId;
		this.track = track;
		this.guildId = guildId;
	}

	public User getOwner() {
		return App.bot.getUserById(this.getOwnerId());
	}

	public boolean isOwnedBy(String ownerId) {
		return ownerId.equals(this.getOwnerId());
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	public AudioTrack getTrack() {
		return track;
	}

	public Guild getGuild() {
		return App.bot.getGuildById(this.getGuildId());
	}

	public String getGuildId() {
		return this.guildId;
	}
}
