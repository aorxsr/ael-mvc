package org.slf4j.impl;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class SimpleLoggerConfig {
    private static Properties PROPERTIES = new Properties();
    DateTimeFormatter dateTimeFormatter = null;
    OutputChoice outputChoice = null;

    public SimpleLoggerConfig() {
    }

    public static void setProperty(String key, String value) {
        PROPERTIES.setProperty(key, value);
    }

    public static void removeProperty(String key) {
        PROPERTIES.remove(key);
    }

    public void init() {
        if (null == this.dateTimeFormatter) {
            String datePattrn = this.getStringProp("org.ael.logger.datePattern", "yyyy-MM-dd hh:mm:ss");
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(datePattrn);
        }

    }

    static int stringToLevel(String levelStr) {
        if ("trace".equalsIgnoreCase(levelStr)) {
            return 0;
        } else if ("debug".equalsIgnoreCase(levelStr)) {
            return 10;
        } else if ("info".equalsIgnoreCase(levelStr)) {
            return 20;
        } else if ("warn".equalsIgnoreCase(levelStr)) {
            return 30;
        } else if ("error".equalsIgnoreCase(levelStr)) {
            return 40;
        } else {
            return "error".equalsIgnoreCase(levelStr) ? 50 : 20;
        }
    }

    public String getStringProp(String name, String defalutValue) {
        Object vol = PROPERTIES.get(name);
        return null == vol ? defalutValue : vol.toString();
    }
}
