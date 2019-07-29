package org.ael.mvc;

import lombok.NoArgsConstructor;

import java.util.Properties;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:25
 */
@NoArgsConstructor
public class Environment {

	private Properties properties;


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

}
