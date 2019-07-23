package JProjects.BaseInfoBot.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageTools {

	public static File mergeHoriz(String... urlss) throws IOException {
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

		BufferedImage concatImage = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = concatImage.createGraphics();
		int currentPosition = 0;
		for (int a = 0; a < images.length; a++) {
			int w = images[a].getWidth();
			g2d.drawImage(images[a], currentPosition, 0, null);
			currentPosition += w;
		}
		g2d.dispose();

		File f = new File("./temp.png");
		ImageIO.write(concatImage, "png", f);
		return f;
	}

	private static BufferedImage getImageFromUrl(String urlStr) throws IOException {
		final URL url = new URL(urlStr);
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Chrome");
		return ImageIO.read(connection.getInputStream());
	}
}
