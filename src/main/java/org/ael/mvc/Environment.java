package org.ael.mvc;

import org.ael.mvc.constant.EnvironmentConstant;

import java.util.Properties;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:25
 */
public class Environment {

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

}
