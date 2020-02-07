package JProjects.BaseInfoBot.commands.admin;

import java.util.List;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.google.GTranslate;
import JProjects.BaseInfoBot.google.GVision;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class TranslateImage extends CommandHandler {

	public TranslateImage(BaseInfoBot bot) {
		super(bot, "itranslate", new String[] { "it", "itrans" }, "Image translation using Google API");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(message);
			return;
		}
		String localeTo = "en";
		if (args.length == 1)
			localeTo = args[0];

		List<Attachment> attachments = message.getAttachments();
		if (attachments.isEmpty() || !attachments.get(0).isImage()) {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
		String url = attachments.get(0).getProxyUrl();

		bot.sendMessage("Let me see what's in this image, hmm...", channel);
		bot.sendThinkingPacket(channel);

		String ocr;
		try {
			ocr = GVision.ocr(url);
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage("Hmm, something went wrong, try again later", channel);
			return;
		}

		bot.sendMessage("Ah ha! Now let me translate it for ya...", channel);
		bot.sendMessage(ocr, channel);
		bot.sendThinkingPacket(channel);

		String translated;
		try {
			translated = GTranslate.translate(ocr, localeTo);
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage("One (or more) of your locale values is incorrect", channel);
			return;
		}
		bot.sendMessage(getTranslatedEmbeded(ocr, translated), channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("iTranslation Template");
		builder.setDescription("Use the following template to translate an image");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " [target locale]```", false));
		builder.addField(
				new Field("List of Locales:", "```https://cloud.google.com/translate/docs/languages```", false));
		return builder.build();
	}

	public MessageEmbed getTranslatedEmbeded(String original, String translated) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Translated Results");
		builder.setDescription("Ehehee here is what I figured out (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
		builder.addField(new Field("Original:", "```" + original + "```", false));
		builder.addField(new Field("Translated:", "```" + translated + "```", false));
		return builder.build();
	}
}
