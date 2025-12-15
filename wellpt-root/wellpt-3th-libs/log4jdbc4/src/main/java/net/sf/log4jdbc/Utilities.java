package net.sf.log4jdbc;

public class Utilities {
	public static String rightJustify(int fieldSize, String field) {
		if (field == null) {
			field = "";
		}
		StringBuffer output = new StringBuffer();
		int i = 0;
		for (int j = fieldSize - field.length(); i < j; i++) {
			output.append(' ');
		}
		output.append(field);
		return output.toString();
	}
}
