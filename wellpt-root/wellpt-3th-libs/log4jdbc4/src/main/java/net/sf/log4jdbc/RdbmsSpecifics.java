package net.sf.log4jdbc;

import java.text.SimpleDateFormat;
import java.util.Date;

class RdbmsSpecifics {
	protected static final String dateFormat = "MM/dd/yyyy HH:mm:ss.SSS";

	String formatParameterObject(Object object) {
		if (object == null) {
			return "NULL";
		}
		if ((object instanceof String)) {
			return "'" + escapeString((String) object) + "'";
		}
		if ((object instanceof Date)) {
			return "'" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(object) + "'";
		}
		if ((object instanceof Boolean)) {
			return ((Boolean) object).booleanValue() ? "1" : DriverSpy.DumpBooleanAsTrueFalse ? "false"
					: ((Boolean) object).booleanValue() ? "true" : "0";
		}
		return object.toString();
	}

	String escapeString(String in) {
		StringBuilder out = new StringBuilder();
		int i = 0;
		for (int j = in.length(); i < j; i++) {
			char c = in.charAt(i);
			if (c == '\'') {
				out.append(c);
			}
			out.append(c);
		}
		return out.toString();
	}
}
