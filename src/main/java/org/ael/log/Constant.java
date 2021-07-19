package org.ael.log;

import java.util.HashMap;
import java.util.Map;

public interface Constant {
	String TRACE = "trace";
	String INFO = "info";
	String DEBUG = "debug";
	String WARN = "warn";
	String ERROR = "error";
	String OFF = "error";
	String DATE_TIME_FORMAT_STR_DEFAULT = "yyyy-MM-dd hh:mm:ss";
	Map<Integer, String> LOG_DESC_MAP = new HashMap<Integer, String>() {
		private static final long serialVersionUID = -8216579733086302246L;

		{
			this.put(0, Ansi.White.and(Ansi.Bold).format("TRACE", new Object[0]));
			this.put(10, Ansi.Cyan.and(Ansi.Bold).format("DEBUG", new Object[0]));
			this.put(20, Ansi.Green.and(Ansi.Bold).format(" INFO", new Object[0]));
			this.put(30, Ansi.Yellow.and(Ansi.Bold).format(" WARN", new Object[0]));
			this.put(40, Ansi.Red.and(Ansi.Bold).format("ERROR", new Object[0]));
			this.put(50, "TRACE");
			this.put(60, "DEBUG");
			this.put(70, " INFO");
			this.put(80, " WARN");
			this.put(90, "ERROR");
		}
	};
}
