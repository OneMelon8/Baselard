package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;
import java.util.ArrayList;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.Emojis;
import JProjects.BaseInfoBot.database.bandori.BandoriEvent;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.spider.bandori.BandoriEventSpider;
import JProjects.BaseInfoBot.tools.TimeFormatter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

public class BandoriEvents extends Command implements ReactionEvent {

	public BandoriEvents(BaseInfoBot bot) {
		super(bot, "event", new String[] { "ev" }, "Shows the events that are now live and future events");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		if (args.length == 0) {
			// Query event list
			try {
				message = bot.sendMessage(getEventsEmbeded(1), channel);
				bot.reactPrev(message);
				bot.reactNext(message);
				EmoteDispatcher.register(message, this, "◀", "▶");
				EmoteDispatcher.registerCleanUp(message);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
			}
			return;
		}

		String eventName = String.join(" ", args).trim().toLowerCase();
		if (eventName.equals("c") || eventName.equals("curr") || eventName.equals("current")) {
			// Query current
			try {
				eventName = BandoriEventSpider.queryLatest().getName();
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
				return;
			}
		} else if (eventName.equals("t") || eventName.equals("track") || eventName.equals("tracker")) {
			MessageEmbed msg = BandoriEventSpider.queryEventTracking();
			bot.sendMessage(msg, channel);
			return;
		}

		// Show details on that event
		try {
			bot.sendMessage(BandoriEventSpider.queryEvent(eventName).getEmbededMessages(), channel);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that event, maybe you spelled it wrong?", channel);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
		}
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
			return;
		bot.removeAllReactions(message);

		String emoteName = emote.getName();
		MessageEmbed msgEmbeded = message.getEmbeds().get(0);
		String footer = msgEmbeded.getFooter().getText().replace("Page ", "");
		int page = 1;
		try {
			page = Integer.parseInt(footer);
		} catch (NumberFormatException ex) {
			page = 1;
		}

		if (emoteName.equals("▶")) {
			page++;
		} else if (emoteName.equals("◀")) {
			page = Math.max(page - 1, 0);
		}

		try {
			message = bot.editMessage(message, getEventsEmbeded(page));
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
		}

		bot.reactPrev(message);
		bot.reactNext(message);
		EmoteDispatcher.register(message, this, "◀", "▶");
		EmoteDispatcher.registerCleanUp(message);
	}

	public MessageEmbed getEventsEmbeded(int page) throws IOException {
		ArrayList<BandoriEvent> events = BandoriEventSpider.queryEventList(page);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Event List in Chronological Order");
		builder.setDescription("These events are arranged in chronological order for the Bandori global server");
		for (BandoriEvent event : events) {
			StringBuilder sb = new StringBuilder();
			sb.append("\\" + Emojis.CALENDAR + " " + TimeFormatter.formatCalendar(event.getStartTime()));
			sb.append("\n\\" + Emojis.CLOCK_3 + " " + event.getCountdown(true));
			builder.addField("**" + event.getName() + "**", sb.toString(), false);
		}
		builder.setFooter("Page " + page, "https://cdn.discordapp.com/emojis/432981158670630924.png");
		return builder.build();
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Events Query Template");
		builder.setDescription("Use the following template to run the Bandori event query");
		builder.addField(
				new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " [name/c(urrent)]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + BotConfig.PREFIX + command + " bushido```", false));
		return builder.build();
	}
}
