package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.google.GTranslate;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Translate extends CommandHandler {

	public Translate(BaseInfoBot bot) {
		super(bot, "translate", new String[] { "t", "trans" }, "Translate from a language to another");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(message);
			return;
		}
		String msg = String.join(" ", args);
		if (msg.isEmpty()) {
			bot.sendMessage("*insert nothing here*", channel);
			return;
		}

		bot.sendMessage("Let me translate it for ya...", channel);
		bot.sendThinkingPacket(channel);

		String translated;
		try {
			translated = GTranslate.translate(msg, "en");
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.sendMessage("One (or more) of your locale values is incorrect", channel);
			return;
		}
		bot.sendMessage(getTranslatedEmbeded(msg, translated), channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Translation template");
		builder.setDescription("Use the following template to translate to English");
		builder.addField(
				new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " <message...>" + "```", false));
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
