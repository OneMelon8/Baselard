package JProjects.BaseInfoBot.commands.bandori;

import java.util.HashMap;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.bandori.BandoriRoom;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

public class BandoriMultiLive extends Command implements ReactionEvent {

	public static HashMap<String, BandoriRoom> multiRooms = new HashMap<String, BandoriRoom>();

	public BandoriMultiLive(BaseInfoBot bot) {
		super(bot, "multi", new String[] { "mul", "m" }, "Parent command for multi-live features");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		if (args.length == 0) {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}

		String sub = args[0];
		String authorId = author.getId();
		if (args.length == 2 && (sub.equals("create") || sub.equals("c") || sub.equals("host"))) {
			if (!idCheck(args[1])) {
				bot.sendMessage(author.getAsMention() + " Invalid room ID!", channel);
				return;
			}

			if (disband(authorId))
				bot.sendMessage(author.getAsMention() + " Successfully disbanded your previous room!", channel);
			leave(author, channel);

			BandoriRoom room = new BandoriRoom(args[1], author);
			Role readyRole = guild.getRolesByName("b-r", true).get(0);
			bot.sendMessage(readyRole.getAsMention() + " " + author.getAsMention() + " has created a multi-live room!",
					channel);
			Message msg = bot.sendMessage(room.getEmbededMessage(), channel);
			bot.addReaction(msg, bot.getEmote(Emotes.LIVE_BOOST));

			multiRooms.put(authorId, room);
			EmoteDispatcher.register(msg, this, "live_boost");
			EmoteDispatcher.registerCleanUp(msg, 180);
		} else if (args.length == 1 && (sub.equals("list") || sub.equals("l") || sub.equals("rooms"))) {
			bot.sendMessage(getRoomList(guild), channel);
		} else if (args.length == 1 && (sub.equals("ping") || sub.equals("mention"))) {
			BandoriRoom room = getRoomByUser(authorId);
			if (room == null) {
				Role readyRole = guild.getRolesByName("b-r", true).get(0);
				bot.sendMessage("Ready ping by " + author.getAsMention() + ":\n" + readyRole.getAsMention(), channel);
				return;
			}
			bot.sendMessage(room.getPingMessage(author), channel);
		} else if (args.length == 1
				&& (sub.equals("disband") || sub.equals("delete") || sub.equals("del") || sub.contentEquals("d"))) {
			if (disband(authorId))
				bot.sendMessage(author.getAsMention() + " Successfully disbanded the room!", channel);
			else
				bot.sendMessage(author.getAsMention() + " You do not own a room!", channel);
		} else if (args.length == 1 && (sub.equals("leave") || sub.equals("quit")) || sub.equals("exit")) {
			if (!leave(author, channel))
				bot.sendMessage(author.getAsMention() + " You are not in a multi-live room!", channel);
		} else if (args.length == 1 && (sub.equals("ready") || sub.equals("r"))) {
			toggleReady(message, channel, author, guild);
		} else if (args.length == 2 && (sub.equals("join") || sub.equals("j"))) {
			if (!idCheck(args[1])) {
				bot.sendMessage(author.getAsMention() + " Invalid room ID!", channel);
				return;
			}
			preJoinCheck(author, channel);
			BandoriRoom room = getRoomById(args[1]);
			if (room == null || !room.join(author, null, channel))
				bot.sendMessage(author.getAsMention() + " Failed to join the room!", channel);
		} else {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
			return;
		String emoteName = emote.getName();
		MessageEmbed msgEmbeded = message.getEmbeds().get(0);
		String roomId = msgEmbeded.getFooter().getText();
		if (emoteName.equals("live_boost")) {
			preJoinCheck(user, channel);
			BandoriRoom room = getRoomById(roomId);
			if (!room.join(user, message, channel))
				bot.sendMessage(user.getAsMention() + " Failed to join the room!", channel);
		}
	}

	private void preJoinCheck(User author, MessageChannel channel) {
		if (disband(author.getId()))
			bot.sendMessage(author.getAsMention() + " Successfully disbanded your previous room!", channel);
		leave(author, channel);
	}

	private boolean leave(User user, MessageChannel channel) {
		BandoriRoom room = getRoomByUser(user.getId());
		if (room == null)
			return false;
		if (user.getId().equals(room.getCreator().getId())) {
			disband(user.getId());
			bot.sendMessage(user.getAsMention() + " Successfully disbanded the room!", channel);
			return true;
		}
		if (!room.leave(user, channel))
			return false;
		bot.sendMessage(user.getAsMention() + " Successfully left the room!", channel);
		return true;
	}

	private boolean disband(String id) {
		if (!multiRooms.containsKey(id))
			return false;
		multiRooms.remove(id);
		return true;
	}

	private BandoriRoom getRoomById(String id) {
		for (String creatorId : multiRooms.keySet()) {
			BandoriRoom room = multiRooms.get(creatorId);
			if (room.getId().equals(id))
				return room;
		}
		return null;
	}

	private BandoriRoom getRoomByUser(String userId) {
		for (String creatorId : multiRooms.keySet()) {
			BandoriRoom room = multiRooms.get(creatorId);
			if (room.getParticipants().contains(userId))
				return room;
		}
		return null;
	}

	private MessageEmbed getRoomList(Guild guild) {
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
					">" + room.getParticipantsDisplay(guild), false));
			count++;
		}

		builder.addField(new Field("", "Use `/multi join <ID>` to join a room", false));
		return builder.build();
	}

	private void toggleReady(Message message, MessageChannel channel, User user, Guild guild) {
		GuildController controller = new GuildController(guild);
		Role readyRole = guild.getRolesByName("b-r", true).get(0);
		Member userMember = guild.getMember(user);
		boolean ready = guild.getMembersWithRoles(readyRole).contains(userMember);
		if (ready) {
			controller.removeSingleRoleFromMember(userMember, readyRole).queue();
			bot.reactCheck(message);
		} else {
			controller.addSingleRoleToMember(userMember, readyRole).queue();
			bot.reactCheck(message);
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
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " ready/r",
				"Toggle your \"ready\" status\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " join/j <ID>", "Join a room by its ID\n"));
		sb.append(
				String.format("%-20s >> %s", BotConfig.PREFIX + command + " leave/quit", "Leave your current room\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " list/l", "See all active rooms\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " ping", "Ping everyone in your room\n"));
		sb.append("```");
		builder.addField(new Field("Regular Commands:", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " create/c <ID>",
				"Create a multi-live room\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " disband/d", "Disband your room\n"));
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
