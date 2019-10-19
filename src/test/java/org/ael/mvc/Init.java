package org.ael.mvc;

import org.ael.mvc.annotation.Configuration;
import org.ael.mvc.handler.init.InitHandler;

@Configuration
public class Init extends InitHandler {
    @Override
    public void init(Ael ael) {
        System.out.println("a");
    }
}
