package org.ael.mvc.container;

import cn.hutool.core.util.ClassUtil;
import io.netty.util.internal.StringUtil;
import org.ael.mvc.Ael;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.container.bean.Container;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleContainer {

	private Ael ael;

	public void initConfig(Ael ael) {
		this.ael = ael;
	}

	private Map<String, Container> beans = new ConcurrentHashMap<>(32);

	public void initContainer() {
		String scanPackage = ael.getEnvironment().getString(EnvironmentConstant.SCAN_PACKAGE);
		if (StringUtil.isNullOrEmpty(scanPackage)) {
			Set<Class<?>> classes = ClassUtil.scanPackage(scanPackage);

		}
	}

}
