package JProjects.BaseInfoBot.commands.bandori;

import java.util.List;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class BandoriReady extends Command {

	public BandoriReady(BaseInfoBot bot) {
		super(bot, "ready", new String[] { "r" }, "Alias for /multi ready command");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length == 0) {
			BandoriMultiLive.toggleReady(channel, author, guild);
			return;
		}
		String sub = args[0];
		if (args.length == 1 && (sub.equals("list") || sub.equals("l") || sub.equals("?"))) {
			Role readyRole = guild.getRolesByName("Bang Dream", true).get(0);
			List<Member> readyList = guild.getMembersWithRoles(readyRole);
			bot.sendMessage(getReadyPeopleEmbeded(readyList, guild), channel);
		} else if (args.length == 1 && (sub.equals("ping") || sub.equals("p"))) {
			Role readyRole = guild.getRolesByName("Bang Dream", true).get(0);
			bot.sendMessage("Ready ping by " + author.getAsMention() + ":\n" + readyRole.getAsMention(), channel);
		} else {
			BandoriMultiLive.toggleReady(channel, author, guild);
			return;
		}
	}

	private MessageEmbed getReadyPeopleEmbeded(List<Member> readyList, Guild guild) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Ready List");
		builder.setDescription("/multi create <Room Code> (/m c 88888)");

		StringBuilder sb = new StringBuilder("*");
		for (Member member : readyList)
			sb.append(bot.getUserDisplayName(member.getUser().getId(), guild) + ", ");
		if (sb.length() != 1)
			sb.delete(sb.length() - 2, sb.length());
		sb.append("*");
		builder.addField(new Field("There are " + readyList.size() + " people who are ready to multi-live",
				sb.toString(), false));
		return builder.build();

	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Ready Template");
		builder.setDescription("Use the following template to toggle your ready status");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		return builder.build();
	}
}
