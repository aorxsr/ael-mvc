package org.ael.orm;

public class Utils {
    public static void classForNames(String[] names) {
        for (String name : names) {
            try {
                Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
