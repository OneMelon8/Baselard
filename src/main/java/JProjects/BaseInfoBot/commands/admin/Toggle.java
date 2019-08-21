package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.ChatEventListener;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class Toggle extends CommandHandler {

	public Toggle(BaseInfoBot bot) {
		super(bot, "toggle", "Administration toggle bot responses");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (!BaseInfoBot.admins.contains(author.getId())) {
			bot.reactCross(message);
			return;
		}
		ChatEventListener.mute = !ChatEventListener.mute;
		bot.reactCheck(message);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Toggle Template");
		builder.setDescription("Use the following template to enable/disable the bot");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		return builder.build();
	}
}
