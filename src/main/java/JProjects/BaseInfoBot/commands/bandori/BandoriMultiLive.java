package JProjects.BaseInfoBot.commands.bandori;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimerTask;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.ChatIntent;
import JProjects.BaseInfoBot.commands.helpers.ChatIntentDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ChatIntentHandler;
import JProjects.BaseInfoBot.commands.helpers.ChatIntentPattern;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.commands.helpers.ReactionDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionHandler;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.bandori.BandoriRoom;
import JProjects.BaseInfoBot.database.config.BandoriConfig;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BandoriMultiLive extends CommandHandler implements ReactionHandler, ChatIntentHandler {

	// SERVER ID >> {CREATOR ID >> ROOM}
	public static HashMap<String, HashMap<String, BandoriRoom>> multiRooms = new HashMap<String, HashMap<String, BandoriRoom>>();

	// MESSAGE WITH REACTION >> INTENT
	public static HashMap<String, ChatIntent> reactionIntent = new HashMap<String, ChatIntent>();

	public BandoriMultiLive(BaseInfoBot bot) {
		super(bot, "multi", new String[] { "mul", "m" }, "Parent command for multi-live features");
		ChatIntentDispatcher.register(ChatIntentPattern.BANDORI_CREATE_ROOM, this);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		updateChannelTopic(guild);
		bot.sendThinkingPacket(channel);
		if (args.length == 0) {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}

		String sub = args[0];
		String authorId = author.getId();
		List<User> pingedUsers = message.getMentionedUsers();
		if (args.length == 2 && (sub.equals("create") || sub.equals("c") || sub.equals("host"))) {
			if (!idCheck(args[1])) {
				bot.sendMessage(author.getAsMention() + " Invalid room ID!", channel);
				return;
			}

			if (disband(authorId, guild))
				bot.sendMessage(author.getAsMention() + " Successfully disbanded your previous room!", channel);
			leave(author, channel, guild);

			BandoriRoom room = new BandoriRoom(args[1], author);
			Role readyRole = guild.getRolesByName("Bang Dream", true).get(0);
			bot.sendMessage(readyRole.getAsMention() + " " + author.getAsMention() + " has created a multi-live room!",
					channel);
			Message msg = bot.sendMessage(room.getEmbededMessage(), channel);
			bot.addReaction(msg, bot.getEmote(Emotes.LIVE_BOOST));

			HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
			multiRooms.put(authorId, room);
			updateOrCreateServerRooms(guild, multiRooms);

			ReactionDispatcher.register(msg, this, 300, "live_boost");
			ReactionDispatcher.registerCleanUp(msg, 300);
		} else if (args.length == 1 && (sub.equals("list") || sub.equals("l") || sub.equals("rooms"))) {
			bot.sendMessage(getRoomList(guild), channel);
		} else if (args.length == 1
				&& (sub.equals("show") || sub.equals("s") || sub.equals("display") || sub.contentEquals("d"))) {
			BandoriRoom room = getRoomByUser(authorId, guild);
			if (room == null) {
				bot.sendMessage(author.getAsMention() + " You are not in a multi-live room!", channel);
				return;
			}
			message = bot.sendMessage(room.getEmbededMessage(), channel);

			// Show boost symbol if still join-able
			if (room.getParticipantsCount() < room.getCapacity()) {
				bot.addReaction(message, bot.getEmote(Emotes.LIVE_BOOST));
				ReactionDispatcher.register(message, this, 60, "live_boost");
				ReactionDispatcher.registerCleanUp(message, 60);
			}
		} else if (args.length == 1 && (sub.equals("ping") || sub.equals("mention"))) {
			BandoriRoom room = getRoomByUser(authorId, guild);
			if (room == null) {
				Role readyRole = guild.getRolesByName("Bang Dream", true).get(0);
				bot.sendMessage("Ready ping by " + author.getAsMention() + ":\n" + readyRole.getAsMention(), channel);
				return;
			}
			bot.sendMessage(room.getPingMessage(author), channel);
		} else if (args.length == 1 && (sub.equals("disband") || sub.equals("delete") || sub.equals("del"))) {
			if (disband(authorId, guild))
				bot.sendMessage(author.getAsMention() + " Successfully disbanded the room!", channel);
			else
				bot.sendMessage(author.getAsMention() + " You do not own a room!", channel);
		} else if (pingedUsers.size() != 0 && args.length == 2 && (sub.equals("transfer") || sub.equals("t"))) {
			User user = pingedUsers.get(0);
			if (transfer(authorId, user, guild))
				bot.sendMessage(
						author.getAsMention() + " Successfully transferred the room to " + user.getAsMention() + "!",
						channel);
			else
				bot.sendMessage(author.getAsMention() + " You do not own a room or the target user is not in the room!",
						channel);
		} else if (args.length == 1 && (sub.equals("leave") || sub.equals("quit")) || sub.equals("exit")) {
			if (!leave(author, channel, guild))
				bot.sendMessage(author.getAsMention() + " You are not in a multi-live room!", channel);
		} else if (args.length == 1 && (sub.equals("ready") || sub.equals("r"))) {
			toggleReady(channel, author, guild);
		} else if (args.length == 2 && (sub.equals("join") || sub.equals("j"))) {
			BandoriRoom room;
			if (!idCheck(args[1])) {
				if (pingedUsers.size() == 0) {
					bot.sendMessage(author.getAsMention() + " Invalid room ID!", channel);
					return;
				}
				User pinged = pingedUsers.get(0);
				room = getRoomByUser(pinged.getId(), guild);
			} else
				room = getRoomById(args[1], guild);
			if (preJoinCheck(room, author, channel, guild)) {
				bot.sendMessage(author.getAsMention() + " You are already in the room!", channel);
				return;
			}
			if (room == null || !room.join(author, null, channel, this))
				bot.sendMessage(author.getAsMention() + " Failed to join the room!", channel);
		} else {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		updateChannelTopic(guild);
		String emoteName = emote.getName();
		if (emoteName.equals("live_boost")) {
			if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
				return;
			MessageEmbed msgEmbeded = message.getEmbeds().get(0);
			String roomId = msgEmbeded.getFooter().getText();
			if (preJoinCheck(getRoomById(roomId, guild), user, channel, guild))
				return;
			BandoriRoom room = getRoomById(roomId, guild);
			if (!room.join(user, message, channel, this))
				bot.sendMessage(user.getAsMention() + " Failed to join the room!", channel);
		} else if (emoteName.equals(Emojis.CHECK) && reactionIntent.containsKey(message.getId())) {
			// If this reaction is caused by chat intent detection
			ChatIntent intent = reactionIntent.get(message.getId());
			if (intent == null || message.getMentionedMembers().isEmpty())
				return;
			String authorId = user.getId();
			if (!message.getMentionedMembers().get(0).getUser().getId().equals(authorId))
				return;
			reactionIntent.remove(message.getId());
			bot.removeAllReactions(message);

			// Get room ID from intent
			String roomId = (String) intent.getData("ROOM_ID");

			// Create new room using room ID
			if (disband(authorId, guild))
				bot.sendMessage(user.getAsMention() + " Successfully disbanded your previous room!", channel);
			leave(user, channel, guild);

			BandoriRoom room = new BandoriRoom(roomId, user);
			Role readyRole = guild.getRolesByName("Bang Dream", true).get(0);
			bot.sendMessage(readyRole.getAsMention() + " " + user.getAsMention() + " has created a multi-live room!",
					channel);
			message = bot.sendMessage(room.getEmbededMessage(), channel);
			bot.addReaction(message, bot.getEmote(Emotes.LIVE_BOOST));

			HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
			multiRooms.put(authorId, room);
			updateOrCreateServerRooms(guild, multiRooms);

			ReactionDispatcher.register(message, this, 300, "live_boost");
			ReactionDispatcher.registerCleanUp(message, 300);
		} else if (emoteName.equals(Emojis.CROSS) && reactionIntent.containsKey(message.getId())) {
			// If this reaction is caused by chat intent detection
			ChatIntent intent = reactionIntent.get(message.getId());
			if (intent == null || message.getMentionedMembers().isEmpty())
				return;
			String authorId = user.getId();
			if (!message.getMentionedMembers().get(0).getUser().getId().equals(authorId))
				return;
			bot.deleteMessage(message);
		}
	}

	@Override
	public void onIntentDetected(ChatIntent intent, User author, Message message, String messageRaw,
			MessageChannel channel, Guild guild) {
		if (intent == null || intent.getPattern() != ChatIntentPattern.BANDORI_CREATE_ROOM)
			return;
		String roomId = (String) intent.getData("ROOM_ID");
		if (getRoomById(roomId, guild) != null)
			return;
		message = bot.sendMessage(author.getAsMention() + " New Bandori room ID detected, create a new room?", channel);
		bot.addReaction(message, Emojis.CHECK, Emojis.CROSS);
		reactionIntent.put(message.getId(), intent);

		ReactionDispatcher.register(message, this, Emojis.CHECK, Emojis.CROSS);
		ReactionDispatcher.registerCleanUp(message);
	}

	private static int checkDelay = 0;

	public static void autoDisband() {
		if (checkDelay > 0) {
			checkDelay--;
			return;
		}
		checkDelay = 300;

		long now = System.currentTimeMillis();
		HashMap<String, BandoriRoom> toDisband = new HashMap<String, BandoriRoom>();
		for (Entry<String, HashMap<String, BandoriRoom>> guildRooms : multiRooms.entrySet()) {
			String guildId = guildRooms.getKey();
			for (Entry<String, BandoriRoom> roomEntry : guildRooms.getValue().entrySet()) {
				BandoriRoom room = roomEntry.getValue();
				if (room.getLastActiveTime() + BandoriConfig.MULTI_ROOM_TIME_OUT > now)
					continue;
				toDisband.put(guildId, room);
			}
			updateChannelTopic(App.bot.getJDA().getGuildById(guildId));
		}

		for (Entry<String, BandoriRoom> roomEntry : toDisband.entrySet()) {
			Guild guild = App.bot.getJDA().getGuildById(roomEntry.getKey());
			disband(roomEntry.getValue().getCreator().getId(), guild);
		}
	}

	private static HashMap<String, BandoriRoom> getOrCreateServerRooms(Guild guild) {
		if (multiRooms.containsKey(guild.getId()))
			return multiRooms.get(guild.getId());
		return new HashMap<String, BandoriRoom>();
	}

	private static void updateOrCreateServerRooms(Guild guild, HashMap<String, BandoriRoom> data) {
		multiRooms.put(guild.getId(), data);
	}

	private static void updateChannelTopic(final Guild guild) {
		if (!guild.getId().equals("423512363765989378"))
			return;

		App.bot.schedule(new TimerTask() {
			@Override
			public void run() {
				TextChannel tc = App.bot.getJDA().getTextChannelById("605289350447759403");
				StringBuilder sb = new StringBuilder("Multi Rooms (/m r): ");
				HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
				for (BandoriRoom room : multiRooms.values())
					sb.append(room.getId() + " (" + room.getParticipantsDisplayTaskBar(guild) + "), ");
				sb.delete(sb.length() - 2, sb.length());
				tc.getManager().setTopic(sb.substring(0, Math.min(sb.length(), 950)).toString()).queue();
			}
		}, 3000);
	}

	/**
	 * Returns true if the current room is the same as the one that the user is
	 * attempting to join
	 * 
	 * @param targetRoom the BandoriRoom to join
	 * @param author     the user to join
	 * @param channel    the channel to send the response
	 * @param guild      the guild that this happened in
	 * @return True if user is in the target room
	 */
	private boolean preJoinCheck(BandoriRoom targetRoom, User author, MessageChannel channel, Guild guild) {
		BandoriRoom currentRoom = getRoomByUser(author.getId(), guild);
		if (currentRoom == null)
			return false;
		if (currentRoom.getId().equals(targetRoom.getId()))
			return true;

		if (disband(author.getId(), guild))
			bot.sendMessage(author.getAsMention() + " Successfully disbanded your previous room!", channel);
		leave(author, channel, guild);
		return false;
	}

	private boolean leave(User user, MessageChannel channel, Guild guild) {
		BandoriRoom room = getRoomByUser(user.getId(), guild);
		if (room == null)
			return false;
		if (user.getId().equals(room.getCreator().getId())) {
			disband(user.getId(), guild);
			bot.sendMessage(user.getAsMention() + " Successfully disbanded the room!", channel);
			return true;
		}
		if (!room.leave(user, channel))
			return false;
		bot.sendMessage(user.getAsMention() + " Successfully left the room!", channel);
		return true;
	}

	private static boolean disband(String id, Guild guild) {
		HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
		if (!multiRooms.containsKey(id))
			return false;
		multiRooms.remove(id);
		return true;
	}

	private boolean transfer(String id, User user, Guild guild) {
		HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
		if (!multiRooms.containsKey(id))
			return false;
		BandoriRoom room = multiRooms.get(id);
		boolean success = room.transfer(id, user.getId());
		if (success) {
			multiRooms.remove(id);
			multiRooms.put(user.getId(), room);
			updateOrCreateServerRooms(guild, multiRooms);
		}
		return success;
	}

	private BandoriRoom getRoomById(String id, Guild guild) {
		HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
		for (String creatorId : multiRooms.keySet()) {
			BandoriRoom room = multiRooms.get(creatorId);
			if (room.getId().equals(id))
				return room;
		}
		return null;
	}

	private BandoriRoom getRoomByUser(String userId, Guild guild) {
		HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
		for (String creatorId : multiRooms.keySet()) {
			BandoriRoom room = multiRooms.get(creatorId);
			if (room.getParticipants().contains(userId))
				return room;
		}
		return null;
	}

	private MessageEmbed getRoomList(Guild guild) {
		HashMap<String, BandoriRoom> multiRooms = getOrCreateServerRooms(guild);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Multi-Live Rooms");
		builder.setDescription("There are " + multiRooms.size() + " active multi-live rooms");

		int count = 1;
		for (String creatorId : multiRooms.keySet()) {
			BandoriRoom room = multiRooms.get(creatorId);
			builder.addField(new Field(
					"**" + count + ".** " + bot.getUserDisplayName(creatorId, guild) + "'s Room ("
							+ room.getParticipantsCount() + "/" + room.getCapacity() + "): **" + room.getId() + "**",
					"`" + room.getParticipantsDisplay(guild) + "`", false));
			count++;
		}

		builder.addField(new Field("", "Use `/multi join <ID>` to join a room", false));
		return builder.build();
	}

	public static void toggleReady(MessageChannel channel, User user, Guild guild) {
		Role readyRole = guild.getRolesByName("Bang Dream", true).get(0);
		Member userMember = guild.getMember(user);
		List<Member> readyList = guild.getMembersWithRoles(readyRole);
		boolean ready = readyList.contains(userMember);
		ready = toggleReady(user, guild, ready);
		if (ready)
			App.bot.sendMessage(user.getAsMention() + " is ready to multi-live! (" + (readyList.size() + 1) + " total)",
					channel);
		else
			App.bot.sendMessage(user.getAsMention() + " is no longer ready. (" + (readyList.size() - 1) + " total)",
					channel);
	}

	private static boolean toggleReady(User user, Guild guild, boolean ready) {
		Role role = guild.getRolesByName("Bang Dream", true).get(0);
		Member member = guild.getMember(user);
		if (ready) {
			guild.removeRoleFromMember(member, role).queue();
			return false;
		} else {
			guild.addRoleToMember(member, role).queue();
			return true;
		}
	}

	private boolean idCheck(String id) {
		return id.length() == 5 && GeneralTools.isNumeric(id);
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Multi-Live Template");
		builder.setDescription("Use the following template to use the multi-live feature");

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " ready/r",
				"Toggle your \"ready\" status\n"));
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " join/j <ID/@>",
				"Join room by ID or @mention\n"));
		sb.append(
				String.format("%-22s >> %s", BotConfig.PREFIX + command + " leave/quit", "Leave your current room\n"));
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " list/l", "See all active rooms\n"));
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " show/s", "See your current room\n"));
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " ping", "Ping everyone in your room\n"));
		sb.append("```");
		builder.addField(new Field("Regular Commands:", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " create/c <ID>",
				"Create a multi-live room\n"));
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " disband/del", "Disband your room\n"));
		sb.append(String.format("%-22s >> %s", BotConfig.PREFIX + command + " transfer/t <ID>",
				"Transfer room ownership\n"));
		sb.append("```");
		builder.addField(new Field("Room Owner Commands:", sb.toString(), false));

		sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " create 88888```", false));
		return builder.build();
	}
}
