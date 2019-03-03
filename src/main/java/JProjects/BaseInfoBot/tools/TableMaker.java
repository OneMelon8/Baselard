package JProjects.BaseInfoBot.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TableMaker {
	private static final String[] tableSymbols = new String[] { "═", "║", "╔", "╗", "╚", "╝", "╠", "╦", "╣", "╩", "╬",
			" ║ " };

	public static String makeDefaultTable(HashMap<String, Number> leftCol, HashMap<String, Number> rightCol) {
		int rows = leftCol.size();
		int[] dataSizes = new int[2];
		Number[] leftValues = leftCol.values().toArray(new Number[rows]);
		Number[] rightValues = rightCol.values().toArray(new Number[rows]);
		for (int a = 0; a < rows; a++) {
			String leftValue = leftValues[a].toString();
			if (leftValue.contains("."))
				leftValue += "%";
			String rightValue = rightValues[a].toString();
			if (rightValue.contains("."))
				rightValue += "%";
			if (leftValue.length() > dataSizes[0])
				dataSizes[0] = leftValue.length();
			if (rightValue.length() > dataSizes[1])
				dataSizes[1] = rightValue.length();
		}

		StringBuilder builder = new StringBuilder("```java\n");
		makeSimpleRow(builder, dataSizes, 2, 7, 3); // Make first row

		String[] leftNames = new String[] { "HP", "ATK", "DEF", "ACC", "EVA", "CNTR" };
		String[] rightNames = new String[] { "CRT%", "CRIT", "STNΩ", "FRZΩ", "SILΩ", "ACDΩ" };
		for (int a = 0; a < leftNames.length; a++) {
			String leftValue = leftCol.get(leftNames[a]).toString();
			if (leftValue.contains("."))
				leftValue += "%";
			String rightValue = rightCol.get(rightNames[a]).toString();
			if (rightValue.contains("."))
				rightValue += "%";
			makeDataRow(builder, leftNames[a], leftValue, rightNames[a], rightValue, dataSizes);

			if (a != leftNames.length - 1)
				makeSimpleRow(builder, dataSizes, 6, 10, 8); // Make separator row
		}

		makeSimpleRow(builder, dataSizes, 4, 9, 5); // Make last row

		builder.append("```");
		return builder.toString();
	}

	public static String makeComparisonTable(ArrayList<HashMap<String, Number>> data,
			ArrayList<HashMap<String, Number>> dataOther) {
		HashMap<String, Number> leftData = data.get(0);
		HashMap<String, Number> leftDataOther = dataOther.get(0);
		HashMap<String, Number> rightData = data.get(1);
		HashMap<String, Number> rightDataOther = dataOther.get(1);

		ArrayList<String> forceInt = new ArrayList<String>(
				Arrays.asList(new String[] { "HP", "ATK", "DEF", "ACC", "EVA" }));
		ArrayList<String> forcePercentage = new ArrayList<String>(
				Arrays.asList(new String[] { "CNTR", "CRT%", "CRIT", "STNΩ", "FRZΩ", "SILΩ", "ACDΩ" }));

		HashMap<String, String> leftFinalData = new HashMap<String, String>();
		for (String key : leftData.keySet())
			leftFinalData.put(key,
					leftData.get(key)
							+ (forcePercentage.contains(key) ? "%" : "") + " (" + getDifference(leftData.get(key),
									leftDataOther.get(key), forceInt.contains(key), forcePercentage.contains(key))
							+ ")");
		HashMap<String, String> rightFinalData = new HashMap<String, String>();
		for (String key : rightData.keySet())
			rightFinalData.put(key,
					rightData.get(key)
							+ (forcePercentage.contains(key) ? "%" : "") + " (" + getDifference(rightData.get(key),
									rightDataOther.get(key), forceInt.contains(key), forcePercentage.contains(key))
							+ ")");

		int[] dataSizes = new int[] { 0, 0 };
		for (String key : leftFinalData.keySet())
			if (leftFinalData.get(key).length() > dataSizes[0])
				dataSizes[0] = leftFinalData.get(key).length();
		for (String key : rightFinalData.keySet())
			if (rightFinalData.get(key).length() > dataSizes[1])
				dataSizes[1] = rightFinalData.get(key).length();

		StringBuilder builder = new StringBuilder("```java\n");
		makeSimpleRow(builder, dataSizes, 2, 7, 3); // Make first row

		String[] leftNames = new String[] { "HP", "ATK", "DEF", "ACC", "EVA", "CNTR" };
		String[] rightNames = new String[] { "CRT%", "CRIT", "STNΩ", "FRZΩ", "SILΩ", "ACDΩ" };
		for (int a = 0; a < leftNames.length; a++) {
			String leftValue = leftFinalData.get(leftNames[a]);
			String rightValue = rightFinalData.get(rightNames[a]);
			makeDataRow(builder, leftNames[a], leftValue, rightNames[a], rightValue, dataSizes);
			if (a != leftNames.length - 1)
				makeSimpleRow(builder, dataSizes, 6, 10, 8); // Make separator row
		}

		makeSimpleRow(builder, dataSizes, 4, 9, 5); // Make last row

		builder.append("```");
		return builder.toString();
	}

	private static void makeSimpleRow(StringBuilder builder, int[] dataSizes, int left, int middle, int right) {
		builder.append(tableSymbols[left]);
		int[] gridSizes = new int[4];
		gridSizes[0] = gridSizes[2] = 4;
		gridSizes[1] = dataSizes[0];
		gridSizes[3] = dataSizes[1];
		for (int size : gridSizes) {
			for (int a = 0; a < size + 2; a++)
				builder.append(tableSymbols[0]);
			builder.append(tableSymbols[middle]);
		}
		builder.deleteCharAt(builder.length() - 1); // Remove last char
		builder.append(tableSymbols[right]);
		builder.append("\n");
	}

	private static void makeDataRow(StringBuilder builder, String leftName, String leftData, String rightName,
			String rightData, int[] dataSizes) {
		builder.append(tableSymbols[1] + " " + extend(leftName, 4) + tableSymbols[11] + extend(leftData, dataSizes[0])
				+ tableSymbols[11] + extend(rightName, 4) + tableSymbols[11] + extend(rightData, dataSizes[1]) + " "
				+ tableSymbols[1] + "\n");
	}

	private static String extend(String s, int len) {
		int amt = len - s.length();
		if (s.length() >= len)
			return s;
		for (int a = 0; a < amt; a++)
			s += " ";
		return s;
	}

	private static String getDifference(Number num, Number num2, boolean forceInt, boolean forcePercentage) {
		String sign = num.doubleValue() > num2.doubleValue() ? "+" : "";
		double diff = Math.round((num.doubleValue() - num2.doubleValue()) * 100) / 100D;
		String diffStr = Double.toString(diff);
		if (forceInt)
			diffStr = Math.round(diff) + "";
		return sign + diffStr + (forcePercentage ? "%" : "");
	}
}
