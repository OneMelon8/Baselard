package JProjects.BaseInfoBot.commands.bandori;

import java.util.HashMap;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.bandori.BandoriRoom;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class BandoriRole extends CommandHandler {

	public static HashMap<String, BandoriRoom> multiRooms = new HashMap<String, BandoriRoom>();

	public BandoriRole(BaseInfoBot bot) {
		super(bot, "bandori", new String[] { "gbp", "bangdream" }, "Parent command for Bandori features");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
//		String sub = args[0];
		if (args.length == 0) {
			toggleBandori(message, author, guild);
		} else {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
	}

	private void toggleBandori(Message message, User user, Guild guild) {
		Role role = guild.getRolesByName("Bang Dream", true).get(0);
		Member member = guild.getMember(user);
		if (guild.getMembersWithRoles(role).contains(member)) {
			// LEAVE ROLE
			guild.removeRoleFromMember(member, role).queue();
			bot.addReaction(message, bot.getEmote(Emotes.KOKORO_ERROR));
		} else {
			// JOIN ROLE
			guild.addRoleToMember(member, role).queue();
			bot.addReaction(message, bot.getEmote(Emotes.KOKORO_SPARKLE));
		}
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Command Template");
		builder.setDescription("Use the following template to use the Bandori feature");

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-15s >> %s", BotConfig.PREFIX + command, "Join/leave the @Bang Dream role\n"));
		sb.append("```");
		builder.addField(new Field("Regular Commands:", sb.toString(), false));

		sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " meow```", false));
		return builder.build();
	}

}
