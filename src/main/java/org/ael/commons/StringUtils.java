package org.ael.commons;

public class StringUtils {

    private static final int ZERO = 0;

    public static boolean isEmpty(String val) {
        return null == val || val.length() == ZERO;
    }

    public static boolean isNotEmpty(String val) {
        return !isEmpty(val);
    }


}
