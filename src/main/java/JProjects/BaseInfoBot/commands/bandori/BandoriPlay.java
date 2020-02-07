package JProjects.BaseInfoBot.commands.bandori;

import java.awt.Color;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.audio.BaseAudioHandler;
import JProjects.BaseInfoBot.audio.QueuedAudioTrack;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.commands.helpers.ReactionDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionHandler;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.files.assets.BandoriImageAssets;
import JProjects.BaseInfoBot.spider.ImgbbSpider;
import JProjects.BaseInfoBot.tools.EmbededUtil;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class BandoriPlay extends CommandHandler implements ReactionHandler {

	private AudioPlayer player;
	private static BaseAudioHandler handler;

	private VoiceChannel defaultVoiceChannel;

	public BandoriPlay(BaseInfoBot bot) {
		super(bot, "bp", new String[] { "bandoriplay", "bplay", "play" },
				"Interface for playing Bandori songs in voice channels");
	}

	@Override
	public void onCommand(final User author, String command, String[] args, Message message,
			final MessageChannel channel, final Guild guild) {
		// Setup
		if (this.defaultVoiceChannel == null) {
			// Alpha Test: 552017107047677964 Beta Test: 535503190947397647
			// Official: 610198724613767228
			this.defaultVoiceChannel = bot.getJDA().getVoiceChannelById("553439564602277901");
		}

		bot.sendThinkingPacket(channel);
		if (player == null)
			setup(channel, guild);

		if (args.length == 0) {
			handler.sendSongMessage();
			return;
		} else if (args.length == 10) {
			guild.getAudioManager().closeAudioConnection();
			return;
		} else if (args.length == 11) {
			System.exit(0);
			return;
		}

		// Ignore request if not in selected voice channel
		if (!this.defaultVoiceChannel.getMembers().contains(guild.getMember(author))) {
			bot.sendMessage(getNotInVcEmbeded(), channel);
			return;
		}

		// Load audio
		String query = args[0];
		bot.audioManager.loadItem(query, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				System.out.println(GeneralTools.getTime() + " >> Loaded track " + track.getInfo().title + "!");
				if (!handler.isInVoiceChannel(guild))
					handler.joinVoiceChannel(defaultVoiceChannel);
				handler.queueAndPlay(new QueuedAudioTrack(author.getId(), track, guild.getId()));
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
//				for (AudioTrack track : playlist.getTracks())
//					scheduler.queue(track);
//					audioPlayer.playTrack(track);
//					System.out.println(track + "!!!");
				bot.sendMessage(EmbededUtil.getNotSupportedEmbeded("Playlists are not currently supported (WIP)"),
						channel);
			}

			@Override
			public void noMatches() {
				// Notify the user that we've got nothing
				bot.sendMessage(getNotFoundEmbeded(), channel);
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				// Notify the user that everything exploded
				bot.sendMessage(EmbededUtil.getErrorEmbeded(exception.getMessage()), channel);
			}
		});

	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		String emoteName = emote.getName();
		if (emoteName.equals(Emojis.CHECK)) {
			// Skip current song
			if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
				return;
			MessageEmbed msgEmbeded = message.getEmbeds().get(0);
			if (!msgEmbeded.getAuthor().getName().equals("Skip Current Song?"))
				return;
			List<Member> inVc = defaultVoiceChannel.getMembers();
			if (!inVc.contains(guild.getMember(user))) {
				ReactionDispatcher.register(message, this, 30, Emojis.CHECK);
				ReactionDispatcher.registerCleanUp(message, 30);
				return;
			}

			int reactCount = 0, vcCount = inVc.size();
			for (MessageReaction r : message.getReactions()) {
				if (!r.getReactionEmote().getName().equals(emoteName))
					continue;
				List<User> uList = r.retrieveUsers().complete();
				for (User u : uList)
					if (inVc.contains(guild.getMember(u)))
						reactCount++;
			}
			if (1D * reactCount / vcCount < 0.5) {
				ReactionDispatcher.register(message, this, 30, Emojis.CHECK);
				ReactionDispatcher.registerCleanUp(message, 30);
				return;
			}
			handler.skipTrack();
		}

	}

	public static void onUserLeave(User user, VoiceChannel channelLeft, Guild guild) {
		handler.removeUserQueuedTracks(user);
	}

	public void setup(MessageChannel channel, Guild guild) {
		System.out.println(GeneralTools.getTime() + " >> Setting up audio player...");
		player = bot.audioManager.createPlayer();
		handler = new BaseAudioHandler(player, this, channel, guild);
		player.addListener(handler);
//		player.setVolume(60);
		System.out.println(GeneralTools.getTime() + " >> Setup complete!");
	}

	public static MessageEmbed getNotInVcEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("Not in Voice Channel");
		builder.setDescription("Join the \"Multilive VC\" channel to start queuing music");
		return builder.build();
	}

	public static MessageEmbed getNotFoundEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("DJ Michelle cannot find the music");
		builder.setDescription("Try using a YouTube ID/Youtube URL/soundtrack URL instead");
		builder.setThumbnail(ImgbbSpider.uploadImage(BandoriImageAssets.getImage(BandoriImageAssets.MICHELLE_ERROR)));
		return builder.build();
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Player Template");
		builder.setDescription("Use the following template to use the Bandori player interface");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " [search...]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " (shows current list)\n"
				+ BotConfig.PREFIX + command + " senbonzakura```", false));
		return builder.build();
	}

}
