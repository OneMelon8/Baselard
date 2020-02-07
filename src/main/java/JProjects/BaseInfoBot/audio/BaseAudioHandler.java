package JProjects.BaseInfoBot.audio;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.TimerTask;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.commands.bandori.BandoriPlay;
import JProjects.BaseInfoBot.commands.helpers.ReactionDispatcher;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.EmbededUtil;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class BaseAudioHandler extends AudioEventAdapter implements AudioSendHandler {
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;

	private BandoriPlay instance;
	private ArrayList<QueuedAudioTrack> queue;
	private MessageChannel messageChannel;
	private Guild guild;
	private Message songInfoMessage;

	public BaseAudioHandler(AudioPlayer audioPlayer, BandoriPlay instance, MessageChannel messageChannel, Guild guild) {
		this.audioPlayer = audioPlayer;
		this.instance = instance;
		this.queue = new ArrayList<QueuedAudioTrack>();
		this.messageChannel = messageChannel;
		this.guild = guild;

		this.initTimer();
	}

	public void joinVoiceChannel(VoiceChannel channel) {
		System.out.println(GeneralTools.getTime() + " >> Attempting to join voice channel...");
		AudioManager manager = this.guild.getAudioManager();
		manager.setSendingHandler(this);
		manager.openAudioConnection(channel);
		System.out.println(GeneralTools.getTime() + " >> Successfully joined channel!");
	}

	/*
	 * Queuing Functions
	 */
	public void queueAndPlay(QueuedAudioTrack queuedTrack) {
		queueTrack(queuedTrack);
		if (this.isMusicPlaying()) {
			App.bot.sendMessage(this.getQueuedEmbeded(this.queue.indexOf(queuedTrack), queuedTrack.getTrack(),
					queuedTrack.getOwner()), this.messageChannel);
			return;
		}
		this.sendSongMessage();
		this.audioPlayer.playTrack(queuedTrack.getTrack());
		System.out.println(
				GeneralTools.getTime() + " >> Now playing: " + this.getCurrentTrack().getTrack().getInfo().title);
	}

	public void removeUserQueuedTracks(User user) {
		String userId = user.getId();
		// Don't remove the current track (is playing right now)
		for (int a = 1; a < this.queue.size(); a++) {
			if (a >= this.queue.size())
				break;
			QueuedAudioTrack qTrack = this.queue.get(a);
			if (!qTrack.isOwnedBy(userId))
				continue;
			this.queue.remove(a);
			a--;
		}
		// Vote skip
		if (getCurrentTrack().isOwnedBy(userId)) {
			Message message = App.bot.sendMessage(this.getSkipCurrentEmbeded(), this.messageChannel);
			App.bot.reactCheck(message);

			ReactionDispatcher.register(message, this.instance, 30, Emojis.CHECK);
			ReactionDispatcher.registerCleanUp(message, 30);
		}
	}

	public void queueTrack(QueuedAudioTrack queuedTrack) {
		this.queue.add(queuedTrack);
	}

	public void skipTrack() {
		App.bot.sendMessage(getSkipConfirmEmbeded(), this.messageChannel);
		this.nextTrack(true);
	}

	public void nextTrack(boolean isSkip) {
		this.editMessage(!isSkip); // Set complete rate to 100%, final edit
		this.queue.remove(0);
		if (this.queue.isEmpty()) {
			if (isSkip)
				this.audioPlayer.stopTrack();
			return;
		}
		this.sendSongMessage();
		this.audioPlayer.startTrack(this.queue.get(0).getTrack(), !isSkip);
		System.out.println(
				GeneralTools.getTime() + " >> Now playing: " + this.getCurrentTrack().getTrack().getInfo().title);
	}

	public MessageEmbed getPlayingEmbeded(QueuedAudioTrack track) {
		return getPlayingEmbeded(getCurrentTrack(), false);
	}

	public MessageEmbed getPlayingEmbeded(QueuedAudioTrack track, boolean finale) {
		if (track == null) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(BotConfig.COLOR_MISC);
			builder.setAuthor("Nothing is playing right now");
			builder.setDescription("Queue a song using /bplay <YouTube ID/sound track URL>");
			return builder.build();
		}
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Now Playing");
		builder.setDescription("Command: /bplay <YouTube ID/YouTube URL/sound track URL>");

		double max = track.getTrack().getDuration();
		double current = finale ? max : track.getTrack().getPosition();
		builder.addField(new Field(track.getTrack().getInfo().title,
				"```" + GeneralTools.getBarFancy(current, max, 21) + "\n" + String.format("%-13s %13s %7s",
						formatTime(current) + " / " + formatTime(max), "", GeneralTools.getPercentage(current, max))
						+ "```",
				false));
		builder.setFooter("Requested by " + track.getGuild().getMember(track.getOwner()).getEffectiveName(),
				track.getOwner().getAvatarUrl());
		return builder.build();
	}

	private MessageEmbed getQueuedEmbeded(int index, AudioTrack track, User user) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Song successfully queued");
		builder.setDescription("Queue position: " + index);
		builder.addField(new Field(track.getInfo().title,
				"Queued by: **" + this.guild.getMember(user).getEffectiveName() + "**\nAuthor: **"
						+ track.getInfo().author + "**\nLength: **" + this.formatTime(track.getDuration()) + "**",
				false));
		return builder.build();
	}

	private MessageEmbed getSkipCurrentEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.ORANGE);
		builder.setAuthor("Skip Current Song?");
		builder.setDescription("The person who requested this song has left the voice channel, skip?");
		builder.addField(new Field(this.getCurrentTrack().getTrack().getInfo().title,
				"Queued by: **" + this.guild.getMember(this.getCurrentTrack().getOwner()).getEffectiveName()
						+ "**\nAuthor: **" + this.getCurrentTrack().getTrack().getInfo().author + "**\nLength: **"
						+ this.formatTime(this.getCurrentTrack().getTrack().getDuration()) + "**",
				false));
		return builder.build();
	}

	private MessageEmbed getSkipConfirmEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.ORANGE);
		builder.setAuthor("Song Skipped");
		builder.setDescription(
				this.getCurrentTrack().getTrack().getInfo().title + " has been skipped by a majority vote");
		return builder.build();
	}

	public void sendSongMessage() {
		this.songInfoMessage = App.bot.sendMessage(this.getPlayingEmbeded(this.getCurrentTrack()), this.messageChannel);
	}

	private void editMessage() {
		editMessage(false);
	}

	private void editMessage(boolean finale) {
		this.songInfoMessage = App.bot.editMessage(this.songInfoMessage,
				this.getPlayingEmbeded(this.getCurrentTrack(), finale));
	}

	private String formatTime(double ms) {
		long minutes = (long) ((ms / 1000) / 60);
		long seconds = (long) ((ms / 1000) % 60);
		return minutes + ":" + String.format("%02d", seconds);
	}

	private void initTimer() {
		App.bot.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (queue.isEmpty() || songInfoMessage == null)
					return;
				editMessage();
			}
		}, 0, 7 * 1000);
	}

	/*
	 * Audio Events
	 */
	@Override
	public void onPlayerPause(AudioPlayer player) {
		// Player was paused
		System.out.println("pause!");
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		// Player was resumed
		System.out.println("resume!");
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		// A track started playing
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext)
			this.nextTrack(false);
		// endReason == FINISHED: A track finished or died by an exception (mayStartNext
		// = true).
		// endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
		// endReason == STOPPED: The player was stopped.
		// endReason == REPLACED: Another track started playing while this had not
		// finished
		// endReason == CLEANUP: Player hasn't been queried for a while, if you want you
		// can put a
		// clone of this back to your queue
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, final FriendlyException exception) {
		// An already playing track threw an exception (track end event will still be
		// received separately)
		App.bot.timer.schedule(new TimerTask() {
			@Override
			public void run() {
				songInfoMessage = App.bot.editMessage(songInfoMessage,
						EmbededUtil.getErrorEmbeded(exception.getMessage()));
			}
		}, 1000);
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// Audio track has been unable to provide us any audio, might want to just start
		// a new track
		System.out.println("stuck!");
	}

	/*
	 * Util Methods:
	 */
	public boolean isMusicPlaying() {
		return this.guild.getSelfMember().getVoiceState().inVoiceChannel() && audioPlayer.getPlayingTrack() != null;
	}

	public boolean isInVoiceChannel(Guild guild) {
		return guild.getSelfMember().getVoiceState().inVoiceChannel();
	}

	/*
	 * Sender Handler Methods
	 */
	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
		return lastFrame != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		if (lastFrame == null)
			lastFrame = audioPlayer.provide();
		byte[] data = lastFrame != null ? lastFrame.getData() : null;
		lastFrame = null;
		return ByteBuffer.wrap(data);
	}

	@Override
	public boolean isOpus() {
		return true;
	}

	/*
	 * Getters
	 */
	public AudioPlayer getPlayer() {
		return this.audioPlayer;
	}

	public QueuedAudioTrack getCurrentTrack() {
		if (this.queue.isEmpty())
			return null;
		return this.queue.get(0);
	}
}