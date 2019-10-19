package org.ael.mvc.enhance;

import java.util.regex.Pattern;

public class EnhanceBuilderTest {

    public static void main(String[] args) {

        Pattern compile = Pattern.compile("[/]");
        System.out.println(compile.matcher("/").matches());

        // A-Za-z0-9 *

        Pattern compile1 = Pattern.compile("[A-Za-z0-9]*");
        System.out.println(compile1.matcher("a0").matches());

    }

}
