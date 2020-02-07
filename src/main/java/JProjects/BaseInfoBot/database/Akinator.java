package JProjects.BaseInfoBot.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.Akiwrapper.Answer;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Guess;
import com.markozajc.akiwrapper.core.entities.Question;

import JProjects.BaseInfoBot.database.config.AkinatorConfig;
import JProjects.BaseInfoBot.spider.PastebinSpider;
import JProjects.BaseInfoBot.tools.GeneralTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class Akinator {

	private Akiwrapper aw;
	private User user;
	private String messageId;

	private long startTime;
	private boolean isActive;

	private LinkedHashMap<String, Integer> log;
	private ArrayList<String> failedGuesses;
	private int round;
	private boolean isStuck;
	private boolean isGuessing;
	private Question question;
	private List<Guess> guesses;
	private Guess guess;
	private double progress;
	private double gain;

	public Akinator(User user) {
		this(user, false);
	}

	public Akinator(User user, boolean enableNsfw) {
		this.user = user;
		this.startTime = System.currentTimeMillis();
		this.aw = new AkiwrapperBuilder().build();

		this.log = new LinkedHashMap<String, Integer>();
		this.failedGuesses = new ArrayList<String>();
		this.guesses = new ArrayList<Guess>();
		this.round = 1;
		this.isActive = true;
		this.question = aw.getCurrentQuestion();
	}

	// Code: 0=no, 1=prob not, 2=dunno, 3=prob, 4=yes
	// >>>>: 5=guess no, 6=guess yes
	public MessageEmbed next(int answer) throws IOException {
		if (answer == 6) {
			this.isActive = false;
			this.log.put("Guess: " + this.guess.getName() + "(" + this.guess.getDescription() + ")", answer);
			return this.getLoseEmbeded();
		} else if (answer == 5) {
			this.failedGuesses.add(this.guess.getName());
			this.log.put("Guess: " + this.guess.getName() + "(" + this.guess.getDescription() + ")", answer);
		}
		this.isGuessing = false;
		this.round++;
		this.log.put(this.question.getQuestion(), answer);
		this.guesses = refineGuesses(guesses);

		if (!this.isStuck) {
			if (answer <= 4) {
				this.isGuessing = false;
				this.question = this.aw.answerCurrentQuestion(this.getAnswerFromIndex(answer));
				this.guesses = refineGuesses(this.aw.getGuessesAboveProbability(AkinatorConfig.CONFIDENCE));
			}

			if (!this.guesses.isEmpty()) {
				this.isGuessing = true;
				this.guess = this.guesses.get(0);
				this.guesses.remove(0);
				return this.getGuessEmbeded();
			}
		}
		// If no more questions, the Akinator is stuck
		if (this.question == null && !this.isStuck) {
			this.isStuck = true;
			this.isGuessing = true;
			this.guesses = refineGuesses(this.aw.getGuesses()); // get all guesses regardless of probability
		}
		// Handle stuck
		if (this.isStuck) {
			this.isGuessing = true;
			this.guesses = this.refineGuesses(this.guesses);
			if (this.guesses.isEmpty()) {
				this.isActive = false;
				return this.getWinEmbeded(); // return win embeded!
			}

			this.guess = this.guesses.get(0);
			this.guesses.remove(0);
			return this.getGuessEmbeded(); // guesses embeded
		}

		return this.getQuestionEmbeded();
	}

	private List<Guess> refineGuesses(List<Guess> list) {
		if (list.isEmpty())
			return list;
		for (int a = list.size() - 1; a >= 0; a--) {
			Guess guess = list.get(a);
			if (!failedGuesses.contains(guess.getName()))
				continue;
			list.remove(guess);
		}
		return list;
	}

	public MessageEmbed getAnswerEmbeded(int answer) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription("Current Progress: " + GeneralTools.round(this.progress, 2) + "% ("
				+ GeneralTools.signNumber(GeneralTools.round(this.gain, 2)) + "%)");

		builder.addField("**" + this.question.getQuestion() + "**",
				"Your Answer: **" + this.getAnswerStringFromIndex(answer) + "**", false);
		return builder.build();
	}

	public MessageEmbed getQuestionEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		double progression = this.question.getProgression();
		this.gain = progression - this.progress;
		builder.setDescription("Current Progress: " + GeneralTools.round(progression, 2) + "% ("
				+ GeneralTools.signNumber(GeneralTools.round(this.gain, 2)) + "%)");
		this.progress = progression;

		builder.addField("**" + this.question.getQuestion() + "**",
				"***1** = No / **2** = Probably Not / **3** = Don't Know / **4** = Probably / **5** = Yes*", false);
		if (this.messageId != null && !this.messageId.isEmpty())
			builder.setFooter("/aki show " + this.messageId, AkinatorConfig.IMAGE_ICON);
		return builder.build();
	}

	public MessageEmbed getGuessEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription("Confidence: " + GeneralTools.round(this.guess.getProbability() * 100, 2) / 100D + "%");
		if (this.guess.getImage() != null)
			builder.setImage(this.guess.getImage().toString());
		builder.addField("**I think of:**", "**" + this.guess.getName() + "** -- " + this.guess.getDescription(),
				false);
		if (this.messageId != null && !this.messageId.isEmpty())
			builder.setFooter("/aki show " + this.messageId, AkinatorConfig.IMAGE_ICON);
		return builder.build();
	}

	public MessageEmbed getWinEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription("You've beat the Akinator at round " + this.round + "!");
		builder.setImage(AkinatorConfig.IMAGE_WIN);
		builder.setFooter("Log: " + getHistoryUrl(), AkinatorConfig.IMAGE_ICON);
		return builder.build();
	}

	public MessageEmbed getLoseEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription("The Akinator guessed it at round " + this.round + "!");
		if (this.guess.getImage() != null)
			builder.setImage(this.guess.getImage().toString());
		builder.addField("**" + this.guess.getName() + "**", this.guess.getDescription(), false);
		builder.setFooter("Log: " + getHistoryUrl(), AkinatorConfig.IMAGE_ICON);
		return builder.build();
	}

	public MessageEmbed getTimeOutEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(AkinatorConfig.COLOR_EMBEDED);
		builder.setAuthor("Akinator Round " + this.round + " -- " + this.user.getName());
		builder.setDescription(this.user.getName() + " has been disqualified (idle time exceeded "
				+ AkinatorConfig.MAX_IDLE_TIME_SECONDS + " seconds)");
		builder.setFooter("Log: " + getHistoryUrl(), AkinatorConfig.IMAGE_ICON);
		return builder.build();
	}

	public String getHistoryUrl() {
		StringBuilder sb = new StringBuilder("Total Rounds: " + this.getRound());
		int count = 1;
		for (Map.Entry<String, Integer> entry : this.getLog().entrySet()) {
			sb.append("\nRound " + count + ": " + entry.getKey() + " -- " + getAnswerStringFromIndex(entry.getValue()));
			count++;
		}
		return PastebinSpider.uploadText(this.getUser().getAsTag() + "'s Akinator Game", sb.toString());
	}

	private Answer getAnswerFromIndex(int index) {
		switch (index) {
		case 0:
			return Answer.NO;
		case 1:
			return Answer.PROBABLY_NOT;
		case 2:
			return Answer.DONT_KNOW;
		case 3:
			return Answer.PROBABLY;
		case 4:
			return Answer.YES;
		default:
			return null;
		}
	}

	private String getAnswerStringFromIndex(int index) {
		switch (index) {
		case 0:
			return "No";
		case 1:
			return "Probably Not";
		case 2:
			return "Don't Know";
		case 3:
			return "Probably";
		case 4:
			return "Yes";
		case 5:
			return "Incorrect guess";
		case 6:
			return "Correct guess";
		default:
			return "Invalid answer";
		}
	}

	// GENERATED
	public static double getConfidence() {
		return AkinatorConfig.CONFIDENCE;
	}

	public User getUser() {
		return user;
	}

	public long getStartTime() {
		return startTime;
	}

	public boolean isActive() {
		return isActive;
	}

	public Akiwrapper getAw() {
		return aw;
	}

	public LinkedHashMap<String, Integer> getLog() {
		return log;
	}

	public ArrayList<String> getFailedGuesses() {
		return failedGuesses;
	}

	public int getRound() {
		return round;
	}

	public boolean isStuck() {
		return isStuck;
	}

	public boolean isGuessing() {
		return isGuessing;
	}

	public Question getQuestion() {
		return question;
	}

	public Guess getBestGuess() {
		return guess;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setMessageId(String id) {
		this.messageId = id;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setAw(Akiwrapper aw) {
		this.aw = aw;
	}

	public void setLog(LinkedHashMap<String, Integer> log) {
		this.log = log;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public void setStuck(boolean stuck) {
		this.isStuck = stuck;
	}

	public void setGuessing(boolean guessing) {
		this.isGuessing = guessing;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void setBestGuess(Guess bestGuess) {
		this.guess = bestGuess;
	}

}
