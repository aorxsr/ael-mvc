package org.ael;

import org.ael.route.RouteHandler;
import org.junit.Test;

import java.util.regex.Pattern;

public class RouteTest {

    @Test
    public void urlPatternTEst() {
        String pattern = RouteHandler.urlToPattern("/t/?/2");
        System.out.println(pattern);

        Pattern compile = Pattern.compile(pattern);

        System.out.println(compile.matcher("/t/q/2").matches());


    }

}
