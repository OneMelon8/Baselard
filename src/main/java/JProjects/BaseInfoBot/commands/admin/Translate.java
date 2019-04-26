package JProjects.BaseInfoBot.commands.admin;

import java.util.Arrays;

import JProjects.BaseInfoBot.Bot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.google.GTranslate;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Translate extends Command {

	public Translate(Bot bot) {
		super(bot, "translate", new String[] { "t", "trans" }, "Developer testing stuff :p");
	}

	// itranslate [to]

	@Override
	public void fire(MessageReceivedEvent e) {
		User author = e.getAuthor();
		MessageChannel ch = e.getChannel();
		if (!Bot.admins.contains(author.getId())) {
			bot.sendMessage(author.getAsMention()
					+ " permission denied :x: (this feature will be premium/limited in the future)", ch);
			return;
		}
		Message m = e.getMessage();
		String[] args = m.getContentRaw().split(" ");
		String msg = String.join(" ", Arrays.asList(args).subList(1, args.length));
		
		bot.sendMessage("Let me translate it for ya...", ch);
		bot.sendThinkingPacket(ch);
		
		String translated;
		try {
			translated = GTranslate.translate(msg, "en");
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage("One (or more) of your locale values is incorrect", ch);
			return;
		}
		bot.sendMessage(getTranslatedEmbeded(msg, translated), ch);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Test template");
		builder.setDescription("You sure you need a template for this?");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.prefix + "test hello world" + "```", false));
		return builder.build();
	}

	public MessageEmbed getTranslatedEmbeded(String original, String translated) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.colorMisc);
		builder.setAuthor("Translated Results");
		builder.setDescription("Ehehee here is what I figured out (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
		builder.addField(new Field("Original:", "```" + original + "```", false));
		builder.addField(new Field("Translated:", "```" + translated + "```", false));
		return builder.build();
	}
}
