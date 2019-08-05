package org.ael.mvc.ioc;

import org.ael.mvc.ioc.bean.Bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomIoc {

	private Map<String, Bean> beans = new ConcurrentHashMap<>(32);

	public void scanLocalCLass() {

	}

}
