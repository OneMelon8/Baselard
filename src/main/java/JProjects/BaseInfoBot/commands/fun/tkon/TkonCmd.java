package JProjects.BaseInfoBot.commands.fun.tkon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import JProjects.BaseInfoBot.BaseInfoBot;
import JProjects.BaseInfoBot.commands.helpers.CommandHandler;
import JProjects.BaseInfoBot.database.config.BotConfig;
import JProjects.BaseInfoBot.database.config.TkonConfig;
import JProjects.BaseInfoBot.database.files.tkon.TkonFileEditor;
import JProjects.BaseInfoBot.database.tkon.TkonEffect;
import JProjects.BaseInfoBot.database.tkon.TkonEffectType;
import JProjects.BaseInfoBot.database.tkon.TkonElement;
import JProjects.BaseInfoBot.database.tkon.TkonEntity;
import JProjects.BaseInfoBot.database.tkon.TkonSkill;
import JProjects.BaseInfoBot.database.tkon.TkonSkillModifier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class TkonCmd extends CommandHandler {

	public static HashMap<String, TkonEntity> toSave = new HashMap<String, TkonEntity>();

	public static ArrayList<String> admins = new ArrayList<String>();

	public TkonCmd(BaseInfoBot bot) {
		super(bot, "tkon", new String[] { "tk", "tko" }, "Raise your own TkON!");
		// TODO: remove this?
		admins.add(""); // XAD
		admins.add(""); // LIB
	}

	/**
	 * Planned commands: <br>
	 * - status: mood/hungry?/exp/level <br>
	 * - stats (combat stats): max hp/max mp/atk/def/crt%/crit/speed <br>
	 * - interactions: feed/play/pet etc? (beard scratches if tater easter egg) <br>
	 * - rename <br>
	 * - battles (pvp pve?) <br>
	 * - buy? shop? get food? <br>
	 * - inventory? check your inv? <br>
	 * 
	 */

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);

		TkonEntity tkon = new TkonEntity(BotConfig.ONE_ID, "tea kay on");
		tkon.addSkill(new TkonSkill("Skill 1", "Skill 1 description", 30, 3, TkonConfig.calcRange(1, true), 1.68, 0.85,
				0.03, Arrays.asList(TkonElement.WATER, TkonElement.EARTH), Arrays.asList(TkonSkillModifier.DESTINY_HIT),
				null, null));
		tkon.addSkill(new TkonSkill("Skill 2", "Skill 2 description", 10, 2, TkonConfig.calcRange(2, true), 1.32, 0.92,
				0.15, Arrays.asList(TkonElement.ICE), null, Arrays.asList(new TkonEffect(TkonEffectType.FROSTBITE, 2)),
				null));
		tkon.addSkill(new TkonSkill("Skill 3", "Skill 3 description", 15, 5, TkonConfig.calcRange(2, false), 3.56, 0.65,
				0.80, Arrays.asList(TkonElement.PLASMA), null, null,
				Arrays.asList(new TkonEffect(TkonEffectType.OVERHEAT, 2))));
		tkon.addSkill(new TkonSkill("Skill 4", "Skill 4 description", 5, 0, TkonConfig.calcRange(1, false), 0.80, 1,
				0.10, Arrays.asList(TkonElement.SPIRIT), null, null, null));

		TkonSkill newSkill = new TkonSkill("Skill 5", "Skill 5 description", 20, 0, TkonConfig.calcRange(1, true), 0.80,
				0.85, 0.10, Arrays.asList(TkonElement.LAVA), null,
				Arrays.asList(new TkonEffect(TkonEffectType.BURN, 3)), null);
		bot.sendMessage(tkon.getEmbededMessage(), channel);
		bot.sendMessage(tkon.getStatsEmbededMessage(), channel);
		bot.sendMessage(tkon.getSkills().get(0).getEmbededMessage(), channel);
		bot.sendMessage(tkon.getNewSkillEmbeded(newSkill), channel);
		bot.sendMessage(tkon.getNewSkillFullEmbeded(newSkill), channel);
		if (tkon.gainExp(100000000))
			bot.sendMessage(tkon.getLevelUpEmbeded(false), channel);
		bot.sendMessage(tkon.getStatsEmbededMessage(), channel);
	}

	public static void saveTkons() {
		for (Entry<String, TkonEntity> entry : toSave.entrySet()) {
			String ownerId = entry.getKey();
			TkonEntity entity = entry.getValue();

			try {
				TkonFileEditor.saveTkonEntity(ownerId, entity);
				toSave.remove(ownerId);
			} catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Unable to save " + ownerId + "'s TkON!! Here it is as a JSON string");
				System.err.println(entity.toString());
			}
		}
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("TkON Template");
		builder.setDescription("Use the following template to tkon tkon tkon");
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + " <Discord ID>```", false));
		return builder.build();
	}
}
