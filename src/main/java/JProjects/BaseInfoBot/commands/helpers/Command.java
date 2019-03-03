package JProjects.BaseInfoBot.commands.helpers;

import JProjects.BaseInfoBot.Bot;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
	protected String command;
	protected String[] aliases;

	protected String helpMsg;

	protected Bot bot;

	/**
	 * Constructor
	 * 
	 * @param command - the command string
	 * @param helpMsg - the help message
	 */
	public Command(Bot bot, String command, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = new String[0];
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, this);
	}

	/**
	 * Constructor
	 * 
	 * @param command - the command string
	 * @param aliases - aliases to the command
	 * @param helpMsg - the help message
	 */
	public Command(Bot bot, String command, String[] aliases, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = aliases;
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, aliases, this);
	}

	/**
	 * Executes the command
	 */
	public abstract void fire(MessageReceivedEvent event);

	/**
	 * @return (String) help message
	 */
	public String getHelp() {
		return this.helpMsg;
	}

	public abstract MessageEmbed getHelpEmbeded();
}
