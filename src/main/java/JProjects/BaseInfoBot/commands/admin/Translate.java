package JProjects.BaseInfoBot.commands.admin;

import java.util.Arrays;

import JProjects.BaseInfoBot.BaseInfoBot;
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

	public Translate(BaseInfoBot bot) {
		super(bot, "translate", new String[] { "t", "trans" }, "Translate from a language to another");
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		User author = e.getAuthor();
		MessageChannel ch = e.getChannel();
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(e.getMessage());
			return;
		}
		Message m = e.getMessage();
		String[] args = m.getContentRaw().split(" ");
		String msg = String.join(" ", Arrays.asList(args).subList(1, args.length));
		if (msg.isEmpty()) {
			bot.sendMessage("*insert nothing here*", ch);
			return;
		}

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
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Translation template");
		builder.setDescription("Use the following template to translate to English");
		builder.addField(
				new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " <message...>" + "```", false));
		return builder.build();
	}

	public MessageEmbed getTranslatedEmbeded(String original, String translated) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Translated Results");
		builder.setDescription("Ehehee here is what I figured out (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
		builder.addField(new Field("Original:", "```" + original + "```", false));
		builder.addField(new Field("Translated:", "```" + translated + "```", false));
		return builder.build();
	}
}
