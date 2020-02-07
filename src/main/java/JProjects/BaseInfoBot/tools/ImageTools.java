package JProjects.BaseInfoBot.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import JProjects.BaseInfoBot.App;
import JProjects.BaseInfoBot.commands.bandori.BandoriUserCards;
import JProjects.BaseInfoBot.database.files.assets.BandoriImageAssets;

public class ImageTools {

	public static BufferedImage mergeHoriz(String... urlss) throws IOException {
		ArrayList<String> urlList = new ArrayList<String>();
		for (String s : urlss) {
			if (s == null || s.isEmpty())
				continue;
			urlList.add(s);
		}
		int totalWidth = 0;
		int maxHeight = 0;
		BufferedImage[] images = new BufferedImage[urlList.size()];
		for (int a = 0; a < urlList.size(); a++) {
			images[a] = getImageFromUrl(urlList.get(a));
			totalWidth += images[a].getWidth();
			if (maxHeight < images[a].getHeight())
				maxHeight = images[a].getHeight();
		}

		if (totalWidth == 0 || maxHeight == 0)
			return null;

		BufferedImage concatImage = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = concatImage.createGraphics();
		int currentPosition = 0;
		for (int a = 0; a < images.length; a++) {
			int w = images[a].getWidth();
			g2d.drawImage(images[a], currentPosition, 0, null);
			currentPosition += w;
		}
		g2d.dispose();
		return concatImage;
	}

	public static BufferedImage mergeBandoriRoom(ArrayList<String> participants) throws IOException {
		BufferedImage concatImage = new BufferedImage(640, 128, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = concatImage.createGraphics();

		// Create background
		g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BACKGROUND_MULTI_ROOM), 0, 0, null);

		// Create users
		int x = 0;
		for (int a = 0; a < participants.size(); a++) {
			g2d.drawImage(BandoriUserCards.generateUserCardImage(App.bot.getUserById(participants.get(a))), x, 0, null);
			x += 128;
		}

		// Create overlays
		x = 0;
//		g2d.drawImage(ImageAssets.getImage(ImageAssets.RAINBOW_FRAME), x, 0, null);
		x += 128;
		for (int a = 1; a < participants.size(); a++) {
//			g2d.drawImage(ImageAssets.getImage(ImageAssets.GOLD_FRAME), x, 0, null);
			x += 128;
		}
		for (int a = 0; a < 5 - participants.size(); a++) {
			g2d.drawImage(BandoriImageAssets.getImage(BandoriImageAssets.BACKGROUND_NO_USER), x, 0, null);
			x += 128;
		}

		g2d.dispose();
		return concatImage;
	}

	public static File imageToFile(BufferedImage img) throws IOException {
		File f = new File("./temp.png");
		ImageIO.write(img, "png", f);
		return f;
	}

	public static BufferedImage getImageFromUrl(String urlStr) throws IOException {
		final URL url = new URL(urlStr);
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Chrome");
		return ImageIO.read(connection.getInputStream());
	}
}
