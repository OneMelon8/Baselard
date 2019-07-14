package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.Arrays;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.spider.bandori.BandoriMemberSpider;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BandoriMembers extends Command {

	public BandoriMembers(BaseInfoBot bot) {
		super(bot, "member", new String[] { "members" }, "Searches for member list or a specific member");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		MessageChannel ch = e.getChannel();
		if (args.length <= 1) {
			bot.sendMessage(getBandsEmbeded(), ch);
			return;
		}

		String search = String.join(" ", Arrays.asList(args).subList(1, args.length));
		try {
			bot.sendMessage(BandoriMemberSpider.queryMember(search, e.getJDA()), ch);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that member, maybe you spelled it wrong?", ch);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like I cannot get the information right now. Check your data and try again later.",
					ch);
		}
	}

	public MessageEmbed getBandsEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
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
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Bandori Member Query Template");
		builder.setDescription("Use the following template to run the Bandori member query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " [name]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:",
				"```" + Messages.PREFIX + command + " (shows all members)" + Messages.PREFIX + command + " kokoro```",
				false));
		return builder.build();
	}
}
