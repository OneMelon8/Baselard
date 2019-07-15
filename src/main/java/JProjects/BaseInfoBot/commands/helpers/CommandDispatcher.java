package JProjects.BaseInfoBot.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.StringSimilarity;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandDispatcher {

	public static HashMap<String, String[]> registeredCommands = new HashMap<String, String[]>();
	public static HashMap<String, Command> registeredListeners = new HashMap<String, Command>();

	private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

	public static boolean mute = false;

	/**
	 * Tries to process the command (if registered)
	 * 
	 * @param e - the {@link MessageReceivedEvent} instance
	 */
	public static void fire(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		if (!msg.startsWith(Messages.PREFIX))
			return;
		String[] msgArr = msg.split(" ");
		String userCmd = msgArr[0].substring(Messages.PREFIX.length()).toLowerCase();
		System.out.println(GeneralTools.getTime() + " >> " + e.getAuthor().getAsTag() + " executed " + msg);

		if (mute && userCmd.equalsIgnoreCase("toggle")) {
			registeredListeners.get("toggle").onCommand(e.getAuthor(), userCmd, null, e.getMessage(), e.getChannel());
			return;
		}
		if (mute)
			return;

		String authorId = e.getAuthor().getId();
		int cooldownTime = 1000; // Milliseconds
		if (cooldown.containsKey(authorId)) {
			long msLeft = cooldown.get(authorId) + cooldownTime - System.currentTimeMillis();
			if (msLeft > 0) {
				App.bot.reactClock(e.getMessage());
				return;
			}
		}
		cooldown.put(authorId, System.currentTimeMillis());

		String[] args = Arrays.copyOfRange(msgArr, 1, msgArr.length);

		// Loop thru all the registered commands
		for (String cmd : registeredCommands.keySet()) {
			ArrayList<String> aliases = new ArrayList<String>(Arrays.asList(registeredCommands.get(cmd)));
			if ((userCmd.equals(cmd) || aliases.contains(userCmd)) && !mute) {
				// Dispatch command
				registeredListeners.get(cmd).onCommand(e.getAuthor(), userCmd, args, e.getMessage(), e.getChannel());
				return;
			}
		}
		// Disabled cause.. spam i guess?
		// unknownCommand(userCmd, event);
		App.bot.reactQuestion(e.getMessage());
	}

	public static void unknownCommand(String cmd, User author, String command, String[] args, Message message,
			MessageChannel channel) {
		// No command found! => attempt to auto correct
		Object[] help = helpAttempt(cmd);
		String attempt = (String) help[0];
		double sim = (Double) help[1];
		if (sim >= 0.85) {
			registeredListeners.get(attempt).onCommand(author, command, args, message, channel);
			return;
		}
		// No attempts are valid => send help message
		String helpMsg = Messages.UNKNOWN_COMMAND[new Random().nextInt(Messages.UNKNOWN_COMMAND.length)];
		if (sim >= 1.65) // Disabled this function
			helpMsg = Messages.COMMAND_SUGGESTION[new Random().nextInt(Messages.COMMAND_SUGGESTION.length)].replace("#",
					attempt);
		App.bot.sendMessage(helpMsg, channel);
	}

	/**
	 * Register a command for the bot <br>
	 * Commands that are not registered will not be executed!
	 * 
	 * @param command  - command to register
	 * @param instance
	 */
	public static void register(String command, Command instance) {
		registeredCommands.put(command, new String[0]);
		registeredListeners.put(command, instance);
	}

	/**
	 * Register a command for the bot <br>
	 * Commands that are not registered will not be executed!
	 * 
	 * @param command - command to register
	 * @param aliases - command's aliases
	 */
	public static void register(String command, String[] aliases, Command instance) {
		registeredCommands.put(command, aliases);
		registeredListeners.put(command, instance);

	}

	/**
	 * Using the string similarity function to try to "save" the typo
	 * 
	 * @param userCmd - command that user typed
	 * @return (Object[]) { (String) "corrected" command and (double) similarity }
	 */
	public static Object[] helpAttempt(String userCmd) {
		String bestMatch = null;
		double bestDist = 0;
		for (String cmd : registeredCommands.keySet()) {
			double dist = StringSimilarity.similarity(userCmd, cmd);
			if (dist > bestDist) {
				bestDist = dist;
				bestMatch = cmd;
			}
			for (String alias : registeredCommands.get(cmd)) {
				dist = StringSimilarity.similarity(userCmd, alias);
				if (dist > bestDist) {
					bestDist = dist;
					bestMatch = cmd;
				}
			}
		}
		return new Object[] { bestMatch, bestDist };
	}
}
