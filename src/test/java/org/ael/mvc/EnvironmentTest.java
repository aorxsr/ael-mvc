package org.ael.mvc;

import org.ael.mvc.constant.EnvironmentConstant;
import org.junit.Test;

public class EnvironmentTest {

    @Test
    public void initConfig() {
        Environment environment = Environment.of();
        environment.setProperty(EnvironmentConstant.ENVIRONMENT_FILE, "application.properties");
        environment.initConfig();

        System.out.println(environment.toString());

    }

}
