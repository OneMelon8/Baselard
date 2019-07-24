package JProjects.BaseInfoBot.commands.bandori;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
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
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

public class BandoriMultiLive extends Command {

	public BandoriMultiLive(BaseInfoBot bot) {
		super(bot, "multi", new String[] { "mul" }, "Parent command for multi-live features");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length == 0) {
			// TODO: in the future maybe GUI?
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}

		String sub = args[0];
		if (args.length == 2 && (sub.equals("create") || sub.equals("c") || sub.equals("host") || sub.equals("h"))) {
			String id = args[1];
			if (id.length() != 5 || !GeneralTools.isNumeric(id)) {
				bot.sendMessage(author.getAsMention() + " Invalid room ID!", channel);
				return;
			}

			BandoriRoom room = new BandoriRoom(args[1], author);
			Role readyRole = guild.getRolesByName("b-r", true).get(0);
			bot.sendMessage(readyRole.getAsMention() + " " + author.getAsMention() + " has created a multi-live room!",
					channel);
			room.sendEmbededMessage(channel);
		} else if (args.length == 1 && (sub.equals("ready") || sub.equals("r"))) {
			toggleReady(message, channel, author, guild);
		} else if (args.length == 2 && (sub.equals("join") || sub.equals("j"))) {
			// TODO: Join the room
		} else {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
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

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Multi-Live Template");
		builder.setDescription("Use the following template to use the multi-live feature");

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command, "Show this message\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " create/c <ID>",
				"Create a multi-live room\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " ready/r",
				"Toggle your \"ready\" status\n"));
		sb.append(String.format("%-20s >> %s", BotConfig.PREFIX + command + " join/j <ID>", "Join a room by its ID\n"));
		sb.append("```");
		builder.addField(new Field("Copy & Paste:", sb.toString(), false));

		sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " host 88888```", false));
		return builder.build();
	}

}
