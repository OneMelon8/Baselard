package JProjects.BaseInfoBot.database.config;

import java.awt.Color;

public class AkinatorConfig {
	/*
	 * Logic
	 */
	public static final long MAX_IDLE_TIME_SECONDS = 60; // 60 seconds
	public static final long MAX_IDLE_TIME = MAX_IDLE_TIME_SECONDS * 1000;
	public static final double CONFIDENCE = 0.85; // 0-1

	/*
	 * Embed Messages
	 */
	public static final Color COLOR_EMBEDED = new Color(255, 180, 0);

	/*
	 * Images
	 */
	public static final String IMAGE_ICON = "https://i.imgur.com/PMrYAWe.jpg";
	public static final String IMAGE_WIN = "https://i.imgur.com/PyQiKs0.jpg";

}
