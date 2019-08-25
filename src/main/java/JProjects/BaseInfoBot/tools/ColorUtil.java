package JProjects.BaseInfoBot.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ColorUtil {

	public static Color getDominantColor(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();

		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				int[] rgbArr = getRGBArr(rgb);
				// Filter out grays....
				if (!isGray(rgbArr)) {
					Integer counter = (Integer) m.get(rgb);
					if (counter == null)
						counter = 0;
					counter++;
					m.put(rgb, counter);
				}
			}
		}
		String colorHex = getMostCommonColour(m);
		return fromHex(colorHex);
	}

	public static Color fromHex(String color) {
		color = color.replace("#", "");
		return new Color(Integer.valueOf(color.substring(0, 2), 16), Integer.valueOf(color.substring(2, 4), 16),
				Integer.valueOf(color.substring(4, 6), 16));
	}

	private static String getMostCommonColour(Map<Integer, Integer> map) {
		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> arg0, Entry<Integer, Integer> arg1) {
				return arg0.getValue().compareTo(arg1.getValue());
			}
		});
		Map.Entry<Integer, Integer> me = (Map.Entry<Integer, Integer>) list.get(list.size() - 1);
		int[] rgb = getRGBArr((Integer) me.getKey());
		return toHexString(rgb[0]) + "" + toHexString(rgb[1]) + "" + toHexString(rgb[2]);
	}

	private static String toHexString(int a) {
		String hex = Integer.toHexString(a);
		if (hex.length() == 1)
			hex = "0" + hex;
		return hex;
	}

	private static int[] getRGBArr(int pixel) {
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return new int[] { red, green, blue };

	}

	private static boolean isGray(int[] rgbArr) {
		int rgDiff = rgbArr[0] - rgbArr[1];
		int rbDiff = rgbArr[0] - rgbArr[2];
		// Filter out black, white and grays...... (tolerance within 10 pixels)
		int tolerance = 10;
		if (rgDiff > tolerance || rgDiff < -tolerance)
			if (rbDiff > tolerance || rbDiff < -tolerance) {
				return false;
			}
		return true;
	}
}