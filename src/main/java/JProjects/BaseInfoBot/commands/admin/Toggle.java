package JProjects.BaseInfoBot.commands.admin;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.JdaEventListener;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

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
		JdaEventListener.mute = !JdaEventListener.mute;
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
