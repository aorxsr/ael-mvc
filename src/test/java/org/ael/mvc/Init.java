package org.ael.mvc;

import org.ael.mvc.annotation.Configuration;
import org.ael.mvc.handler.init.AbstractInitHandler;

@Configuration
public class Init extends AbstractInitHandler {
    @Override
    public void init(Ael ael) {
        System.out.println("a");
    }
}
