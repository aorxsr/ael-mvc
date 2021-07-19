package org.slf4j.impl;

import org.ael.log.Constant;
import org.ael.log.LogUtil;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.impl.OutputChoice.OutputChoiceType;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class SimpleLogger extends MarkerIgnoringBase {
	static final int LOG_LEVEL_TRACE = 0;
	static final int LOG_LEVEL_DEBUG = 10;
	static final int LOG_LEVEL_INFO = 20;
	static final int LOG_LEVEL_WARN = 30;
	static final int LOG_LEVEL_ERROR = 40;
	private static final boolean LOG_FLAG = false;
	private static boolean INITIALIZED = false;
	private static SimpleLoggerConfig SIMPLE_LOGGER_CONFIG = null;
	protected int rootLevel;

	static void lazyInit() {
		if (!INITIALIZED) {
			INITIALIZED = true;
			SIMPLE_LOGGER_CONFIG = new SimpleLoggerConfig();
			SIMPLE_LOGGER_CONFIG.init();
		}
	}

	public SimpleLogger(String name) {
		this.name = name;
		String tempName = name;
		String levelString = null;

		for (int indexOfLastDot = name.length(); levelString == null && indexOfLastDot > -1; indexOfLastDot = tempName.lastIndexOf(".")) {
			tempName = tempName.substring(0, indexOfLastDot);
			levelString = SIMPLE_LOGGER_CONFIG.getStringProp("org.ael.logger." + tempName, (String) null);
		}

		if (null == levelString) {
			this.rootLevel = 20;
		} else {
			this.rootLevel = SimpleLoggerConfig.stringToLevel(levelString);
		}

	}

	public void init() {
		SIMPLE_LOGGER_CONFIG.init();
	}

	private void log(int level, String message, Throwable t) {
		if (this.isLevelEnable(level)) {
			StringBuffer buf = new StringBuffer(message.length() + 110);
			String datetime = this.getDateFormat() + ' ';
			buf.append(datetime);
			buf.append("[ ");
			buf.append((String) Constant.LOG_DESC_MAP.get(level));
			buf.append(" ]");
			buf.append(' ');
			buf.append(LogUtil.getThreadName());
			buf.append(LogUtil.getShortName(this.name));
			buf.append(message);
			System.out.println(buf.toString());
		}
	}

	private void write(StringBuffer buf, Throwable t) {
		if (SIMPLE_LOGGER_CONFIG.outputChoice.outputChoiceType == OutputChoiceType.FILE) {
			if (null != t) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				String stack = " " + sw.toString();
				buf.append(stack);
				System.err.println(buf.toString());
			} else {
				System.out.println(buf.toString());
				System.out.flush();
			}
		} else {
			PrintStream printStream = SIMPLE_LOGGER_CONFIG.outputChoice.getTargetPrintStream();
			printStream.println(buf.toString());
			if (t != null) {
				t.printStackTrace();
				if (SIMPLE_LOGGER_CONFIG.outputChoice.outputChoiceType == OutputChoiceType.FILE) {
					t.printStackTrace(printStream);
				}
			}

			printStream.flush();
		}

	}

	private boolean isLevelEnable(int logLevel) {
		return logLevel >= this.rootLevel;
	}

	@Override
	public boolean isTraceEnabled() {
		return this.isLevelEnable(0);
	}

	@Override
	public void trace(String msg) {
		this.log(0, msg, (Throwable) null);
	}

	@Override
	public void trace(String format, Object arg) {
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
	}

	@Override
	public void trace(String format, Object... arguments) {
	}

	@Override
	public void trace(String msg, Throwable t) {
		this.log(0, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return this.isLevelEnable(10);
	}

	@Override
	public void debug(String msg) {
		this.log(10, msg, (Throwable) null);
	}

	@Override
	public void debug(String format, Object arg) {
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
	}

	@Override
	public void debug(String format, Object... arguments) {
	}

	@Override
	public void debug(String msg, Throwable t) {
		this.log(10, msg, (Throwable) null);
	}

	@Override
	public boolean isInfoEnabled() {
		return this.isLevelEnable(20);
	}

	@Override
	public void info(String msg) {
		this.log(20, msg, (Throwable) null);
	}

	@Override
	public void info(String format, Object arg) {
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
	}

	@Override
	public void info(String format, Object... arguments) {
	}

	@Override
	public void info(String msg, Throwable t) {
		this.log(20, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return this.isLevelEnable(30);
	}

	@Override
	public void warn(String msg) {
		this.log(30, msg, (Throwable) null);
	}

	@Override
	public void warn(String format, Object arg) {
	}

	@Override
	public void warn(String format, Object... arguments) {
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
	}

	@Override
	public void warn(String msg, Throwable t) {
		this.log(30, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return this.isLevelEnable(40);
	}

	@Override
	public void error(String msg) {
		this.log(40, msg, (Throwable) null);
	}

	@Override
	public void error(String format, Object arg) {
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
	}

	@Override
	public void error(String format, Object... arguments) {
	}

	@Override
	public void error(String msg, Throwable t) {
		this.log(40, msg, t);
	}

	public String getDateFormat() {
		return LocalDateTime.now().format(SIMPLE_LOGGER_CONFIG.dateTimeFormatter);
	}
}
