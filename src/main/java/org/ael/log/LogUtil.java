package org.ael.log;

import java.util.concurrent.ConcurrentHashMap;

public class LogUtil {
    private static ConcurrentHashMap<String, String> THREAD_NAME_CACHE = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, String> CLASS_NAME_CACHE = new ConcurrentHashMap();
    private static boolean isWindows = System.getProperties().getProperty("os.name").toLowerCase().contains("win");

    public LogUtil() {
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String getThreadName() {
        String threadNme = Thread.currentThread().getName();
        if (THREAD_NAME_CACHE.contains(threadNme)) {
            return (String)THREAD_NAME_CACHE.get(threadNme);
        } else {
            String val = "[ " + padLeft(Thread.currentThread().getName(), 17) + " ] ";
            THREAD_NAME_CACHE.put(threadNme, val);
            return val;
        }
    }

    public static String getShortName(String className) {
        if (CLASS_NAME_CACHE.contains(className)) {
            return (String)CLASS_NAME_CACHE.get(className);
        } else {
            int len = 31;
            StringBuilder shortName = buildShortName(className);
            String val = padLeft(shortName.toString(), len);
            val = val + " : ";
            CLASS_NAME_CACHE.put(className, val);
            return val;
        }
    }

    private static StringBuilder buildShortName(String className) {
        String[] packageNames = className.split("\\.");
        StringBuilder shortName = new StringBuilder();
        int pos = 0;
        String[] var4 = packageNames;
        int var5 = packageNames.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String pkg = var4[var6];
            if (pos != packageNames.length - 1) {
                shortName.append(pkg.charAt(0)).append('.');
            } else {
                shortName.append(pkg);
            }

            ++pos;
        }

        return shortName;
    }

    public static boolean stringIsEmpty(String value) {
        return null == value || value.length() == 0;
    }

    public static boolean stringIsNotEmpty(String value) {
        return null != value && value.length() != 0;
    }

    public static String toLowerCaseFirstOne(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String toUpperCaseFirstOne(String s) {
        return Character.isUpperCase(s.charAt(0)) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
