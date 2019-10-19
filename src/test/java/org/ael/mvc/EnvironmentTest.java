package org.ael.mvc;

import org.ael.mvc.constant.EnvironmentConstant;
import org.junit.Test;

public class EnvironmentTest {

    @Test
    public void initConfig() {
        Environment environment = new Environment();
        environment.put(EnvironmentConstant.ENVIRONMENT_FILE, "application.properties");
        environment.initConfig();

        System.out.println(environment.toString());

    }

}
