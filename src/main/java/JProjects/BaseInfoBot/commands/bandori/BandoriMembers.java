package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.BotConfig;
import JProjects.BaseInfoBot.spider.bandori.BandoriMemberSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class BandoriMembers extends Command {

	public BandoriMembers(BaseInfoBot bot) {
		super(bot, "member", new String[] { "members" }, "Searches for member list or a specific member");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
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
		builder.setDescription("Poppin' Party, Afterglow, Pastel✽Palettes, Roselia, Hello Happy World");
		builder.addField(new Field(Emotes.POPPIN_PARTY + " **Poppin' Party:**",
				"Kasumi Toyama, Tae Hanazono, Rimi Ushigome, Saaya Yamabuki, Arisa Ichigaya", false));
		builder.addField(new Field(Emotes.AFTERGLOW + " **Afterglow:**",
				"Ran Mitake, Moca Aoba, Himari Uehara, Tomoe Udagawa, Tsugumi Hazawa", false));
		builder.addField(new Field(Emotes.PASTEL_PALETTES + " **PASTEL✽PALETTES:**",
				"Aya Maruyama, Hina Hikawa, Chisato Shirasagi, Maya Yamato, Eve Wakamiya", false));
		builder.addField(new Field(Emotes.ROSELIA + " **Roselia:**",
				"Yukina Minato, Sayo Hikawa, Lisa Imai, Ako Udagawa, Rinko Shirokane", false));
		builder.addField(new Field(Emotes.HELLO_HAPPY_WORLD + " **Hello Happy World:**",
				"Kokoro Tsurumaki, Kaoru Seta, Hagumi Kitazawa, Kanon Matsubara, Misaki Okusawa", false));
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
		builder.addField(new Field("Example:",
				"```" + BotConfig.PREFIX + command + " (shows all members)\n" + BotConfig.PREFIX + command + " Kokoro```",
				false));
		return builder.build();
	}
}
