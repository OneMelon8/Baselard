package JProjects.BaseInfoBot.commands.bandori;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriBand;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.database.bandori.BandoriSkillType;
import JProjects.BaseInfoBot.database.config.BandoriConfig;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.files.assets.BandoriImageAssets;
import JProjects.BaseInfoBot.spider.ImgbbSpider;
import JProjects.BaseInfoBot.spider.bandori.BandoriCardSpider;
import JProjects.BaseInfoBot.tools.ColorUtil;
import JProjects.BaseInfoBot.tools.ImageTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class BandoriUserCards extends CommandHandler {

	/**
	 * Random Generation Pattern <br>
	 * 1. Attribute <br>
	 * 2. Rarity <br>
	 * 3. Band <br>
	 * 4. Performance statistics <br>
	 * 5. Technique statistics <br>
	 * 6. Visual statistics <br>
	 */

	public BandoriUserCards(BaseInfoBot bot) {
		super(bot, "ucard", new String[] { "pcard", "mcard" }, "Create a card using the player's data");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		if (args.length > 0 && !message.getMentionedMembers().isEmpty()) {
			User mentioned = message.getMentionedMembers().get(0).getUser();
			bot.sendMessage(generateUserCard(mentioned), channel);
			return;
		}

		bot.sendMessage(generateUserCard(author), channel);
	}

	public static BufferedImage generateUserCardImage(User user) {
		Random r = new Random(user.getIdLong());
		BandoriAttribute attr = BandoriAttribute.fromIndex(r.nextInt(4) + 1); // 1-4
		int rarity = r.nextInt(4); // 0-3
		BandoriBand band = BandoriBand.fromIndex(r.nextInt(6)); // 0-5

		BufferedImage icon = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = icon.createGraphics();

		try {
			// LAYER 0 -- Universal card background (for transparent images)
			g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.CARD_BACKGROUND), 0, 0, null);

			// LAYER 1 -- User profile picture
			g2d.drawImage(
					ImageTools.getImageFromUrl(user.getAvatarUrl()).getScaledInstance(128, 128, Image.SCALE_DEFAULT), 0,
					0, null);

			// LAYER 2 -- Card Frame
			switch (rarity) {
			case 0:
			case 1:
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.CARD_FRAME_SILVER), 0, 0, null);
				break;
			case 2:
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.CARD_FRAME_GOLD), 0, 0, null);
				break;
			case 3:
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.CARD_FRAME_RAINBOW), 0, 0, null);
				break;
			}

			// LAYER 3 -- Band, attribute, star icons
			g2d.drawImage(attr.getBufferedImage(), 94, 2, null);
			g2d.drawImage(band.getBufferedImage(), 1, 2, null);

			switch (rarity) {
			// Normal stars (level 1-2)
			case 1:
				// 2 stars
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BANDORI_STAR), 5, 85, null);
			case 0:
				// 1 star
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BANDORI_STAR), 5, 104, null);
				break;
			// Premium stars (level 3+)
			case 3:
				// 4 stars
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BANDORI_STAR_PREMIUM), 5, 47, null);
			case 2:
				// 3 stars
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BANDORI_STAR_PREMIUM), 5, 66, null);
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BANDORI_STAR_PREMIUM), 5, 85, null);
				g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BANDORI_STAR_PREMIUM), 5, 104, null);
				break;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			g2d.dispose();
		}
		return icon;
	}

	private MessageEmbed generateUserCard(User user) {
		Random r = new Random(user.getIdLong());

		BandoriCard card = new BandoriCard();
		card.setName(user.getAsTag());

		BandoriAttribute attr = BandoriAttribute.fromIndex(r.nextInt(4) + 1); // 1-4
		card.setAttr(attr);

		int rarity = r.nextInt(4); // 0-3
		card.setRarity(rarity + 1);

		card.setVersions("English");

		r.nextInt(6); // Skip to the next number based on seed

		BandoriCard ref;
		try {
			ref = BandoriCardSpider.queryCard("", r.nextInt(200));
			card.setSkillName(ref.getSkillName());
			card.setSkillDesc(ref.getSkillDesc());
			card.setSkillType(ref.getSkillType());
		} catch (Exception ex) {
			ex.printStackTrace();
			card.setSkillName("Discord Power!");
			card.setSkillDesc("Perfect lock (Score up) All GOOD notes turn into PERFECT notes "
					+ "and boosts score of all notes by 20.0% for the next 5 seconds");
		}

		int performance = r.nextInt(BandoriConfig.PERFORMANCE_MAX);
		int technique = r.nextInt(BandoriConfig.TECHNIQUE_MAX);
		int visual = r.nextInt(BandoriConfig.VISUAL_MAX);
		int overall = performance + technique + visual;
		double scale = overall / BandoriConfig.OVERALL_MAX;

		card.setPerformance((int) (performance / (scale > 1 ? scale : 1)));
		card.setTechnique((int) (technique / (scale > 1 ? scale : 1)));
		card.setVisual((int) (visual / (scale > 1 ? scale : 1)));

		// Easter egg?
		if (user.getId().equals(BotConfig.BOT_ID)) {
			card.setAttr(BandoriAttribute.HAPPY);
			card.setRarity(4);
			card.setSkillName("Kono Tensei Base-sama!");
			card.setSkillDesc(
					"Increases beatmap difficulty by *3* levels and boosts score of all notes by *50.0%* for the next *5* seconds");
			card.setSkillType(BandoriSkillType.SCORE_UP);
			card.setPerformance(BandoriConfig.PERFORMANCE_MAX);
			card.setTechnique(BandoriConfig.TECHNIQUE_MAX);
			card.setVisual(BandoriConfig.VISUAL_MAX);
		}

		BufferedImage icon = generateUserCardImage(user);
		if (icon != null)
			card.setColor(ColorUtil.getDominantColor(icon));
		String url = ImgbbSpider.uploadImage(icon);
		if (url == null) {
			url = "https://i.imgur.com/vWUaliR.png"; // Discord icon
			card.setColor(BotConfig.COLOR_MISC);
		}
		card.setIconUrl(url);
		return card.getDetailedEmbededMessage();
	}

	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Bandori Player Card Template");
		builder.setDescription("Use the following template to see what card you are");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		StringBuilder sb = new StringBuilder("```");
		for (String aliase : aliases)
			sb.append(aliase + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append("```");
		builder.addField(new Field("Aliases:", sb.toString(), false));
		return builder.build();
	}

}
