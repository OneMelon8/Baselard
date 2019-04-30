package JProjects.BaseInfoBot.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.database.Messages;
import JProjects.BaseInfoBot.tools.GeneralTools;
import JProjects.BaseInfoBot.tools.StringSimilarity;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandDispatcher {

	public static HashMap<String, String[]> registeredCommands = new HashMap<String, String[]>();
	public static HashMap<String, Command> registeredListeners = new HashMap<String, Command>();

	public static boolean mute = false;

	/**
	 * Tries to process the command (if registered)
	 * 
	 * @param e - the {@link MessageReceivedEvent} instance
	 */
	public static void onCommand(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		if (!msg.startsWith(Messages.prefix))
			return;
		String[] msgArr = msg.split(" ");
		String userCmd = msgArr[0].substring(Messages.prefix.length()).toLowerCase();
		System.out.println(GeneralTools.getTime() + " >> " + e.getAuthor().getAsTag() + " executed " + msg);

		if (mute && userCmd.equalsIgnoreCase("toggle")) {
			registeredListeners.get("toggle").fire(e);
			return;
		}
		if (mute)
			return;

		// Loop thru all the registered commands
		for (String cmd : registeredCommands.keySet()) {
			ArrayList<String> aliases = new ArrayList<String>(Arrays.asList(registeredCommands.get(cmd)));
			if ((userCmd.equals(cmd) || aliases.contains(userCmd)) && !mute) {
				// Dispatch command
				registeredListeners.get(cmd).fire(e);
				return;
			}
		}
		// Disabled cause.. spam i guess?
		// unknownCommand(userCmd, event);
		App.bot.reactQuestion(e.getMessage());
	}

	public static void unknownCommand(String cmd, MessageReceivedEvent event) {
		// No command found! => attempt to auto correct
		Object[] help = helpAttempt(cmd);
		String attempt = (String) help[0];
		double sim = (Double) help[1];
		if (sim >= 0.85) {
			registeredListeners.get(attempt).fire(event);
			return;
		}
		// No attempts are valid => send help message
		String helpMsg = Messages.unknownCommand[new Random().nextInt(Messages.unknownCommand.length)];
		if (sim >= 1.65) // Disabled this function
			helpMsg = Messages.commandSuggestion[new Random().nextInt(Messages.commandSuggestion.length)].replace("#",
					attempt);
		App.bot.sendMessage(helpMsg, event.getChannel());
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
