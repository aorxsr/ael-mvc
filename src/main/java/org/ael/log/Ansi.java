package org.ael.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ansi {
	public static final String SANE = "\u001b[0m";
	public static final String HIGH_INTENSITY = "\u001b[1m";
	public static final String LOW_INTESITY = "\u001b[2m";
	public static final String ITALIC = "\u001b[3m";
	public static final String UNDERLINE = "\u001b[4m";
	public static final String BLINK = "\u001b[5m";
	public static final String RAPID_BLINK = "\u001b[6m";
	public static final String REVERSE_VIDEO = "\u001b[7m";
	public static final String INVISIBLE_TEXT = "\u001b[8m";
	public static final String BLACK = "\u001b[30m";
	public static final String RED = "\u001b[31m";
	public static final String GREEN = "\u001b[32m";
	public static final String YELLOW = "\u001b[33m";
	public static final String BLUE = "\u001b[34m";
	public static final String MAGENTA = "\u001b[35m";
	public static final String CYAN = "\u001b[36m";
	public static final String WHITE = "\u001b[37m";
	public static final String BACKGROUND_BLACK = "\u001b[40m";
	public static final String BACKGROUND_RED = "\u001b[41m";
	public static final String BACKGROUND_GREEN = "\u001b[42m";
	public static final String BACKGROUND_YELLOW = "\u001b[43m";
	public static final String BACKGROUND_BLUE = "\u001b[44m";
	public static final String BACKGROUND_MAGENTA = "\u001b[45m";
	public static final String BACKGROUND_CYAN = "\u001b[46m";
	public static final String BACKGROUND_WHITE = "\u001b[47m";
	public static final Ansi HighIntensity = new Ansi(new String[]{"\u001b[1m"});
	public static final Ansi Bold;
	public static final Ansi LowIntensity;
	public static final Ansi Normal;
	public static final Ansi Italic;
	public static final Ansi Underline;
	public static final Ansi Blink;
	public static final Ansi RapidBlink;
	public static final Ansi Black;
	public static final Ansi Red;
	public static final Ansi Green;
	public static final Ansi Yellow;
	public static final Ansi Blue;
	public static final Ansi Magenta;
	public static final Ansi Cyan;
	public static final Ansi White;
	public static final Ansi BgBlack;
	public static final Ansi BgRed;
	public static final Ansi BgGreen;
	public static final Ansi BgYellow;
	public static final Ansi BgBlue;
	public static final Ansi BgMagenta;
	public static final Ansi BgCyan;
	public static final Ansi BgWhite;
	private final String[] codes;
	private final String codes_str;

	public Ansi(String... codes) {
		this.codes = codes;
		String _codes_str = "";
		String[] var3 = codes;
		int var4 = codes.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			String code = var3[var5];
			_codes_str = _codes_str + code;
		}

		this.codes_str = _codes_str;
	}

	public Ansi and(Ansi other) {
		List<String> both = new ArrayList();
		Collections.addAll(both, this.codes);
		Collections.addAll(both, other.codes);
		return new Ansi((String[]) both.toArray(new String[0]));
	}

	public String colorize(String original) {
		return this.codes_str + original + "\u001b[0m";
	}

	public String format(String template, Object... args) {
		if (LogUtil.isWindows()) {
			return null != args && args.length != 0 ? String.format(template, args) : template;
		} else {
			String text = null != args && args.length != 0 ? String.format(template, args) : template;
			return this.colorize(text);
		}
	}

	static {
		Bold = HighIntensity;
		LowIntensity = new Ansi(new String[]{"\u001b[2m"});
		Normal = LowIntensity;
		Italic = new Ansi(new String[]{"\u001b[3m"});
		Underline = new Ansi(new String[]{"\u001b[4m"});
		Blink = new Ansi(new String[]{"\u001b[5m"});
		RapidBlink = new Ansi(new String[]{"\u001b[6m"});
		Black = new Ansi(new String[]{"\u001b[30m"});
		Red = new Ansi(new String[]{"\u001b[31m"});
		Green = new Ansi(new String[]{"\u001b[32m"});
		Yellow = new Ansi(new String[]{"\u001b[33m"});
		Blue = new Ansi(new String[]{"\u001b[34m"});
		Magenta = new Ansi(new String[]{"\u001b[35m"});
		Cyan = new Ansi(new String[]{"\u001b[36m"});
		White = new Ansi(new String[]{"\u001b[37m"});
		BgBlack = new Ansi(new String[]{"\u001b[40m"});
		BgRed = new Ansi(new String[]{"\u001b[41m"});
		BgGreen = new Ansi(new String[]{"\u001b[42m"});
		BgYellow = new Ansi(new String[]{"\u001b[43m"});
		BgBlue = new Ansi(new String[]{"\u001b[44m"});
		BgMagenta = new Ansi(new String[]{"\u001b[45m"});
		BgCyan = new Ansi(new String[]{"\u001b[46m"});
		BgWhite = new Ansi(new String[]{"\u001b[47m"});
	}
}