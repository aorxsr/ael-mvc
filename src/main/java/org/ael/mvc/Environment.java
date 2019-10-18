package org.ael.mvc;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.commons.StringUtils;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.container.ClassPathFileConstant;

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
        try {
            String APPLICATION = properties.getProperty(EnvironmentConstant.ENVIRONMENT_FILE);
            InputStream inputStream;
            if (StringUtils.isEmpty(APPLICATION)) {
                inputStream = ClassPathFileConstant.getClassPathFile("/app.properties");
            } else {
                // 读取配置
                inputStream = ClassPathFileConstant.getClassPathFile(APPLICATION);
            }
            if (null == inputStream) {
                return;
            } else {
                properties.load(inputStream);
            }
            // load active
            readActive();
        } catch (IOException e) {
            log.error("load config fail: {}", e);
        }
    }

    private void readActive() {
        String active = properties.getProperty(EnvironmentConstant.ACTIVE_NAME);
        try {
            InputStream inputStream = null;
            if (StringUtils.isEmpty(active)) {
                return;
            } else {
                // 读取配置
                inputStream = ClassPathFileConstant.getClassPathFile(active);
                if (null == inputStream) {
                    return;
                } else {
                    properties.load(inputStream);
                }
            }
        } catch (IOException e) {
            log.error("load config fail: {}", e);
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
