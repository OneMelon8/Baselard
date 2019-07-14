package JProjects.BaseInfoBot.commands.admin;

import java.util.List;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.google.GTranslate;
import JProjects.BaseInfoBot.google.GVision;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TranslateImage extends Command {

	public TranslateImage(BaseInfoBot bot) {
		super(bot, "itranslate", new String[] { "it", "itrans" }, "Image translation using Google API");
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
		String localeTo = "en";
		if (args.length == 2)
			localeTo = args[1];

		List<Attachment> attachments = m.getAttachments();
		if (attachments.isEmpty() || !attachments.get(0).isImage()) {
			bot.sendMessage(getHelpEmbeded(), ch);
			return;
		}
		String url = attachments.get(0).getProxyUrl();

		bot.sendMessage("Let me see what's in this image, hmm...", ch);
		bot.sendThinkingPacket(ch);

		String ocr;
		try {
			ocr = GVision.ocr(url);
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage("Hmm, something went wrong, try again later", ch);
			return;
		}

		bot.sendMessage("Ah ha! Now let me translate it for ya...", ch);
		bot.sendMessage(ocr, ch);
		bot.sendThinkingPacket(ch);

		String translated;
		try {
			translated = GTranslate.translate(ocr, localeTo);
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage("One (or more) of your locale values is incorrect", ch);
			return;
		}
		bot.sendMessage(getTranslatedEmbeded(ocr, translated), ch);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("iTranslation Template");
		builder.setDescription("Use the following template to translate an image");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " [target locale]```", false));
		builder.addField(
				new Field("List of Locales:", "```https://cloud.google.com/translate/docs/languages```", false));
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
