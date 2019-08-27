package JProjects.BaseInfoBot.commands.bandori;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.bandori.BandoriAttribute;
import JProjects.BaseInfoBot.database.bandori.BandoriCard;
import JProjects.BaseInfoBot.database.bandori.BandoriSkillType;
import JProjects.BaseInfoBot.database.config.BandoriConfig;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.files.assets.ImageAssets;
import JProjects.BaseInfoBot.spider.ImgbbSpider;
import JProjects.BaseInfoBot.spider.bandori.BandoriCardSpider;
import JProjects.BaseInfoBot.tools.ColorUtil;
import JProjects.BaseInfoBot.tools.ImageTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class BandoriUserCards extends CommandHandler {

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
		int band = r.nextInt(6); // 0-5

		BufferedImage icon = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = icon.createGraphics();

		try {
			// LAYER 0 -- User profile picture
			g2d.drawImage(ImageAssets.getImage(ImageAssets.BACKGROUND_CARD), 0, 0, null);

			// LAYER 1 -- User profile picture
			g2d.drawImage(
					ImageTools.getImageFromUrl(user.getAvatarUrl()).getScaledInstance(128, 128, Image.SCALE_DEFAULT), 0,
					0, null);

			// LAYER 2 -- Card Frame
			switch (rarity) {
			case 0:
			case 1:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.SILVER_CARD_FRAME), 0, 0, null);
				break;
			case 2:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.GOLD_CARD_FRAME), 0, 0, null);
				break;
			case 3:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.RAINBOW_CARD_FRAME), 0, 0, null);
				break;
			}

			// LAYER 3 -- Band, attribute, stars
			switch (attr) {
			case PURE:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.ATTR_PURE), 94, 2, null);
				break;
			case COOL:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.ATTR_COOL), 94, 2, null);
				break;
			case HAPPY:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.ATTR_HAPPY), 94, 2, null);
				break;
			case POWER:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.ATTR_POWERFUL), 94, 2, null);
				break;
			}
			switch (band) {
			case 0:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.LOGO_POPPIN_PARTY), 1, 2, null);
				break;
			case 1:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.LOGO_AFTERGLOW), 1, 2, null);
				break;
			case 2:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.LOGO_PASTEL_PALETTES), 1, 2, null);
				break;
			case 3:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.LOGO_ROSELIA), 1, 2, null);
				break;
			case 4:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.LOGO_HELLO_HAPPY_WORLD), 1, 2, null);
				break;
			case 5:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.LOGO_RAISE_A_SUILEN), 1, 2, null);
				break;
			}
			switch (rarity) {
			case 1:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.BANDORI_STAR), 5, 85, null);
			case 0:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.BANDORI_STAR), 5, 104, null);
				break;
			case 3:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.BANDORI_STAR_PREMIUM), 5, 47, null);
			case 2:
				g2d.drawImage(ImageAssets.getImage(ImageAssets.BANDORI_STAR_PREMIUM), 5, 66, null);
				g2d.drawImage(ImageAssets.getImage(ImageAssets.BANDORI_STAR_PREMIUM), 5, 85, null);
				g2d.drawImage(ImageAssets.getImage(ImageAssets.BANDORI_STAR_PREMIUM), 5, 104, null);
				break;
			}

			g2d.dispose();
		} catch (IOException ex) {
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

			card.setPerformance(r.nextInt(BandoriConfig.PERFORMANCE_MAX) + 1);
			card.setTechnique(r.nextInt(BandoriConfig.TECHNIQUE_MAX) + 1);
			card.setVisual(r.nextInt(BandoriConfig.VISUAL_MAX) + 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			card.setSkillName("Discord Power!");
			card.setSkillDesc(
					"Perfect lock (Score up) All GOOD notes turn into PERFECT notes and boosts score of all notes by 20.0% for the next 5 seconds");
			card.setPerformance((int) (BandoriConfig.PERFORMANCE_MAX * 0.8));
			card.setTechnique((int) (BandoriConfig.TECHNIQUE_MAX * 0.6));
			card.setVisual((int) (BandoriConfig.VISUAL_MAX * 0.7));
		}
		// Easter egg?
		if (user.getId().equals("245924814781415424")) {
			card.setSkillName("Gotta go fast!");
			card.setSkillDesc(
					"Temporarily increases beatmap note count by *100.0%* and boosts score of all notes by *50.0%* for the next *5* seconds");
			card.setSkillType(BandoriSkillType.SCORE_UP);
		}

		BufferedImage icon = generateUserCardImage(user);
		if (icon != null)
			card.setColor(ColorUtil.getDominantColor(icon));
		String url = ImgbbSpider.uploadImage(icon);
		if (url == null)
			url = "https://i.imgur.com/vWUaliR.png";
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
