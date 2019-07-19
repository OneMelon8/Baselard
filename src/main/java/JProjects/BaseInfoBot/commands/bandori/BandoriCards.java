package JProjects.BaseInfoBot.commands.bandori;

import java.io.IOException;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.Command;
import JProjects.BaseInfoBot.commands.helpers.EmoteDispatcher;
import JProjects.BaseInfoBot.commands.helpers.ReactionEvent;
import JProjects.BaseInfoBot.database.Emotes;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.spider.bandori.BandoriCardSpider;
import JProjects.BaseInfoBot.tools.EmbededUtil;
import JProjects.BaseInfoBot.tools.ReactionUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;

public class BandoriCards extends Command implements ReactionEvent {

	public BandoriCards(BaseInfoBot bot) {
		super(bot, "card", new String[] { "cards" }, "Search for cards on bandori.party");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length == 0) {
			try {
				bot.sendMessage(BandoriCardSpider.queryRandom().getEmbededMessage(), channel);
			} catch (IOException ex) {
				ex.printStackTrace();
				bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
			}
			return;
		}

		String query = String.join(" ", args).trim();
		try {
			BandoriCard card = BandoriCardSpider.queryCard(query);
			if (card == null)
				throw new IndexOutOfBoundsException("No results");
			card.setIndex(0);
			card.setNotes(query);
			Message msg = bot.sendMessage(card.getEmbededMessage(), channel);
//			bot.addReaction(msg, bot.getJDA().getEmoteById(Emotes.getId(card.getAttr().getEmote())));
//			bot.addReaction(msg, bot.getJDA().getEmoteById(Emotes.getId(Emotes.getRarityEmote(card.getRarity()))));
			bot.reactPrev(msg);
			bot.reactNext(msg);

			EmoteDispatcher.purgeReactions.put(msg, System.currentTimeMillis() / 1000 + 30);
		} catch (IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			bot.sendMessage("I cannot find information on that card, maybe you spelled it wrong?", channel);
		} catch (IOException ex) {
			ex.printStackTrace();
			bot.sendMessage("Seems like bandori.party cannot be reached right now, try again later.", channel);
		}
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message msg, MessageChannel channel) {
		if (msg.getEmbeds() == null || msg.getEmbeds().size() == 0)
			return;
		bot.removeAllReactions(msg);

		String emoteName = emote.getName();
		MessageEmbed msgEmbeded = msg.getEmbeds().get(0);
		String[] info = msgEmbeded.getFooter().getText().split(" - ");
		String query = info[1];
		int index = Integer.parseInt(info[0].split("/")[0]) - 1;
		int total = Integer.parseInt(info[0].split("/")[1]);
		BandoriAttribute attr = getCurrentAttribute(msg);
		int rarity = getCurrentRarity(msg);

		// Insert "wait" message
		bot.editMessage(msg, EmbededUtil.getThinkingEmbeded());
		try {
			BandoriCard card = null;
			if (emoteName.equals("▶")) {
				index = (index + 1) % total;
				card = customQuery(query, index);
			} else if (emoteName.equals("◀")) {
				index = Math.floorMod((index - 1), total);
				card = customQuery(query, index);
			} else if (emoteName.contains("attr_")) {
				attr = BandoriAttribute.fromIndex(attr.getIndex() + 1);
				card = customQuery(query, attr, rarity, 0);
			} else if (emoteName.contains("bandori_rarity_")) {
				rarity = rarity % 4 + 1;
				card = customQuery(query, attr, rarity, 0);
			}

			if (card == null)
				throw new IndexOutOfBoundsException("No results!");
			msg = bot.editMessage(msg, card.getEmbededMessage());

//			bot.addReaction(msg, bot.getJDA().getEmoteById(Emotes.getId(attr.getEmote())));
//			bot.addReaction(msg, bot.getJDA().getEmoteById(Emotes.getId(Emotes.getRarityEmote(rarity))));
			bot.reactPrev(msg);
			bot.reactNext(msg);

			EmoteDispatcher.purgeReactions.put(msg, System.currentTimeMillis() / 1000 + 30);
		} catch (Exception ex) {
			ex.printStackTrace();
			bot.addReaction(msg, bot.getJDA().getEmoteById(Emotes.getId(Emotes.KOKORON_ERROR)));
		}
	}

	private BandoriCard customQuery(String query, int index) throws IOException {
		BandoriCard card = BandoriCardSpider.queryCard(query, index);
		if (card == null)
			throw new IndexOutOfBoundsException("No results!");
		card.setIndex(index);
		card.setNotes(query);
		return card;
	}

	private BandoriCard customQuery(String query, BandoriAttribute attr, int rarity, int index) throws IOException {
		BandoriCard card = BandoriCardSpider.queryCard(query, attr, rarity, index);
		if (card == null)
			throw new IndexOutOfBoundsException("No results!");
		card.setIndex(index);
		card.setNotes(query);
		return card;
	}

	private int getCurrentRarity(Message msg) {
		if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.BANDORI_RARITY_1)))
			return 1;
		else if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.BANDORI_RARITY_2)))
			return 2;
		else if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.BANDORI_RARITY_3)))
			return 3;
		else if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.BANDORI_RARITY_4)))
			return 4;
		else
			return 0;
	}

	private BandoriAttribute getCurrentAttribute(Message msg) {
		if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.ATTR_POWER)))
			return BandoriAttribute.POWER;
		else if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.ATTR_PURE)))
			return BandoriAttribute.PURE;
		else if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.ATTR_COOL)))
			return BandoriAttribute.COOL;
		else if (ReactionUtil.hasReaction(msg, Emotes.getName(Emotes.ATTR_HAPPY)))
			return BandoriAttribute.HAPPY;
		else
			return null;
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Messages.COLOR_MISC);
		builder.setAuthor("Bandori Card Query Template");
		builder.setDescription("Use the following template to run the Bandori card query");
		builder.addField(new Field("Copy & Paste:", "```" + Messages.PREFIX + command + " [search...]```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		builder.addField(new Field("Example:", "```" + Messages.PREFIX + command + " (shows a random card)\n"
				+ Messages.PREFIX + command + " detective kokoro```", false));
		return builder.build();
	}

}
