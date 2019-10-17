package org.ael.mvc;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.constant.EnvironmentConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:25
 */
@Slf4j
public class Environment {

    private ClassLoader classLoader = Environment.class.getClassLoader();

    private Properties properties = new Properties();

    private Environment() {
        properties.setProperty(EnvironmentConstant.HTTP_ZIP, String.valueOf(false));
    }

    public static Environment of() {
        return new Environment();
    }

    public Environment setProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public boolean isProperty(String name) {
        return properties.containsKey(name);
    }

    public boolean getBoolean(String name) {
        return properties.containsKey(name) ? (boolean) properties.get(name) : false;
    }

    public String getString(String name) {
        return properties.containsKey(name) ? properties.get(name).toString() : null;
    }

    public String getString(String name, String defaultValue) {
        return properties.containsKey(name) ? properties.get(name).toString() : defaultValue;
    }

    public void initConfig() {
        String APPLICATION = properties.getProperty(EnvironmentConstant.ENVIRONMENT_FILE);
        try {
            if (StringUtil.isNullOrEmpty(APPLICATION)) {
                APPLICATION = "application.properties";
                // default load application.properties
                File appFile = new File(APPLICATION);
                if (appFile.exists()) {
                    properties.load(new FileInputStream(appFile));
                } else {
                    InputStream resourceAsStream = classLoader.getResourceAsStream(APPLICATION);
                    if (null != resourceAsStream) {
                        properties.load(resourceAsStream);
                        loadActiveFile(APPLICATION);
                    }
                }
            } else {
				InputStream ins = classLoader.getResourceAsStream(APPLICATION);
				if (null == ins) {
					ins = this.getClass().getResourceAsStream(APPLICATION);
				}
				properties.load(ins);
                loadActiveFile(APPLICATION);
            }
        } catch (IOException e) {
            log.error(" env fileName " + APPLICATION + " not found.");
        }
    }

    private void loadActiveFile(String filePath) throws IOException {
        String son = properties.getProperty(EnvironmentConstant.ACTIVE_NAME);
        if (StringUtil.isNullOrEmpty(son)) {
            return;
        } else {
            // load son file
            String prifix = filePath.substring(0, filePath.lastIndexOf('.'));
            filePath = prifix + "-" + son + ".properties";
            properties.load(classLoader.getResourceAsStream(filePath));
        }
    }

}
