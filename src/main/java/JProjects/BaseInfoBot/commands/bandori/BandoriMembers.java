package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.bandori.BandoriBand;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.spider.bandori.BandoriMemberSpider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class BandoriMembers extends CommandHandler {

	public BandoriMembers(BaseInfoBot bot) {
		super(bot, "member", new String[] { "members" }, "Searches for member list or a specific member");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		if (args.length == 0) {
			bot.sendMessage(getBandsEmbeded(), channel);
			return;
		}

		String search = String.join(" ", args);
		try {
			bot.sendMessage(BandoriMemberSpider.queryMember(search, bot.getJDA()), channel);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that member, maybe you spelled it wrong?", channel);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
		}
	}

	public MessageEmbed getBandsEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("All Bands in Bandori");
		builder.setDescription(
				"Poppin' Party, Afterglow, Pastel✽Palettes, Roselia, Hello, Happy World!, Raise A Suilen");

		builder.addField(BandoriBand.POPPIN_PARTY.getBandMembersField());
		builder.addField(BandoriBand.AFTERGLOW.getBandMembersField());
		builder.addField(BandoriBand.PASTEL_PALETTES.getBandMembersField());
		builder.addField(BandoriBand.ROSELIA.getBandMembersField());
		builder.addField(BandoriBand.HELLO_HAPPY_WORLD.getBandMembersField());
		builder.addField(BandoriBand.RAISE_A_SUILEN.getBandMembersField());
		return builder.build();
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Member Query Template");
		builder.setDescription("Use the following template to run the Bandori member query");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " [name]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " (shows all members)\n"
				+ BotConfig.PREFIX + command + " Kokoro```", false));
		return builder.build();
	}
}
